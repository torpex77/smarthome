package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.joda.time.DateTime;

/**
 * Implements the "do something when a door has been left open too long" use case.
 * All constructors take the number of minutes to wait before sending an alert.
 * The alert() method is called when the door has been left open the given number of minutes.
 * The closedAfterAlert() method is called when the door has been closed after an alert
 * has been sent.
 *
 * @author doug
 */
public class OpenTooLongRule extends OpenClosedRule {

    /**
     * sends an alert when the timer executes.
     */
    protected Timer timer;

    /**
     * the number of minutes a switch has to be open before sending
     * an alert.
     */
    final protected int minutes;

    /**
     * lock for protecting from concurrency issues
     */
    final private ReentrantLock eventLock = new ReentrantLock();

    /**
     * true if an alert was triggered. it is reset when the siwtch
     * is closed
     */
    protected boolean alertSent = false;

    /**
     * constructor
     *
     * @param itemNames map of item names to friendly names
     * @param minutes the number of minutes the switch has to remain open before triggering the alert
     */
    public OpenTooLongRule(Map<String, String> itemNames, int minutes) {
        super(itemNames);
        this.minutes = minutes;
    }

    /**
     * constructor
     *
     * @param itemName the name of the item
     * @param friendlyName the friendly name of the item
     * @param minutes the number of minutes the switch has to remain open before triggering the alert
     */
    public OpenTooLongRule(String itemName, String friendlyName, int minutes) {
        super(itemName, friendlyName);
        this.minutes = minutes;
    }

    /**
     * constructor
     *
     * @param itemName the name of the item
     * @param minutes the number of minutes the switch has to remain open before triggering the alert
     */
    public OpenTooLongRule(String itemName, int minutes) {
        super(itemName);
        this.minutes = minutes;
    }

    /**
     * constructor
     *
     * @param items a list of item names to monitor
     * @param minutes the number of minutes the switch has to remain open before triggering the alert
     */
    public OpenTooLongRule(List<String> items, int minutes) {
        super(items);
        this.minutes = minutes;
    }

    /**
     * start a timer that will execute the alert method when it expires.
     *
     * @param eventName the event (ItemStateChangedEvent)
     * @return not used
     */
    @Override
    protected Object open(Object origEvent) {
        eventLock.lock();
        ItemStateChangedEvent event = (ItemStateChangedEvent) origEvent;
        try {
            if (timer != null) { // shouldn't happen unless we somehow get two OPEN events without a CLOSED
                logDebug(getFriendlyName(event) + " running timer cancelled.");
                timer.cancel();
                timer = null;
            }
            logDebug(getFriendlyName(event) + " open. Starting timer for " + minutes + " minutes.");
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    // the door could be closed while we are running, so we need to lock here too.
                    // And make sure we didn't get cancelled while waiting for the lock
                    eventLock.lock();
                    try {
                        if (timer != null) {
                            logDebug("Timer expired. Calling alert()");
                            alertSent = true;
                            alert(event.getItemName());
                            timer = null;
                        } else {
                            logDebug("Timer was cancelled while waiting to execute alert.");
                        }
                    } catch (Exception e) {
                        logError("Exception in alert handler: ", e);
                    } finally {
                        eventLock.unlock();
                    }
                }
            };
            timer = createTimer(new DateTime().plusMinutes(minutes), r);
        } finally {
            eventLock.unlock();
        }
        return null;
    }

    /**
     * cancels any pending alert, or calls the closedAfterAlert method so the
     * to notify that things have returned to normal.
     *
     * @param eventName the event (ItemStateChangedEvent)
     * @return not used
     */
    @Override
    protected Object closed(Object origEvent) {
        eventLock.lock();
        ItemStateChangedEvent event = (ItemStateChangedEvent) origEvent;
        try {
            if (timer != null) {
                logDebug(getFriendlyName(event) + " closed. Timer cancelled.");
                timer.cancel();
                timer = null;
            } else {
                logDebug(getFriendlyName(event) + " closed. No timer running.");
            }
            if (alertSent) {
                alertSent = false;
                closedAfterAlert(event.getItemName());
            }
        } finally {
            eventLock.unlock();
        }
        return null;
    }

    /**
     * called when an item has been open more than the configured time limit
     *
     * @param itemName the name of the item that is open (String)
     */
    protected Object alert(Object itemName) {
        logInfo(getFriendlyName((String) itemName) + " open more than " + minutes + " minutes");
        return null;
    }

    /**
     * called when an item has been closed after an alert has been sent.
     *
     * @param itemName the name of the item that has returned to normal (String)
     */
    protected Object closedAfterAlert(Object itemName) {
        logInfo(getFriendlyName((String) itemName) + " closed.");
        return null;
    }
}
