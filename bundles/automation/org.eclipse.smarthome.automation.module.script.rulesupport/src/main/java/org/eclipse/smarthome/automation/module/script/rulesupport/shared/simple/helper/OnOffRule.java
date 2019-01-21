package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.library.types.OnOffType;

/**
 * Implements the "do something when an item turns on or off" use case.
 *
 * Rule that executes when one or more items change from OFF to ON or ON to OFF.
 * It does NOT execute when changing from null to ON/OFF.
 * on() and off() take and return an Object to better fit with the Groovy
 * <code>def on(event) { // do something }</code> style
 *
 * @author doug
 */
public class OnOffRule extends ItemRule {

    public OnOffRule(String itemName) {
        super(itemName);
    }

    public OnOffRule(String itemName, String friendlyName) {
        super(itemName, friendlyName);
    }

    public OnOffRule(Map<String, String> itemNames) {
        super(itemNames);
    }

    public OnOffRule(List<String> items) {
        super(items);
    }

    /**
     * called with an item changes from OFF to ON
     *
     * @param event the event (ItemStateChangedEvent)
     * @return not used
     */
    protected Object on(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * called with an item changes from ON to OFF
     *
     * @param event the event (ItemStateChangedEvent)
     * @return not used
     */
    protected Object off(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * call either on() or off(), depending upon the new state received
     * in the event, catching all exceptions.
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            ItemStateChangedEvent event = (ItemStateChangedEvent) inputs.get("event");
            logDebug("Event received: " + event.toString());
            OnOffType newState = (OnOffType) inputs.get("newState");
            if (newState == OnOffType.OFF) {
                off(event);
            } else {
                on(event);
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
            triggers.add(createItemStateChangeTrigger(itemName, OnOffType.OFF, OnOffType.ON));
            triggers.add(createItemStateChangeTrigger(itemName, OnOffType.ON, OnOffType.OFF));
        }
        return triggers;
    }
}
