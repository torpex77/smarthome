package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

/**
 * Implements the "turn a switch on when a range event starts and turn it off
 * when it ends" use case.
 *
 * @author doug
 *
 */
public class RangeEventSwitchRule extends RangeEventRule {

    /**
     * the name of the switch to change
     */
    protected final String switchName;

    /**
     * constructor
     * 
     * @param channelUID the channel UID
     * @param switchName the switch name
     */
    public RangeEventSwitchRule(String channelUID, String switchName) {
        super(channelUID);
        this.switchName = switchName;
    }

    /**
     * turns the switch on
     */
    @Override
    protected Object start(Object event) {
        sendCommand(switchName, "ON");
        return null;
    }

    /**
     * turns the switch off
     */
    @Override
    protected Object end(Object event) {
        sendCommand(switchName, "OFF");
        return null;
    }
}
