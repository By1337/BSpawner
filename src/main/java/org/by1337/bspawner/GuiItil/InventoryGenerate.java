package org.by1337.bspawner.GuiItil;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.persistence.PersistentDataType;
import org.by1337.bspawner.Task.*;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;
import org.by1337.bspawner.util.TasksGenerate;

import java.util.*;

import static org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES;
import static org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS;
import static org.by1337.bspawner.BSpawner.instance;
import static org.by1337.bspawner.Listener.InventoryClick.InventoryListener.getLockedTaskItem;

public class InventoryGenerate {
    public static void MenuGenerate(SpawnerTask spawnerTask, Player pl) {
        Inventory inv = spawnerTask.getInv();
        List<Integer> slots = instance.getConfig().getIntegerList("tasks-slots");
        slots.stream().sorted();
        for (ITask key : spawnerTask.getTasks()) {
            switch (key.getTaskType()) {
                case "type-bring-items": {
                    inv.setItem(key.getSlot(), bringItemsItem((TaskBringItems) key, spawnerTask, pl));
                    break;
                }
                case "type-bring-the-mob": {
                    inv.setItem(key.getSlot(), BringTheMobItem((TaskBringTheMob) key, spawnerTask, pl));
                    break;
                }
                case "type-place-block": {
                    inv.setItem(key.getSlot(), PlaceBlock((TaskPlaceBlock) key, spawnerTask, pl));
                    break;
                }
                case "type-break-block": {
                    inv.setItem(key.getSlot(), BreakBlock((TaskBreakBlock) key, spawnerTask, pl));
                    break;
                }
            }

        }
        HashMap<Integer, ItemStack> items = guiGenerate(spawnerTask, pl);
        for (Integer slot : items.keySet()) {
            inv.setItem(slot, items.get(slot));
        }
    }

