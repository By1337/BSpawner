package org.by1337.bspawner.GuiItil;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import org.bukkit.persistence.PersistentDataType;
import org.by1337.bspawner.Task.*;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;


import java.util.*;

import static org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS;
import static org.by1337.bspawner.BSpawner.instance;

public class InventoryGenerate {
    public static void MenuGenerate(SpawnerTask spawnerTask) {//mat -> slot:0, bring:0, brought:0
        Inventory inv = spawnerTask.getInv();
        boolean isCompleted = true;
        List<Integer> slots = instance.getConfig().getIntegerList("tasks-slots");
        slots.stream().sorted();
        for (ITask key : spawnerTask.getTasks()) {
            switch (key.getTaskType()) {
                case "type-bring-items": {
                    inv.setItem(key.slot(), bringItemsItem((TaskBringItems) key));
                    break;
                }
                case "type-bring-the-mob": {
                    inv.setItem(key.slot(), BringTheMobItem((TaskBringTheMob) key, spawnerTask));
                    break;
                }
                case "type-place-block": {
                    inv.setItem(key.slot(), PlaceBlock((TaskPlaceBlock) key));
                    break;
                }
                case "type-break-block": {
                    inv.setItem(key.slot(), BreakBlock((TaskBreakBlock) key));
                    break;
                }
            }

        }
        HashMap<Integer, ItemStack> items = guiGenerate(spawnerTask);
        for (Integer slot : items.keySet()) {
            inv.setItem(slot, items.get(slot));
        }
    }
    private static ItemStack  BreakBlock(TaskBreakBlock task){
        if (!task.isTaskActive()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(instance.getConfig().getString("locked-tasks-material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Message.messageBuilder("&cВыполните предыдущие задание!"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }
        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//block -> amount:0, broken:0

        ItemStack item = new ItemStack(Material.JIGSAW);
        for(String mat : taskMap.keySet()){
            try {
                item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-break-block." + task.getTaskId() + ".info.material")));
            } catch (IllegalArgumentException e) {
                Message.error("Матерьяла " + instance.getConfig().getString("tasks.type-place-block." + task.getTaskId() + ".info.material") + " Не существует!");
                continue;
            }
            int amount = taskMap.get(mat).get("amount");
            int broken = taskMap.get(mat).get("broken");

            String name = instance.getConfig().getString("tasks.type-break-block." + task.getTaskId() + ".info.name");
            assert name != null;
            name = name.replace("{item-name}", Config.getTranslation("items." + item.getType()));
            item = ItemSetName(item, name);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "type-break-block");
            item.setItemMeta(im);
        }
        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getTaskId() + ".info.lore");
        for(String key : taskMap.keySet())
            lore.replaceAll(s -> s.replace("{broken-" + key + "}", "" + taskMap.get(key).get("broken")));
        item = ItemAddLore(item, lore);

        if(task.isTaskCompleted())
            item = addEnchant(item);
        return item;
    }

    private static ItemStack  PlaceBlock(TaskPlaceBlock task){
        if (!task.isTaskActive()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(instance.getConfig().getString("locked-tasks-material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Message.messageBuilder("&cВыполните предыдущие задание!"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//block -> amount:0, put:0

        ItemStack item = new ItemStack(Material.JIGSAW);
        for(String mat : taskMap.keySet()){
            try {
                item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-place-block." + task.getTaskId() + ".info.material")));
            } catch (IllegalArgumentException e) {
                Message.error("Матерьяла " + instance.getConfig().getString("tasks.type-place-block." + task.getTaskId() + ".info.material") + " Не существует!");
                continue;
            }
            int amount = taskMap.get(mat).get("amount");
            int placed = taskMap.get(mat).get("put");

            String name = instance.getConfig().getString("tasks.type-place-block." + task.getTaskId() + ".info.name");
            assert name != null;
            name = name.replace("{item-name}", Config.getTranslation("items." + item.getType()));
            item = ItemSetName(item, name);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "type-place-block");
            item.setItemMeta(im);
        }
        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getTaskId() + ".info.lore");
        for(String key : taskMap.keySet())
            lore.replaceAll(s -> s.replace("{put-" + key + "}", "" + taskMap.get(key).get("put")));
        item = ItemAddLore(item, lore);

        if(task.isTaskCompleted())
            item = addEnchant(item);
        return item;
    }

    private static ItemStack  BringTheMobItem(TaskBringTheMob task, SpawnerTask spawnerTask){
        if (!task.isTaskActive()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(instance.getConfig().getString("locked-tasks-material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Message.messageBuilder("&cВыполните предыдущие задание!"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//mob type -> amount:0, completed:0 | 0 false 1 true

        ItemStack item = new ItemStack(Material.JIGSAW);
        for(String mob : taskMap.keySet()){
            try {
                item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-bring-the-mob." + task.getTaskId() + ".info.material")));
            } catch (IllegalArgumentException e) {
                Message.error("Матерьяла " + instance.getConfig().getString("tasks.type-bring-the-mob." + task.getTaskId() + ".info.material") + " Не существует!");
                continue;
            }
            int amount = taskMap.get(mob).get("amount");
            int completed = taskMap.get(mob).get("completed");
            String name = instance.getConfig().getString("tasks.type-bring-the-mob." + task.getTaskId() + ".info.name");
            assert name != null;
            name = name.replace("{item-name}", Config.getTranslation("mobs." + item.getType()));
            item = ItemSetName(item, name);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "type-bring-the-mob");
            item.setItemMeta(im);
        }

        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getTaskId() + ".info.lore");
//        for(String key : taskMap.keySet())
//            lore.replaceAll(s -> s.replace("{brought-" + key + "}", "" + taskMap.get(key).get("brought")));
        item = ItemAddLore(item, lore);

        if(task.isTaskCompleted())
            item = addEnchant(item);
        return item;
    }

    private static ItemStack  bringItemsItem(TaskBringItems task){
        if (!task.isTaskActive()) {
            ItemStack itemStack = new ItemStack(Material.valueOf(instance.getConfig().getString("locked-tasks-material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;
            itemMeta.setDisplayName(Message.messageBuilder("&cВыполните предыдущие задание!"));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();//mat -> bring:0, brought:0
        ItemStack item = new ItemStack(Material.JIGSAW);
        if(taskMap.isEmpty()){
            item = ItemAddLore(item, new ArrayList<>(List.of(
                    "&cЭто задание не правильно настроено",
                    "&cПоэтому его невозможно пройти!",
                    "&cСообщите об это администрации!"
            )));
            item = ItemSetName(item, "&cERROR");
            item = addEnchant(item);
            return item;
        }
        for(String mat : taskMap.keySet()){
            int bring = taskMap.get(mat).get("bring");
            int brought = taskMap.get(mat).get("brought");

            try {
                item.setType(Material.valueOf(instance.getConfig().getString("tasks.type-bring-items." + task.getTaskId() + ".info.material")));
            } catch (IllegalArgumentException e) {
                Message.error("Матерьяла " + mat + " Не существует!");
                continue;
            }



//            lore.replaceAll(s -> s.replace(
//                    "{bring}", "" + bring).replace(
//                    "{brought}", "" + brought).replace(
//                    "{item-name}", Config.getTranslation("items." + mat.toUpperCase())));

            String name = instance.getConfig().getString("tasks.type-bring-items." + task.getTaskId() + ".info.name");
          //  assert name != null;
          //  name = name.replace("{item-name}", Config.getTranslation("items." + item.getType()));
            item = ItemSetName(item, name);
            ItemMeta im = item.getItemMeta();

            assert im != null;
            im.getPersistentDataContainer().set(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING, "tasks.type-bring-items");
            item.setItemMeta(im);

        }

        List<String> lore = instance.getConfig().getStringList("tasks." + task.getTaskType() + "." + task.getTaskId() + ".info.lore");
        for(String key : taskMap.keySet())
            lore.replaceAll(s -> s.replace("{brought-" + key + "}", "" + taskMap.get(key).get("brought")));
        item = ItemAddLore(item, lore);

        if(task.isTaskCompleted())
            item = addEnchant(item);
        return item;
    }
    public static HashMap<Integer, ItemStack> guiGenerate(SpawnerTask tasks) {
        HashMap<Integer, ItemStack> gui = new HashMap<>();
        for (String key : instance.getConfig().getConfigurationSection("gui").getKeys(false)) {
            ItemStack item;
            try {
                item = new ItemStack(Material.valueOf(instance.getConfig().getString("gui." + key + ".material")));
            } catch (IllegalArgumentException e) {
                Message.error("Матерьяла " + key + " Не существует!");
                continue;
            }
            if (instance.getConfig().get("gui." + key + ".name") != null)
                item = ItemSetName(item, instance.getConfig().getString("gui." + key + ".name"));
            if (instance.getConfig().getBoolean("gui." + key + ".enchant"))
                item = addEnchant(item);
            if (instance.getConfig().get("gui." + key + ".lore") != null) {
                item = ItemAddLore(item, instance.getConfig().getStringList("gui." + key + ".lore"));
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
        ItemStack getSpawner = new ItemStack(Material.valueOf(instance.getConfig().getString("get-spawner.material")));

        if (instance.getConfig().getBoolean("get-spawner.enchanted")) {
            getSpawner = addEnchant(getSpawner);
        }
        getSpawner = ItemSetName(getSpawner, instance.getConfig().getString("get-spawner.display-name"));
        if (tasks.CheckAllTaskCompleted())
            getSpawner = ItemAddLore(getSpawner, instance.getConfig().getStringList("get-spawner.lore.unlocked"));
        else
            getSpawner = ItemAddLore(getSpawner, instance.getConfig().getStringList("get-spawner.lore.locked"));
        gui.put(instance.getConfig().getInt("get-spawner.slot"), getSpawner);
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

    public static ItemStack ItemSetName(ItemStack getItem, String name) {
        ItemStack item = new ItemStack(getItem);
        ItemMeta meta = item.getItemMeta();
        name = Message.messageBuilder(name);
        assert meta != null;
        meta.setDisplayName(name);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack ItemAddLore(ItemStack getItem, List<String> addLore) {
        ItemStack item = new ItemStack(getItem);
        ItemMeta meta = item.getItemMeta();

        assert meta != null;
        List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
        addLore.replaceAll(Message::messageBuilder);
        lore.addAll(addLore);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
