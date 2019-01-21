package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.items.events.ItemStateEvent;

/**
 * Implements the "do something when an item is updated" use case.
 * The updated() method is called when an item is updated.
 *
 * @author doug
 *
 */
public class UpdatedRule extends ItemRule {

    public UpdatedRule(Map<String, String> itemNames) {
        super(itemNames);
    }

    public UpdatedRule(String itemName, String friendlyName) {
        super(itemName, friendlyName);
    }

    public UpdatedRule(String itemName) {
        super(itemName);
    }

    public UpdatedRule(List<String> items) {
        super(items);
    }

    /**
     * executes when an item is updated
     *
     * @param event the ItemStateEvent
     * @return not used
     */
    protected Object updated(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * call the updated() method when an item is updated, handling all exceptions
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            ItemStateEvent event = (ItemStateEvent) inputs.get("event");
            logDebug("Event received: " + event.toString());
            updated(event);
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

    /**
     * return the triggers required to create the rule, triggering for any state update for the item(s)
     */
    @Override
    public List<Trigger> getTriggers() {
        List<Trigger> triggers = new ArrayList<>();

        for (String itemName : getItemsForTriggers()) {
            triggers.add(createItemStateUpdateTrigger(itemName));
        }
        return triggers;
    }

}
