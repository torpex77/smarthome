package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.library.types.DecimalType;

/**
 * Implements the "do something when an item moves out of a normal range" use case.
 * The high and low values are optional, so it can also be used for "do something
 * when an value is too high" and "do something when a value is too low" use cases.
 * The high() method is called when the value exceeds the given high value.
 * The low() method is called when the value exceeds the given low value
 * The backInRange() method is called when the value returns to the normal range
 * and an alert was sent.
 *
 *
 * @author doug
 *
 */
public class RangeRule extends ChangedRule {

    /**
     * the rule will trigger an alert if the item value goes below this value
     */
    final protected Double lowValue;

    /**
     * the rule will trigger an alert if the item value goes below this value
     */
    final protected Double highValue;

    /**
     * true if an alert has been sent, it is used to determine if the backInRange()
     * method should be called.
     */
    protected boolean alertSent = false;

    /**
     * constructor
     *
     * @param items a list of item names to monitor
     * @param lowValue the low threshold of the normal range
     * @param highValue the high threshold of the normal range
     */
    public RangeRule(List<String> items, Double lowValue, Double highValue) {
        super(items);
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    /**
     * constructor
     *
     * @param itemNames map of item names to friendly names
     * @param lowValue the low threshold of the normal range
     * @param highValue the high threshold of the normal range
     */
    public RangeRule(Map<String, String> itemNames, Double lowValue, Double highValue) {
        super(itemNames);
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    /**
     * constructor
     *
     * @param itemName the name of the item
     * @param friendlyName the friendly name of the item
     * @param lowValue the low threshold of the normal range
     * @param highValue the high threshold of the normal range
     */
    public RangeRule(String itemName, String friendlyName, Double lowValue, Double highValue) {
        super(itemName, friendlyName);
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    /**
     * constructor
     *
     * @param itemName the item name to monitor
     * @param lowValue the low threshold of the normal range
     * @param highValue the high threshold of the normal range
     */
    public RangeRule(String itemName, Double lowValue, Double highValue) {
        super(itemName);
        this.lowValue = lowValue;
        this.highValue = highValue;
    }

    /**
     * the item value is lower than the low value threshold
     *
     * @param eventName the ItemStateChangedEvent
     * @return not used
     */
    protected Object low(Object origEvent) {
        ItemStateChangedEvent event = (ItemStateChangedEvent) origEvent;
        if (lowValue != null) {
            logWarn(getFriendlyName(event) + " low: " + ((DecimalType) event.getItemState()).doubleValue());
        }
        return null;
    }

    /**
     * the item value is higher than the high value threshold
     *
     * @param eventName the ItemStateChangedEvent
     * @return not used
     */
    protected Object high(Object origEvent) {
        ItemStateChangedEvent event = (ItemStateChangedEvent) origEvent;
        if (highValue != null) {
            logWarn(getFriendlyName(event) + " high: " + ((DecimalType) event.getItemState()).doubleValue());
        }
        return null;
    }

    /**
     * the item value has returned to the normal range
     *
     * @param eventName the ItemStateChangedEvent
     * @return not used
     */
    protected Object backInRange(Object origEvent) {
        ItemStateChangedEvent event = (ItemStateChangedEvent) origEvent;
        logWarn(getFriendlyName(event) + " normal: " + ((DecimalType) event.getItemState()).doubleValue());
        return null;
    }

    /**
     * Calls the high() or low() method if the value of an item moves outside the configured range.
     * Calls the backInRange() method if the item returns to normal after an alert has been sent.
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            ItemStateChangedEvent event = (ItemStateChangedEvent) inputs.get("event");
            logDebug("Event received: " + event.toString());
            // TODO: Handle NumberType ? Is everything DecimalType?
            if (event.getItemState() instanceof DecimalType) {
                DecimalType state = (DecimalType) event.getItemState();
                double val = state.doubleValue();
                if (lowValue != null && val < lowValue) {
                    alertSent = true;
                    low(event);
                } else if (highValue != null && val > highValue) {
                    alertSent = true;
                    high(event);
                } else {
                    if (alertSent) {
                        alertSent = false;
                        backInRange(event);
                    }
                }
            } else {
                logError(event.getItemName() + " new state is not DecimalType. Was "
                        + event.getItemState().getClass().getName());
            }

        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }
}
