package org.eclipse.smarthome.automation.module.script.rulesupport.shared.simple.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.smarthome.core.items.GroupItem;
import org.eclipse.smarthome.core.items.Item;
import org.eclipse.smarthome.core.items.ItemNotFoundException;
import org.eclipse.smarthome.core.items.events.ItemStateChangedEvent;
import org.eclipse.smarthome.core.types.State;

/**
 * Base class for rules that action on changes to items.
 * It has various constructors that take a item or items and
 * will return them for triggers. An item can have a "friendly name"
 * which is used for default messages and can be used by subclasses to
 * output a more readable name than the switch.
 * <p>
 * If an item is really a group, then all items in the group will be
 * added, recursively.
 * <p>
 *
 * TODO: Groups need testing. It was copied from my OH1 stuff
 * TODO: any() and all() need testing
 *
 * @author doug
 *
 */
abstract public class ItemRule extends BaseRule {

    /**
     * maps item names to their "friendly" names.
     */
    protected final Map<String, String> itemNames;

    /**
     * create a rule for the tiven item
     *
     * @param itemName the name of the item
     */
    public ItemRule(String itemName) {
        this(itemName, null);
    }

    /**
     * create a rule for the given item and its friendly name
     *
     * @param itemName the item name (GarageDoorContact)
     * @param friendlyName the friendly name ("Garage door")
     */
    public ItemRule(String itemName, String friendlyName) {
        super();
        Map<String, String> map = new HashMap<String, String>();
        if (friendlyName == null) {
            map.put(itemName, itemName);
        } else {
            map.put(itemName, friendlyName);
        }
        itemNames = Collections.unmodifiableMap(createItemMap(map));
    }

    /**
     * create a rule for multiple items and their friendly names
     *
     * @param itemNames item names and their friendly names
     */
    public ItemRule(Map<String, String> itemNames) {
        super();
        // create an copy and make that unmodifiable
        this.itemNames = Collections.unmodifiableMap(createItemMap(itemNames));
    }

    /**
     * creates a rule for a list of itmes
     *
     * @param items a list of the item names
     */
    public ItemRule(List<String> items) {
        super();
        Map<String, String> map = new HashMap<String, String>();
        for (String itemName : items) {
            map.put(itemName, itemName);
        }
        itemNames = Collections.unmodifiableMap(createItemMap(map));
    }

    /**
     * create a map of all items and their friendly name for the rule, including
     * items from groups.
     *
     * @param map the item(s) passed in via constructor
     *
     * @return map of items to friendly names
     */
    private Map<String, String> createItemMap(Map<String, String> map) {

        Map<String, String> resolvedMap = new HashMap<>();
        // iterate the keys in the itemNames
        // if item type, add
        // if group type with BaseType, add
        // if group type without BaseType add all members (use the recurse method to get them)
        // for(String itemName : map.)

        for (Map.Entry<String, String> entry : map.entrySet()) {
            try {
                Item item = getItem(entry.getKey());
                logDebug("itemType = " + item.getClass().getName());
                if (item instanceof GroupItem) {
                    GroupItem gi = (GroupItem) item;
                    if (gi.getBaseItem() == null) {
                        for (Item child : gi.getAllMembers()) {
                            logDebug("GI No Base adding: " + child.getName());
                            resolvedMap.put(child.getName(), entry.getValue());
                        }
                    } else {
                        logDebug("GI Base adding: " + item.getName());
                        resolvedMap.put(entry.getKey(), entry.getValue());
                    }
                } else {
                    logDebug("Item adding: " + item.getName());
                    resolvedMap.put(entry.getKey(), entry.getValue());
                }
            } catch (ItemNotFoundException e) {
                logError(entry.getKey() + " does not exist. Ignored.");
            }
        }

        return resolvedMap;
    }

