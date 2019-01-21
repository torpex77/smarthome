package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.automation.core.util.TriggerBuilder;
import org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.SimpleRule;
import org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.SimpleRuleActionHandler;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.ItemNotUniqueException;
import org.eclipse.smarthome.core.items.ItemRegistry;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.model.script.ScriptServiceUtil;
import org.eclipse.smarthome.model.script.actions.BusEvent;
import org.eclipse.smarthome.model.script.engine.action.ActionService;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class to make it easier to write Groovy rules. It contains helper methods to simplify
 * common tasks such as logging, bus interaction (sendCommand and postUpdate), getting items
 * from the item repository, creating triggers, getting OH actions, creating timers, and
 * a few other miscellaneous things.
 *
 * @author doug
 */
abstract public class BaseRule extends SimpleRule implements SimpleRuleActionHandler {

    /**
     * create a logger for all user defined rules
     */
    protected Logger logger = LoggerFactory.getLogger(getLoggerName());

    /**
     * get the name for the logger
     *
     * @return the name of the class, or the name of the superclass if it is anonymous
     */
    protected String getLoggerName() {
        if (getClass().getSimpleName().equals("")) { // is anonymous class
            return "UserRules." + getClass().getSuperclass().getSimpleName();
        } else {
            return "UserRules." + getClass().getSimpleName();
        }
    }

    /**
     * hold the item registry. It would usually only be used by the getItem methods in this class
     */
    protected ItemRegistry ir = ScriptServiceUtil.getItemRegistry();

    public BaseRule() {
        super();
    }

    // methods from BusEvent for sending commands
    static public void sendCommand(Item item, String commandString) {
        BusEvent.sendCommand(item, commandString);
    }

    static public void sendCommand(String itemName, String commandString) {
        BusEvent.sendCommand(itemName, commandString);
    }

    static public void sendCommand(Item item, Number number) {
        BusEvent.sendCommand(item, number);
    }

    static public void sendCommand(Item item, Command command) {
        BusEvent.sendCommand(item, command);
    }

    // methods from BusEvent for posting updates
    static public void postUpdate(Item item, Number state) {
        BusEvent.postUpdate(item, state);
    }

    static public void postUpdate(Item item, String stateAsString) {
        BusEvent.postUpdate(item, stateAsString);
    }

    static public void postUpdate(Item item, State state) {
        BusEvent.postUpdate(item, state);
    }

    static public void postUpdate(String itemName, String stateString) {
        BusEvent.postUpdate(itemName, stateString);
    }

    // methods that get an item or items using the item repository
    protected Item getItem(String itemName) throws ItemNotFoundException {
        return ir.getItem(itemName);
    }

    protected Item getItemByPattern(String pattern) throws ItemNotFoundException, ItemNotUniqueException {
        return ir.getItemByPattern(pattern);
    }

    protected Collection<Item> getItems() {
        return ir.getItems();
    }

    protected Collection<Item> getItems(String pattern) {
        return ir.getItems(pattern);
    }

    protected void logError(String format, Object... args) {
        logger.error(format, args);
    }

    protected void logWarn(String format, Object... args) {
        logger.warn(format, args);
    }

    protected void logInfo(String format, Object... args) {
        logger.info(format, args);
    }

    protected void logDebug(String format, Object... args) {
        logger.debug(format, args);
    }

    // methods that log a message and update a corresponding item with it as well
    protected void alertMsg(String msg) {
        logWarn(msg);
        postUpdate("AlertMsg", msg);
    }

    protected void warnMsg(String msg) {
        logWarn(msg);
        postUpdate("WarnMsg", msg);
    }

    protected void infoMsg(String msg) {
        logInfo(msg);
        postUpdate("InfoMsg", msg);
    }

    protected void debugMsg(String msg) {
        logDebug(msg);
        postUpdate("DebugMsg", msg);
    }

