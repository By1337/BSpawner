package org.by1337.bspawner.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.by1337.bspawner.GuiItil.InventoryGenerate;
import org.by1337.bspawner.Task.ITask;
import org.by1337.bspawner.Task.TaskBringTheMob;
import org.by1337.bspawner.util.Effect.Sphere;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static org.by1337.bspawner.BSpawner.SpawnersDb;
import static org.by1337.bspawner.BSpawner.instance;
import static org.by1337.bspawner.util.Config.getMessage;
import static org.by1337.bspawner.util.Config.getTranslation;


public class SpawnerTask {

    private List<ITask> Tasks = new ArrayList<>();

    private boolean allTaskComplete = false;
    private boolean isDropped = false;
    private Block block;
    public PlayerInteractEvent pie;
    private Player lastViewer = null;
    private final String spawnerId;
    private Inventory inv = Bukkit.createInventory(null, 54, Config.getMessage("spawner-inventory-name"));

    public SpawnerTask(Block block) {
        this.block = block;
        spawnerId = "Spawner-Id_" + block.getLocation().getBlockX() + block.getLocation().getBlockY() + block.getLocation().getBlockZ() + ThreadLocalRandom.current().nextInt(1000);
        TasksGenerate.Generate(this);
    }

    public SpawnerTask(String spawnerId) {
        this.spawnerId = spawnerId;
    }


    public Inventory getInv() {
        return inv;
    }


    public Block getBlock() {
        return block;
    }

    public void ReplaceTask(ITask oldTask, ITask newTask) {
        for (int x = 0; x < Tasks.size(); x++) {
            if (Tasks.get(x).equals(oldTask)) {
                Tasks.set(x, newTask);
                InventoryGenerate.MenuGenerate(this, lastViewer);
                break;
            }
        }
    }

    public boolean CheckAllTaskCompleted() {
        boolean isChecked = false;
        for (ITask key : Tasks) {
            if (key.isTaskCompleted()) {
                isChecked = true;
            } else {
                isChecked = false;
                break;
            }
        }
        allTaskComplete = isChecked;
        return isChecked;
    }

    public ITask getLastActiveTask() {
        ITask buff = null;
        for (ITask task : getTasks()) {
            if (task.isTaskActive()) {
                if (buff != null) {//необходима на тот случай если задания будут находиться не по порядку
                    if (task.getSlot() > buff.getSlot())
                        buff = task;
                } else
                    buff = task;
            }
        }
        return buff;
    }

    public void skipTask() {
        ITask task = getLastActiveTask();
        task.setComplete();
        ActivateNextTask(task);
        InventoryGenerate.MenuGenerate(this, lastViewer);
        CheckAllTaskCompleted();
    }

    public void ChangeTask() {
        ITask oldTask = getLastActiveTask();
        ITask newTask = TasksGenerate.getRandomTask(oldTask.getSlot(), getBlock().getLocation());
        while (Objects.equals(oldTask.getTaskType(), newTask.getTaskType()) && Objects.equals(oldTask.getConfigId(), newTask.getConfigId())){
            newTask = TasksGenerate.getRandomTask(oldTask.getSlot(), getBlock().getLocation());
        }
        newTask.setTaskActive(true);
        ReplaceTask(oldTask, newTask);
        InventoryGenerate.MenuGenerate(this, lastViewer);
    }

    public ITask getTaskBySlot(int slot) {
        for (ITask key : Tasks) {
            if (slot == key.getSlot()) {
                return key;
            }
        }
        return null;
    }

    public void ActivateNextTask(ITask thisTask) {
        int indexSlot = instance.getConfig().getIntegerList("tasks-slots").indexOf(thisTask.getSlot());
        if (instance.getConfig().getIntegerList("tasks-slots").size() >= indexSlot) {
            int nextSlot = instance.getConfig().getIntegerList("tasks-slots").size() == indexSlot + 1 ? -1 : instance.getConfig().getIntegerList("tasks-slots").get(indexSlot + 1);
            if (indexSlot == -1)
                return;
            ITask nextTask = getTaskBySlot(nextSlot);
            if (nextTask != null) nextTask.setTaskActive(true);
        }
    }

    public void MineSpawner() {
        inv.clear();
        Sphere.totem(getBlock().getLocation().clone(), 2, 20);
        dropSpawner();
    }

    public void dropSpawner() {
        ItemStack item = new ItemStack(Material.SPAWNER);

        BlockStateMeta bsm = (BlockStateMeta) item.getItemMeta();

        CreatureSpawner cs2 = (CreatureSpawner) block.getState();
        assert bsm != null;
        CreatureSpawner cs = (CreatureSpawner) bsm.getBlockState();
        cs.setSpawnedType(cs2.getSpawnedType());
        bsm.setBlockState(cs);
        item.setItemMeta(bsm);
        block.getLocation().getWorld();


        String name = getMessage("spawner-name").replace("{spawner-name}", getTranslation("spawners-name." + cs.getSpawnedType()));
        item = InventoryGenerate.ItemSetName(item, "&a" + name, lastViewer);

        block.setType(Material.AIR);
        Objects.requireNonNull(block.getLocation().getWorld()).dropItem(block.getLocation(), item);
        isDropped = true;
        SpawnersDb.remove(getBlock().getLocation());
    }

    public String getSpawnerId() {
        return spawnerId;
    }

    public boolean isDropped() {
        return isDropped;
    }

    public void setDropped(boolean dropped) {
        isDropped = dropped;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public String getListTypeNum(ITask task, String key2) {
        String str = "";
        for (String key : task.getTask().keySet()) {
            str += str != "" ? Config.getMessage("get-list-splitter") : "";
            if (task instanceof TaskBringTheMob)
                str += Config.getMessage("get-list-type-num").replace("{item}", Config.getTranslation("mobs." + key)).replace("{num}", "" + task.getTask().get(key).get(key2));
            else
                str += Config.getMessage("get-list-type-num").replace("{item}", Config.getTranslation("items." + key)).replace("{num}", "" + task.getTask().get(key).get(key2));
        }
        return str;
    }

    public List<ITask> getTasks() {
        return Tasks;
    }

    public void taskPut(ITask task, String type) {
        Tasks.add(task);
    }

    public boolean isAllTaskComplete() {
        return allTaskComplete;
    }

    public Player getLastViewer() {
        return lastViewer;
    }

    public void setLastViewer(Player lastViewer) {
        this.lastViewer = lastViewer;
    }

    public PlayerInteractEvent getPie() {
        return pie;
    }

    public void setPie(PlayerInteractEvent pie) {
        this.pie = pie;
    }
}
