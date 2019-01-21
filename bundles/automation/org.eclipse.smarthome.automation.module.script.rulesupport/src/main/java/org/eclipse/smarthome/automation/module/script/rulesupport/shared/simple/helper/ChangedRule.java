package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;

/**
 * Implements the "do something when an item changes" use case.
 * The changed() method is called when the item(s) change.
 *
 * @author doug
 *
 */
public class ChangedRule extends ItemRule {

    public ChangedRule(List<String> items) {
        super(items);
    }

    public ChangedRule(Map<String, String> itemNames) {
        super(itemNames);
    }

    public ChangedRule(String itemName, String friendlyName) {
        super(itemName, friendlyName);
    }

    public ChangedRule(String itemName) {
        super(itemName);
    }

    /**
     * executes when an item changes state
     *
     * @param event the ItemStateChangedEvent
     * @return not used
     */
    protected Object changed(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * call the changed() method when the item changes state, handling all exceptions
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            ItemStateChangedEvent event = (ItemStateChangedEvent) inputs.get("event");
            logDebug("Event received: " + event.toString());
            changed(event);
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

    /**
     * return the triggers required to create the rule, triggering for any state change of the item
     */
    @Override
    public List<Trigger> getTriggers() {
        List<Trigger> triggers = new ArrayList<>();

        for (String itemName : getItemsForTriggers()) {
            triggers.add(createItemStateChangeTrigger(itemName));
        }
        return triggers;
    }

}