    /**
     * Toggle an item between ON and OFF. Works for both switches and dimmers.
     *
     * @param itemName the item name
     * @throws ItemNotFoundException the item does not exist
     */
    protected void toggle(String itemName) throws ItemNotFoundException {
        logDebug("toggle(" + itemName + ") start");
        Item item = getItem(itemName);
        if (item.getState() instanceof org.eclipse.smarthome.core.library.types.PercentType) {
            // dimmer
            PercentType pt = (PercentType) item.getState();
            if (pt.intValue() > 0) {
                sendCommand(itemName, "0");
            } else {
                sendCommand(itemName, "100");
            }
        } else if (item.getState() instanceof org.eclipse.smarthome.core.library.types.OnOffType) {
            // switch
            OnOffType type = (OnOffType) item.getState();
            if (type == OnOffType.ON) {
                sendCommand(itemName, "OFF");
            } else {
                sendCommand(itemName, "ON");
            }

        } else {
            logError(itemName + " toggle ignored. State is type " + item.getState().getClass().getName());
            logError("item = " + item);
            logError("state = " + item.getState());
        }
        logDebug("toggle(" + itemName + ") end");
    }

    /**
     * Is the item on? Works with both switches and dimmers
     *
     * @param itemName the item name
     * @return true if the switch is on or the dimmer is non-zero
     * @throws ItemNotFoundException the item doesn't exist
     */
    protected boolean isOn(String itemName) throws ItemNotFoundException {
        Item item = getItem(itemName);
        if (item.getState() instanceof org.eclipse.smarthome.core.library.types.PercentType) {
            // dimmer
            PercentType pt = (PercentType) item.getState();
            if (pt.intValue() > 0) {
                return true;
            } else {
                return false;
            }
        } else if (item.getState() instanceof org.eclipse.smarthome.core.library.types.OnOffType) {
            // switch
            OnOffType type = (OnOffType) item.getState();
            return type == OnOffType.ON;
        } else {
            return false;
        }
    }

    /**
     * create a item state change trigger for any state change
     *
     * @param itemName the item name
     * @return the trigger
     */
    protected Trigger createItemStateChangeTrigger(String itemName) {
        return createItemStateChangeTrigger(itemName, null, null);
    }

    /**
     * create a trigger than fires the rule when an item changes from one state to another.
     *
     * @param itemName      the item name
     * @param previousState the old state (can be null)
     * @param state         the new state (can be null)
     * @return the trigger
     */
    protected Trigger createItemStateChangeTrigger(String itemName, State previousState, State state) {
        Configuration config = new Configuration();
        config.put("itemName", itemName);

        if (previousState != null) {
            config.put("previousState", previousState.toFullString()); // have to use string, not state
        }
        if (state != null) {
            config.put("state", state.toFullString());
        }

        String triggerName;
        if (state == null) {
            triggerName = itemName + "_CHG";
        } else {
            triggerName = itemName + state.toFullString();
        }
        return buildTrigger(triggerName, "core.ItemStateChangeTrigger", config);
    }

    /**
     * create a trigger that fires the rule when an item state is updated
     *
     * @param itemName the item name
     * @return the trigger
     */
    protected Trigger createItemStateUpdateTrigger(String itemName) {
        Configuration config = new Configuration();
        config.put("itemName", itemName);
        return buildTrigger(itemName + "_UPD", "core.ItemStateUpdateTrigger", config);
    }

    /**
     * create a trigger than fires the rule based on a cron expression
     *
     * @param cronExpression the cron expression
     * @return the trigger
     */
    protected Trigger createTimerTrigger(String cronExpression) {
        logDebug("createTimerTrigger(" + cronExpression + ") start");
        Configuration config = new Configuration();
        config.put("cronExpression", cronExpression);
        return buildTrigger("Timer", "timer.GenericCronTrigger", config);
    }

    /**
     * Create a timer and schedule a task to run. It emulates the createTimer()
     * method in OpenHab 1.
     * TODO: This is very much a work in progress..
     *
     * @param when when the task should be executed
     * @param r    the code to execute
     * @return the created and running timer.
     */
    protected Timer createTimer(DateTime when, Runnable r) {
        Timer t = new Timer();
        final TimerTask tt = new TimerTask() {
            @Override
            public void run() {
                r.run();
            }
        };
        // joda DateTime.toDate() is blocked by osgi... do this the hard way for now
        org.joda.time.format.DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        String dtStr = fmt.print(when);
        logDebug("Setting timer set for " + dtStr);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate;
        try {
            startDate = df.parse(dtStr);
            t.schedule(tt, startDate);
        } catch (ParseException e) {
            // shoudln't happen...
            logError("Unexpected parse exception setting timer", e);
        }

        return t;
    }

