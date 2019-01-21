package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;

/**
 * Implements the "do something at a specific time" use case.
 * It takes one or more cron specs and runs the timeFor() method
 * when they are triggered.
 *
 * @author doug
 */
public class TimerRule extends BaseRule {

    /**
     * the cron specifications that trigger the rule
     */
    protected final List<String> cronSpecs;

    /**
     * constructor
     *
     * @param cronSpec the cron spec
     */
    public TimerRule(String cronSpec) {
        List<String> l = new ArrayList<String>();
        l.add(cronSpec);
        cronSpecs = Collections.unmodifiableList(l);
        logDebug("TimerRule(" + cronSpec + ") created");
    }

    /**
     * constructor
     *
     * @param cronSpecs a list of cron specs
     */
    public TimerRule(List<String> cronSpecs) {
        List<String> l = new ArrayList<String>();
        l.addAll(cronSpecs);
        this.cronSpecs = Collections.unmodifiableList(l);
        logDebug("TimerRule(" + cronSpecs + ") created");

    }

    /**
     * called when any of the cron specs are triggered
     *
     * @return not used
     */
    protected Object timeFor() {
        logDebug("Time for event");
        return null;
    }

    /**
     * calls the timeFor() method when the cron expressions trigger the rule,
     * catch all exceptions
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            timeFor();
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

    /**
     * creates the timer triggers needed for the rule
     */
    @Override
    public List<Trigger> getTriggers() {
        logDebug("getTriggers() start");
        List<Trigger> triggers = new ArrayList<>();
        for (String cronSpec : cronSpecs) {
            triggers.add(createTimerTrigger(cronSpec));
        }
        logDebug("getTriggers() end");
        return triggers;
    }

}
