package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.thing.events.ChannelTriggeredEvent;

/**
 * Implements the "do something when a channel event happens" use case.
 * An optional event name can be used to restrict the triggers to only
 * specific events.
 *
 * @author doug
 *
 */
public class ChannelEventRule extends BaseRule {

    /**
     * the channel UID
     */
    final protected String channelUID;

    /**
     * the name of the event to trigger or, or null for all events
     */
    final protected String eventName;

    /**
     * constructor
     *
     * @param channelUID the channel UID
     * @param eventName the name of the event
     */
    public ChannelEventRule(String channelUID, String eventName) {
        this.channelUID = channelUID;
        this.eventName = eventName;
    }

    /**
     * constructor
     *
     * @param channelUID the channel UID
     */
    public ChannelEventRule(String channelUID) {
        this.channelUID = channelUID;
        this.eventName = null;
    }

    /**
     * called when the channel event occurs
     *
     * @param event the ChannelTriggeredEvent
     * @return not used
     */
    protected Object triggered(Object event) {
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
        triggers.add(createChannelEventTrigger(channelUID, eventName));
        return triggers;
    }
}