    /**
     * Create a trigger than runs a rule based upon a channel event.
     *
     * @param channelUID astro:sun:local:nauticDusk#event for example
     * @param event      typically START, END, or null for both
     * @return the trigger
     */
    protected Trigger createChannelEventTrigger(String channelUID, String event) {
        Configuration config = new Configuration();
        config.put("channelUID", channelUID);
        config.put("event", event);
        return buildTrigger("channel_" + event, "core.ChannelEventTrigger", config);
    }

    /**
     * Create a trigger that runs a rule based on any event on a channel
     *
     * @param channelUID astro:sun:local:nauticDusk#event for example
     * @return the trigger
     */
    protected Trigger createChannelEventTrigger(String channelUID) {
        return createChannelEventTrigger(channelUID, null);
    }

    /**
     * create a trigger that runs a rule on all events from a given source.
     *
     * @param eventSource typically a channel UID such as astro:sun:local:nauticDusk#event
     * @return the trigger
     */
    protected Trigger createGenericChannelEventTrigger(String eventSource) {
        return createGenericChannelEventTrigger("smarthome/channels/*/triggered", eventSource);
    }

    /**
     * create a trigger that runs a rule on all events from a given source and topic.
     *
     * @param eventTopic  then event topic
     * @param eventSource typically the channelUID
     * @return the trigger
     */
    protected Trigger createGenericChannelEventTrigger(String eventTopic, String eventSource) {
        Configuration config = new Configuration();
        config.put("eventTopic", eventTopic);
        config.put("eventSource", eventSource);
        config.put("eventTypes", "ChannelTriggeredEvent"); // if null then all events match, I think
        return buildTrigger("gen_ALL", "core.GenericEventTrigger", config);
    }

    /**
     * Convenience method for creating a trigger using TriggerBuilder.
     *
     * @param id      the trigger ID
     * @param typeUID the trigger type UID
     * @param config  configuration for the trigger
     * @return the trigger
     */
    protected Trigger buildTrigger(String id, String typeUID, Configuration config) {
        return TriggerBuilder.create().withId(id).withTypeUID(typeUID).withConfiguration(config).build();
    }

    /**
     * Find an OH1 or OH2 action with the given name
     *
     * @param actionName the action name
     * @return the action class or null if it doesn't exist
     */
    protected Object getAction(String actionName) {
        Object obj = getOH1Action(actionName);
        if (obj == null) {
            obj = getOH2Action(actionName);
        }
        return obj;
    }

    /**
     * Find an OpenHab 1 action with the given action name.
     *
     * @param actionName the action name
     * @return the action class or null if it doesn't exist
     */
    protected Object getOH1Action(String actionName) {
        Bundle bundle = FrameworkUtil.getBundle(BaseRule.class);
        BundleContext context = bundle.getBundleContext();

        try {
            // look for OH1 actions
            ServiceReference<?>[] refs = context
                    .getAllServiceReferences("org.openhab.core.scriptengine.action.ActionService", null);
            for (ServiceReference<?> ref : refs) {
                if (((String) ref.getProperty("component.name")).contains(actionName)) {
                    // org.openhab.core.scriptengine.action.ActionService can't be imported...
                    // so use reflection.
                    Object svc = context.getService(ref);
                    try {
                        Method method = svc.getClass().getMethod("getActionClass");
                        return method.invoke(svc);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                            | NoSuchMethodException | SecurityException e) {
                        logWarn("Unexpected exception finding OH1 Action", e);
                    }
                }
            }
        } catch (InvalidSyntaxException e) {
            // should not happen since we are not using a filter
            logWarn("Unexpected exception finding OH1 Action", e);
        }
        // not found.
        return null;
    }

    /**
     * Find an OpenHab 2 action with the given action name.
     *
     * @param actionName the action name
     * @return the action class or null if it doesn't exist
     */
    protected Object getOH2Action(String actionName) {
        Bundle bundle = FrameworkUtil.getBundle(BaseRule.class);
        BundleContext context = bundle.getBundleContext();

        try {
            // look for OH2 actions
            ServiceReference<?>[] refs = context
                    .getAllServiceReferences("org.eclipse.smarthome.model.script.engine.action.ActionService", null);
            for (ServiceReference<?> ref : refs) {
                if (((String) ref.getProperty("component.name")).contains(actionName)) {
                    ActionService svc = (ActionService) context.getService(ref);
                    return svc.getActionClass();
                }
            }
        } catch (InvalidSyntaxException e) {
            // should not happen since we are not using a filter
            logWarn("Unexpected exception finding OH2 Action", e);
        }
        // not found.
        return null;
    }

}