    /**
     * get a list of all items that need a trigger created, going
     * through groups as needed.
     *
     * TODO: Is this still needed with the same code in createItemMap?
     *
     * @return list of all items,
     */
    protected List<String> getItemsForTriggers() {
        // TODO: Test groups? Is this needed anymore?

        List<String> allNames = new ArrayList<String>();
        // iterate the keys in the itemNames
        // if item type, add
        // if group type with BaseType, add
        // if group type without BaseType add all members (use the recurse method to get them)
        // for(String itemName : map.)

        for (Map.Entry<String, String> entry : itemNames.entrySet()) {
            try {
                Item item = getItem(entry.getKey());
                logDebug("itemTYpe = " + item.getClass().getName());
                if (item instanceof GroupItem) {
                    GroupItem gi = (GroupItem) item;
                    if (gi.getBaseItem() == null) {
                        for (Item child : gi.getAllMembers()) {
                            logDebug("GI No Base adding: " + child.getName());
                            allNames.add(child.getName());
                        }
                    } else {
                        logDebug("GI Base adding: " + item.getName());
                        allNames.add(item.getName());
                    }
                } else {
                    logDebug("Item adding: " + item.getName());
                    allNames.add(item.getName());
                }
            } catch (ItemNotFoundException e) {
                logError(entry.getKey() + " does not exist. Ignored.");
            }
        }

        return allNames;

    }

    /**
     * get the friendly name for an item
     *
     * @param itemName the item name
     * @return the friendly name
     */
    protected String getFriendlyName(String itemName) {
        String s = itemNames.get(itemName);
        if (s == null) {
            return itemName;
        } else {
            return s;
        }
    }

    /**
     * get the friendly name for an item that the event is for
     *
     * @param event the event
     * @return the friendly name
     */
    protected String getFriendlyName(ItemStateChangedEvent event) {
        return getFriendlyName(event.getItemName());
    }

    /**
     * get the friendly name for an item
     *
     * @param item the item
     * @return the friendly name
     */
    protected String getFriendlyName(Item item) {
        return getFriendlyName(item.getName());
    }

    /**
     * returns true if all items are in the given state
     *
     * @param state the state to check for
     * @return true if all items are in that state
     */
    public boolean all(State state) {
        // logDebug("all(State) == " + state);
        // logDebug("all(State) toString == " + state.toString());
        // logDebug("all(State) class == " + state.getClass().getName());
        boolean b = true;
        for (String itemName : itemNames.keySet()) {
            try {
                // logDebug("all getItem(" + itemName + ").getState()) == " + getItem(itemName).getState());
                // logDebug("all getItem(" + itemName + ").getState() toString == " +
                // getItem(itemName).getState().toString());
                // logDebug("all getItem(" + itemName + ").getState() class == " +
                // getItem(itemName).getState().getClass().getName());
                Item item = getItem(itemName);
                if (item.getState() != state) {
                    logDebug(itemName + " is " + item.getState().toString());
                    b = false;
                    break;
                }
            } catch (ItemNotFoundException e) {
                logError(itemName + " does not exist. Ignored.");

            }
        }
        return b;
    }

    /**
     * returns true if any of the items are in the given state
     *
     * @param state the state for check for
     * @return true if at least one item is in the given state.
     */
    public boolean any(State state) {
        // logDebug("any(State) == " + state);
        // logDebug("any(State) toString == " + state.toString());
        // logDebug("any(State) class == " + state.getClass().getName());
        boolean b = false;
        for (String itemName : itemNames.keySet()) {
            try {
                // logDebug("any getItem(" + itemName + ").getState()) == " + getItem(itemName).getState());
                // logDebug("any getItem(" + itemName + ").getState() toString == " +
                // getItem(itemName).getState().toString());
                // logDebug("any getItem(" + itemName + ").getState() class == " +
                // getItem(itemName).getState().getClass().getName());
                Item item = getItem(itemName);
                if (item.getState() == state) {
                    logDebug(itemName + " is " + item.getState().toString());
                    b = true;
                    break;
                }
            } catch (ItemNotFoundException e) {
                logError(itemName + " does not exist. Ignored.");

            }
        }
        return b;
    }

}
