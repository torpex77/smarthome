package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.smarthome.automation.Action;
import org.eclipse.smarthome.automation.Trigger;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.OnOffType;

/**
 * Executes when any of a set of related switches change to ON from OFF. Could be used for buttons on a remote, or
 * HVAC modes. Executes b1() for the first button in the list, b2() for the second, and so on. It is dynamic and can
 * support any number of buttons. It also accepts an DecimalType item and will call the same methods b1(), b2(), etc.
 * when the value of the item changes. In both cases, a warning will be logged if the bN() method is not implemented.
 *
 * @author doug
 *
 */
public class MultiSwitchRule extends BaseRule {

    final protected Map<String, Integer> itemButtons;

    final protected String numberItemName;

    // TODO: call "switch" instead of "button" in log statements?

    /**
     * constructor
     *
     * @param buttonItemNames a list of buttons to call when pressed
     * @param numberItemName an number item that will call methods when it changes
     */
    public MultiSwitchRule(List<String> buttonItemNames, String numberItemName) {
        this.itemButtons = new HashMap<>();
        this.numberItemName = numberItemName;
        if (buttonItemNames != null) {
            int idx = 0;
            for (String name : buttonItemNames) {
                this.itemButtons.put(name, ++idx);
            }
        }
    }

    /**
     * constructor
     *
     * @param buttonItemNames a list of buttons to call when pressed
     */
    public MultiSwitchRule(List<String> buttonItemNames) {
        this(buttonItemNames, null);
    }

    /**
     * constructor
     *
     * @param numberItemName an number item that will call methods when it changes
     */
    public MultiSwitchRule(String numberItemName) {
        this(null, numberItemName);
    }

    /**
     * Uses Java reflection to find and execute a method corresponding to
     * the button number that was pressed. The methods are named bN where
     * N is the button number.
     */
    @Override
    public Object execute(Action module, Map<String, ?> inputs) {
        try {
            // We will either get an ItemStateEvent if the numberItemName or ItemStateChangedEvent for one of the
            // buttons

            logDebug("Event received: " + inputs.get("event").toString());
            logDebug("Event class " + inputs.get("event").getClass().getName());
            ItemStateChangedEvent event = (ItemStateChangedEvent) inputs.get("event");

            // TODO: Change to look for methods that take Object instead of ItemStateChangedEvent to better work with
            // Groovy
            if (event.getItemName().equals(numberItemName)) {
                DecimalType state = (DecimalType) event.getItemState();
                String methodName = "b" + state.intValue();
                try {
                    Method method = this.getClass().getMethod(methodName, ItemStateChangedEvent.class);
                    method.invoke(this, event);
                } catch (NoSuchMethodException e) {
                    logWarn("Virtual button " + state.intValue() + " updated. " + methodName + "() not found.");
                }
            } else {
                Integer button = itemButtons.get(event.getItemName());
                if (button != null) {
                    String methodName = "b" + button.intValue();
                    try {
                        Method method = this.getClass().getMethod(methodName, ItemStateChangedEvent.class);
                        method.invoke(this, event);
                    } catch (NoSuchMethodException e) {
                        logWarn("Button " + button + " pressed. " + methodName + "() not found.");
                    }

                } else {
                    logError("Could not determine button from event item name " + event.getItemName());
                }
            }
        } catch (Exception e) {
            logError("Exception in event handler: ", e);
        }
        return null;
    }

    /**
     * create the triggers to execute the rule, one for each button and one for the number item.
     */
    @Override
    public List<Trigger> getTriggers() {
        List<Trigger> triggers = new ArrayList<>();
        if (numberItemName != null) {
            triggers.add(createItemStateChangeTrigger(numberItemName));
        }

        for (Entry<String, Integer> button : itemButtons.entrySet()) {
            triggers.add(createItemStateChangeTrigger(button.getKey(), OnOffType.OFF, OnOffType.ON));
        }

        return triggers;
    }

}
