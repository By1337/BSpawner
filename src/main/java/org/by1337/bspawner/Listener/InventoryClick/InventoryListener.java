package org.by1337.bspawner.Listener.InventoryClick;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.by1337.bspawner.GuiItil.BaseHeadHook;
import org.by1337.bspawner.GuiItil.InventoryGenerate;
import org.by1337.bspawner.Task.TaskBreakBlock;
import org.by1337.bspawner.Task.TaskBringItems;
import org.by1337.bspawner.Task.TaskBringTheMob;
import org.by1337.bspawner.Task.TaskPlaceBlock;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.ItemUtil;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static org.by1337.bspawner.BSpawner.*;

public class InventoryListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        Player pl;
        if (e.getWhoClicked() instanceof Player)
            pl = (Player) e.getWhoClicked();
        else
            return;
        SpawnerTask spawnerTask = null;
            for (Location loc : SpawnersDb.keySet()) {
                if (SpawnersDb.get(loc).getInv().getViewers().size() == 0)
                    continue;
                if (SpawnersDb.get(loc).getInv().equals(e.getInventory())) {
                    spawnerTask = SpawnersDb.get(loc);
                    break;
                }
            }
        if (spawnerTask == null)
            return;
        if (e.getCurrentItem() != null){
            e.setCancelled(true);
            return;
        }
        for (String key : instance.getConfig().getConfigurationSection("gui").getKeys(false)) {
            if (instance.getConfig().get("gui." + key + ".commands") == null)
                return;
            List<Integer> slots = new ArrayList<>();
            if (instance.getConfig().get("gui." + key + ".slot") != null)
                slots.add(instance.getConfig().getInt("gui." + key + ".slot"));
            if (instance.getConfig().get("gui." + key + ".slots") != null)
                slots.addAll(instance.getConfig().getIntegerList("gui." + key + ".slots"));

            if (slots.contains(e.getSlot())) {
                if (CommandGui.clickRequirement(spawnerTask, pl, key)) {

                    CommandGui.run(spawnerTask, pl, "gui." + key + ".commands");
                }
                pl.updateInventory();
                e.setCancelled(true);
                return;
            }


        }

        if (instance.getConfig().getIntegerList("tasks-slots").contains(e.getSlot())) {
            ItemStack item = e.getCurrentItem();
            ItemMeta im = item.getItemMeta();

            if (Objects.equals(item.getItemMeta().getLore() == null ? new ArrayList<String>() : item.getItemMeta().getLore(),
                    getLockedTaskItem().getItemMeta().getLore() == null ? new ArrayList<String>() : getLockedTaskItem().getItemMeta().getLore()
            )) {
                Message.sendMsg(pl, Config.getMessage("task-no-open"));
                e.setCancelled(true);
                return;
            }

            assert im != null;
            String type = im.getPersistentDataContainer().get(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING);
            if (im.getPersistentDataContainer().get(Objects.requireNonNull(NamespacedKey.fromString("type")), PersistentDataType.STRING) == null) {
                e.setCancelled(true);
                pl.closeInventory();
                pl.updateInventory();
                return;
            }
            type = type.replace("tasks.", "");
            switch (type) {
                case "type-bring-items": {
                    bringItems(item, (TaskBringItems) spawnerTask.getTaskBySlot(e.getSlot()), pl, spawnerTask);
                    InventoryGenerate.MenuGenerate(spawnerTask, pl);
                    break;
                }
                case "type-bring-the-mob": {
                    bringTheMob((TaskBringTheMob) spawnerTask.getTaskBySlot(e.getSlot()), spawnerTask, pl);
                    break;
                }
                case "type-place-block": {
                    placeBlock((TaskPlaceBlock) spawnerTask.getTaskBySlot(e.getSlot()), spawnerTask, pl);
                    break;
                }
                case "type-break-block": {
                    BreakBlock((TaskBreakBlock) spawnerTask.getTaskBySlot(e.getSlot()), spawnerTask, pl);
                    break;
                }
            }
        }
        if (e.getSlot() == instance.getConfig().getInt("get-spawner.slot")) {

            if (spawnerTask.CheckAllTaskCompleted()) {
                spawnerTask.MineSpawner();
                spawnerTask.setDropped(true);
                Message.sendMsg(pl, Config.getMessage("spawner-mined"));
                pl.closeInventory();
            } else
                Message.sendMsg(pl, Config.getMessage("spawner-no-mined"));
        }
        e.setCancelled(true);
    }

    private void bringTheMob(TaskBringTheMob taskBringTheMob, SpawnerTask spawnerTask, Player pl) {
        if (!taskBringTheMob.isTaskCompleted()) {
            Message.sendMsg(pl, Config.getMessage("task-mob").replace(
                    "{list}", spawnerTask.getListTypeNum(taskBringTheMob, "amount")));
        } else
            Message.sendMsg(pl, Config.getMessage("task-completed-click"));
    }

    private void placeBlock(TaskPlaceBlock taskPlaceBlock, SpawnerTask spawnerTask, Player pl) {
        if (!taskPlaceBlock.isTaskCompleted()) {
            Message.sendMsg(pl, Config.getMessage("task-place-block").replace(
                    "{list}", spawnerTask.getListTypeNum(taskPlaceBlock, "amount")));
        } else
            Message.sendMsg(pl, Config.getMessage("task-completed-click"));
    }

    private void BreakBlock(TaskBreakBlock taskBreakBlock, SpawnerTask spawnerTask, Player pl) {
        if (!taskBreakBlock.isTaskCompleted()) {
            Message.sendMsg(pl, Config.getMessage("task-break-block").replace(
                    "{list}", spawnerTask.getListTypeNum(taskBreakBlock, "amount")));
        } else
            Message.sendMsg(pl, Config.getMessage("task-completed-click"));
    }

    private void bringItems(ItemStack item, TaskBringItems tasks, Player pl, SpawnerTask spawnerTask) {
        if (tasks == null) return;

        if (tasks.isTaskCompleted()) {
            Message.sendMsg(pl, Config.getMessage("task-completed-click"));
            return;
        }
        HashMap<String, HashMap<String, Integer>> taskMap = tasks.getTask();//mat -> bring:0, brought:0
        for (String mat : taskMap.keySet()) {
            try {
                Material material = Material.valueOf(mat);
            } catch (IllegalArgumentException e) {
                Message.error("Матерьяла " + mat + " Не существует!");
                continue;
            }

            int itemHas = ItemUtil.playerHasItem(pl, Material.valueOf(mat));
            if (itemHas != 0) {
                int bring = taskMap.get(mat).get("bring");
                int brought = taskMap.get(mat).get("brought");
                if (bring == brought) {
                    continue;
                }

                if (bring < brought + itemHas) {
                    itemHas = taskMap.get(mat).get("bring") - taskMap.get(mat).get("brought");
                }

                ItemUtil.TakeItem(pl, Material.valueOf(mat), itemHas);
                taskMap.get(mat).put("brought", taskMap.get(mat).get("brought") + itemHas);

                Message.sendMsg(pl,
                        Config.getMessage("get-by").replace(
                                "{item}", Config.getTranslation("items." + Material.valueOf(mat))).replace(
                                "{amount}", "" + itemHas));
                tasks.setTask(taskMap);
            } else {
                Message.sendMsg(pl, Config.getMessage("no-item"));
            }

        }
        if (tasks.isTaskCompleted()) {
            Message.sendMsg(pl, Config.getMessage("task-completed-click"));
        }
        if (tasks.taskCompletionCheck()) {
            Message.sendAllNear(Config.getMessage("task-completed"), spawnerTask.getSpawner().getLocation());
            spawnerTask.ActivateNextTask(tasks);
        }
    }

    public static ItemStack getLockedTaskItem() {
        ItemStack itemStack;
        if (instance.getConfig().getString("locked-tasks-material").contains("basehead-")) {
            itemStack = BaseHeadHook.getItem(instance.getConfig().getString("locked-tasks-material"));
        } else {
            itemStack = new ItemStack(Material.valueOf(instance.getConfig().getString("locked-tasks-material")));
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (instance.getConfig().get("nbt") != null) {
                itemMeta.setCustomModelData(Integer.parseInt(instance.getConfig().getString("nbt").replace("{CustomModelData:", "").replace("}", "")));
                itemStack.setItemMeta(itemMeta);
            }
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Config.getMessage("task-no-open"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
