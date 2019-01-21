package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.library.types.OpenClosedType;

/**
 * Implements the "do something when an switch opens or closes" use case.
 *
 * Rule that executes when one or more items change from CLOSED to OPEN or OPEN to CLOSED.
 * It does NOT execute when changing from null to OPEN/CLOSED.
 * open() and closed() take and return an Object to better fit with the Groovy
 * <code>def open(event) { // do something }</code> style
 *
 * @author doug
 *
 */
public class OpenClosedRule extends ItemRule {

    public OpenClosedRule(String itemName) {
        super(itemName);
    }

    public OpenClosedRule(String itemName, String friendlyName) {
        super(itemName, friendlyName);
    }

    public OpenClosedRule(Map<String, String> itemNames) {
        super(itemNames);
    }

    public OpenClosedRule(List<String> items) {
        super(items);
    }

    /**
     * called with an item changes from CLOSED to OPEN
     *
     * @param event the event (ItemStateChangedEvent)
     * @return not used
     */
    protected Object open(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * called with an item changes from OPEN to CLOSED
     *
     * @param event the event (ItemStateChangedEvent)
     * @return not used
     */
    protected Object closed(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * call either open() or closed(), depending upon the new state received
     * in the event, catching all exceptions.
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            ItemStateChangedEvent event = (ItemStateChangedEvent) inputs.get("event");
            logDebug("Event received: " + event.toString());
            OpenClosedType newState = (OpenClosedType) inputs.get("newState");
            if (newState == OpenClosedType.OPEN) {
                open(event);
            } else {
                closed(event);
            }
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

    /**
     * return the triggers required to create the rule, one pair for each item.
     */
    @Override
    public List<Trigger> getTriggers() {
        List<Trigger> triggers = new ArrayList<>();

        for (String itemName : getItemsForTriggers()) {
            triggers.add(createItemStateChangeTrigger(itemName, OpenClosedType.CLOSED, OpenClosedType.OPEN));
            triggers.add(createItemStateChangeTrigger(itemName, OpenClosedType.OPEN, OpenClosedType.CLOSED));
        }
        return triggers;
    }

}
