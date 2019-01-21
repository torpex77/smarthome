package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.thing.events.ChannelTriggeredEvent;

/**
 * Implements the "do something when a channel event happens" use case.
 *
 * TODO: WIP... This needs a lot of cleanup yet, and more testing...
 *
 * @author doug
 *
 */
public class GenericChannelEventRule extends BaseRule {

    // topic is typically "smarthome/channels/*/triggered"
    final protected String eventTopic;

    /**
     * typically the channel UID, like "astro:sun:local:night#event"
     */
    final protected String eventSource;

    public GenericChannelEventRule(String eventSource) {
        this.eventTopic = null;
        this.eventSource = eventSource;
    }

    // TODO: This is confusing with the optional argument first. Fix...
    public GenericChannelEventRule(String eventTopic, String eventSource) {
        this.eventTopic = eventTopic;
        this.eventSource = eventSource;
    }

    /**
     * called when the channel event occurs
     *
     * @param event the ChannelTriggeredEvent
     * @return not used
     */
    protected Object triggered(ChannelTriggeredEvent event) {
        logDebug("triggered()");
        return null;
    }

    /**
     * calls the triggered() method when the configured channel event occurs, trapping all exceptions.
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            logDebug("Event received: " + inputs.get("event").toString());
            ChannelTriggeredEvent event = (ChannelTriggeredEvent) inputs.get("event");
            triggered(event);
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

    /**
     * create the trigger to execute the rule.
     */
    @Override
    public List<Trigger> getTriggers() {
        List<Trigger> triggers = new ArrayList<>();
        if (eventTopic == null) {
            triggers.add(createGenericChannelEventTrigger(eventSource));
        } else {
            triggers.add(createGenericChannelEventTrigger(eventTopic, eventSource));
        }
        return triggers;
    }
}