    private static ItemStack BreakBlock(TaskBreakBlock task, SpawnerTask spawnerTask, Player pl) {
        if (!task.isTaskActive()) {
            return getLockedTaskItem();
        }
        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//block -> amount:0, broken:0

        ItemStack item = new ItemStack(Material.JIGSAW);
        if (taskMap.isEmpty()) {
            Message.error(String.format(Config.getMessage("task-error"), task.getTaskType(), task.getConfigId()));
            Message.error(String.format(Config.getMessage("task-error-2"), task.getSlot(), spawnerTask.getBlock().getLocation()));
            Message.error(Config.getMessage("task-error-3"));
            TaskBreakBlock newTask = TasksGenerate.taskBreakBlock(spawnerTask.getBlock().getLocation(), task.getSlot());
            newTask.setTaskActive(true);
            taskMap.clear();
            taskMap.putAll(task.getTask());
            spawnerTask.ReplaceTask(task, newTask);
        }
        for (String mat : taskMap.keySet()) {
            if (instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material").contains("basehead-")) {
                item = BaseHeadHook.getItem(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material"));
            } else
                try {
                    item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-break-block." + task.getConfigId() + ".info.material")));
                } catch (IllegalArgumentException e) {
                    Message.error(String.format(Config.getMessage("material-error"), instance.getConfig().getString("tasks.type-place-block." + task.getConfigId() + ".info.material")));

                    continue;
                }
            int amount = taskMap.get(mat).get("amount");
            int broken = taskMap.get(mat).get("broken");

            if (instance.getConfig().get("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt") != null) {
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(Integer.parseInt(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt").replace("{CustomModelData:", "").replace("}", "")));
                item.setItemMeta(im);
            }

            String name = instance.getConfig().getString("tasks.type-break-block." + task.getConfigId() + ".info.name");
            assert name != null;
            name = name.replace("{item-name}", Config.getTranslation("items." + item.getType()));
            item = ItemSetName(item, name, pl);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "type-break-block");
            item.setItemMeta(im);
        }


        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.lore");
        for (String key : taskMap.keySet())
            lore.replaceAll(s -> s.replace("{broken-" + key + "}", "" + taskMap.get(key).get("broken")));
        item = ItemAddLore(item, lore, pl);

        if (task.isTaskCompleted())
            item = addEnchant(item);
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(NamespacedKey.fromString("spawner"), PersistentDataType.STRING, "true");
        im.addItemFlags(HIDE_ATTRIBUTES);
        item.setItemMeta(im);

        return item;
    }

    private static ItemStack PlaceBlock(TaskPlaceBlock task, SpawnerTask spawnerTask, Player pl) {
        if (!task.isTaskActive()) {
            return getLockedTaskItem();
        }

        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//block -> amount:0, put:0

        ItemStack item = new ItemStack(Material.JIGSAW);

        if (taskMap.isEmpty()) {
            Message.error(String.format(Config.getMessage("task-error"), task.getTaskType(), task.getConfigId()));
            Message.error(String.format(Config.getMessage("task-error-2"), task.getSlot(), spawnerTask.getBlock().getLocation()));
            Message.error(Config.getMessage("task-error-3"));
            TaskPlaceBlock newTask = TasksGenerate.taskPlaceBlock(spawnerTask.getBlock().getLocation(), task.getSlot());
            newTask.setTaskActive(true);
            taskMap.clear();
            taskMap.putAll(task.getTask());
            spawnerTask.ReplaceTask(task, newTask);
        }
        for (String mat : taskMap.keySet()) {
            if (instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material").contains("basehead-")) {
                item = BaseHeadHook.getItem(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material"));
            } else
                try {
                    item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-place-block." + task.getConfigId() + ".info.material")));
                } catch (IllegalArgumentException e) {
                    Message.error(String.format(Config.getMessage("material-error"), instance.getConfig().getString("tasks.type-place-block." + task.getConfigId() + ".info.material")));
                    continue;
                }
            int amount = taskMap.get(mat).get("amount");
            int placed = taskMap.get(mat).get("put");

            if (instance.getConfig().get("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt") != null) {
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(Integer.parseInt(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt").replace("{CustomModelData:", "").replace("}", "")));
                item.setItemMeta(im);
            }

            String name = instance.getConfig().getString("tasks.type-place-block." + task.getConfigId() + ".info.name");
            assert name != null;
            name = name.replace("{item-name}", Config.getTranslation("items." + item.getType()));
            item = ItemSetName(item, name, pl);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "type-place-block");
            item.setItemMeta(im);
        }

        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.lore");
        for (String key : taskMap.keySet())
            lore.replaceAll(s -> s.replace("{put-" + key + "}", "" + taskMap.get(key).get("put")));
        item = ItemAddLore(item, lore, pl);

        if (task.isTaskCompleted())
            item = addEnchant(item);
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(NamespacedKey.fromString("spawner"), PersistentDataType.STRING, "true");
        im.addItemFlags(HIDE_ATTRIBUTES);
        item.setItemMeta(im);
        return item;
    }

    private static ItemStack BringTheMobItem(TaskBringTheMob task, SpawnerTask spawnerTask, Player pl) {
        if (!task.isTaskActive()) {
            return getLockedTaskItem();
        }

        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//mob type -> amount:0, completed:0 | 0 false 1 true

        ItemStack item = new ItemStack(Material.JIGSAW);
        if (taskMap.isEmpty()) {
            Message.error(String.format(Config.getMessage("task-error"), task.getTaskType(), task.getConfigId()));
            Message.error(String.format(Config.getMessage("task-error-2"), task.getSlot(), spawnerTask.getBlock().getLocation()));
            Message.error(Config.getMessage("task-error-3"));
            TaskBringTheMob newTask = TasksGenerate.taskBringTheMob(spawnerTask.getBlock().getLocation(), task.getSlot());
            newTask.setTaskActive(true);
            taskMap.clear();
            taskMap.putAll(task.getTask());
            spawnerTask.ReplaceTask(task, newTask);
        }
        for (String mob : taskMap.keySet()) {
            if (instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material").contains("basehead-")) {
                item = BaseHeadHook.getItem(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material"));
            } else
                try {
                    item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-bring-the-mob." + task.getConfigId() + ".info.material")));
                } catch (IllegalArgumentException e) {
                    Message.error(String.format(Config.getMessage("material-error"), instance.getConfig().getString("tasks.type-place-block." + task.getConfigId() + ".info.material")));
                    continue;
                }
            if (instance.getConfig().get("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt") != null) {
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(Integer.parseInt(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt").replace("{CustomModelData:", "").replace("}", "")));
                item.setItemMeta(im);
            }

            int amount = taskMap.get(mob).get("amount");
            int completed = taskMap.get(mob).get("completed");
            String name = instance.getConfig().getString("tasks.type-bring-the-mob." + task.getConfigId() + ".info.name");
            assert name != null;
            name = name.replace("{item-name}", Config.getTranslation("mobs." + item.getType()));
            item = ItemSetName(item, name, pl);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "type-bring-the-mob");
            im.addItemFlags(HIDE_ATTRIBUTES);
            item.setItemMeta(im);
        }

        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.lore");
        item = ItemAddLore(item, lore, pl);

        if (task.isTaskCompleted())
            item = addEnchant(item);
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(NamespacedKey.fromString("spawner"), PersistentDataType.STRING, "true");
        im.addItemFlags(HIDE_ATTRIBUTES);
        item.setItemMeta(im);
        return item;
    }


    private static ItemStack bringItemsItem(TaskBringItems task, SpawnerTask spawnerTask, Player pl) {
        if (!task.isTaskActive()) {
            return getLockedTaskItem();
        }

        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//mat -> bring:0, brought:0
        ItemStack item = new ItemStack(Material.JIGSAW);
        if (taskMap.isEmpty()) {
            Message.error(String.format(Config.getMessage("task-error"), task.getTaskType(), task.getConfigId()));
            Message.error(String.format(Config.getMessage("task-error-2"), task.getSlot(), spawnerTask.getBlock().getLocation()));
            Message.error(Config.getMessage("task-error-3"));
            TaskBringItems newTask = TasksGenerate.bringItemsGenerate(task.getSlot());
            newTask.setTaskActive(true);
            taskMap.clear();
            taskMap.putAll(task.getTask());
            spawnerTask.ReplaceTask(task, newTask);
        }
        for (String mat : taskMap.keySet()) {
            int bring = taskMap.get(mat).get("bring");
            int brought = taskMap.get(mat).get("brought");

            if (instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material").contains("basehead-")) {
                item = BaseHeadHook.getItem(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.material"));
            } else
                try {
                    item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-bring-items." + task.getConfigId() + ".info.material")));
                } catch (IllegalArgumentException e) {
                    Message.error(String.format(Config.getMessage("material-error"), mat));
                    continue;
                }

            if (instance.getConfig().get("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt") != null) {
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(Integer.parseInt(instance.getConfig().getString("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.nbt").replace("{CustomModelData:", "").replace("}", "")));
                item.setItemMeta(im);
            }

            String name = instance.getConfig().getString("tasks.type-bring-items." + task.getConfigId() + ".info.name");

            item = ItemSetName(item, name, pl);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "tasks.type-bring-items");
            im.addItemFlags(HIDE_ATTRIBUTES);
            item.setItemMeta(im);

        }

        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getConfigId() + ".info.lore");
        for (String key : taskMap.keySet())
            lore.replaceAll(s -> s.replace("{brought-" + key + "}", "" + taskMap.get(key).get("brought")));
        item = ItemAddLore(item, lore, pl);


        if (task.isTaskCompleted())
            item = addEnchant(item);
        ItemMeta im = item.getItemMeta();
        im.getPersistentDataContainer().set(NamespacedKey.fromString("spawner"), PersistentDataType.STRING, "true");
        im.addItemFlags(HIDE_ATTRIBUTES);
        item.setItemMeta(im);
        return item;
    }

    public static HashMap<Integer, ItemStack> guiGenerate(SpawnerTask tasks, Player pl) {
        HashMap<Integer, ItemStack> gui = new HashMap<>();
        for (String key : instance.getConfig().getConfigurationSection("gui").getKeys(false)) {
            ItemStack item;
            if (instance.getConfig().getString("gui." + key + ".material").contains("basehead-")) {
                item = BaseHeadHook.getItem(instance.getConfig().getString("gui." + key + ".material"));
            } else
                try {
                    item = new ItemStack(Material.valueOf(instance.getConfig().getString("gui." + key + ".material")));
                } catch (IllegalArgumentException e) {
                    Message.error(String.format(Config.getMessage("material-error"), key));
                    continue;
                }

            if (instance.getConfig().get("gui." + key + ".nbt") != null) {
                ItemMeta im = item.getItemMeta();
                im.setCustomModelData(Integer.parseInt(instance.getConfig().getString("gui." + key + ".nbt").replace("{CustomModelData:", "").replace("}", "")));
                item.setItemMeta(im);
            }
            ItemMeta im = item.getItemMeta();
            im.getPersistentDataContainer().set(NamespacedKey.fromString("spawner"), PersistentDataType.STRING, "true");
            im.addItemFlags(HIDE_ATTRIBUTES);
            item.setItemMeta(im);
            if (instance.getConfig().get("gui." + key + ".name") != null)
                item = ItemSetName(item, instance.getConfig().getString("gui." + key + ".name"), pl);
            if (instance.getConfig().getBoolean("gui." + key + ".enchant"))
                item = addEnchant(item);
            if (instance.getConfig().get("gui." + key + ".lore") != null) {
                item = ItemAddLore(item, instance.getConfig().getStringList("gui." + key + ".lore"), pl);
            }
            if (instance.getConfig().get("gui." + key + ".slot") != null) {
                gui.put(instance.getConfig().getInt("gui." + key + ".slot"), item);
            }
            if (instance.getConfig().get("gui." + key + ".slots") != null) {
                for (Integer slot : instance.getConfig().getIntegerList("gui." + key + ".slots")) {
                    gui.put(slot, item);
                }
            }
        }
        return gui;
    }

    public static ItemStack addEnchant(ItemStack getItem) {
        ItemStack item = new ItemStack(getItem);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 0, true);
        meta.addItemFlags(HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack ItemSetName(ItemStack getItem, String name, Player pl) {
        ItemStack item = new ItemStack(getItem);
        ItemMeta meta = item.getItemMeta();
        name = Message.messageBuilder(name);
        assert meta != null;
        meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack ItemAddLore(ItemStack getItem, List<String> addLore, Player pl) {
        ItemStack item = new ItemStack(getItem);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        addLore.replaceAll(s -> Message.setPlaceholders(pl, s));
        addLore.replaceAll(Message::messageBuilder);

        lore.addAll(addLore);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
