package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.Map;

import org.eclipse.smarthome.automation.Action;

/**
 * TODO: This doesn't work. I don't know how to trigger a rule when the system starts.
 *
 * @author doug
 *
 */
public class StartedRule extends BaseRule {

    private long delayMs;
    private boolean started = false;

    public StartedRule() {
    }

    public StartedRule(long delayMs) {
        this.delayMs = delayMs;
    }

    protected void started() {
        logDebug("OpenHAB started");
    }

    public boolean isStarted() {
        return started;
    }

    // TODO: Triggers? I don't know what to put for that...
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            // ItemStateChangedEvent event = (ItemStateChangedEvent) inputs.get("event");
            // logDebug("Event received: " + event.toString());
            inputs.forEach((k, v) -> logWarn("key : " + k + " value : " + v));
            logDebug("Startup event received");
            if (delayMs > 0) {
                logDebug("Sleeping " + delayMs + " ms");
                Thread.sleep(delayMs);
            }
            started = true;
            started();
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }

        return null;
    }

}
