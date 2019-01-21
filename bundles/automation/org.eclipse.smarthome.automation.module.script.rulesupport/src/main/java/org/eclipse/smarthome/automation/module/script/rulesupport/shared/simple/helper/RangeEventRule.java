package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.core.thing.events.ChannelTriggeredEvent;

/**
 * Implements the "do something at the start and end of a range event" use case.
 * The start() method is called when the event starts and the end() method is
 * called when the event ends
 *
 * @author doug
 *
 */
public class RangeEventRule extends ChannelEventRule {

    /**
     * constructor
     *
     * @param channelUID the channel UID.
     */
    public RangeEventRule(String channelUID) {
        super(channelUID);
    }

    /**
     * called when the range event starts
     *
     * @param event the ChannelTriggeredEvent event
     * @return not used
     */
    protected Object start(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * called when the range event ends
     *
     * @param event the ChannelTriggeredEvent event
     * @return not used
     */
    protected Object end(Object event) {
        logDebug(event.toString());
        return null;
    }

    /**
     * calls the start() or end() method when the corresponding event is received.
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            logDebug("Event received: " + inputs.get("event").toString());
            ChannelTriggeredEvent event = (ChannelTriggeredEvent) inputs.get("event");
            // TODO: Probably don't need to pass the event - there is only one possible channel.
            if (event.getEvent().equals("START")) {
                start(event);
            } else {
                end(event);
            }
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

}
