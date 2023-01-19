package org.by1337.bspawner.util;


import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.scheduler.BukkitRunnable;
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

    // private HashMap<String, ITask> Task = new HashMap<>();
    private List<ITask> Tasks = new ArrayList<>();
    //TasksGenerate tasksGenerate = new TasksGenerate();
    private boolean allTaskComplete = false;
    private boolean isDropped = false;
    private Block Spawner;
    private final String spawnerId;
    private Inventory inv = Bukkit.createInventory(null, 54, "§7Спавнер");

    public SpawnerTask(Block spawner) {
        Spawner = spawner;
        spawnerId = "Spawner-Id_" + spawner.getLocation().getBlockX() + spawner.getLocation().getBlockY() + spawner.getLocation().getBlockZ() + ThreadLocalRandom.current().nextInt(1000);
        TasksGenerate.Generate(this);
    }

    public SpawnerTask(String spawnerId) {
        this.spawnerId = spawnerId;
    }


    public Inventory getInv() {
        return inv;
    }

    public void setInv(Inventory inv) {
        this.inv = inv;
    }

    public Block getSpawner() {
        return Spawner;
    }


    public boolean isAllTasksCompleted() {
        return allTaskComplete;
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

    public ITask getTaskBySlot(int slot) {
        for (ITask key : Tasks) {
            if (slot == key.slot()) {
                return key;
            }
        }
        return null;
    }

    public void ActivateNextTask(ITask thisTask) {
        int indexSlot = instance.getConfig().getIntegerList("tasks-slots").indexOf(thisTask.slot());
        if (instance.getConfig().getIntegerList("tasks-slots").size() >= indexSlot) {
            //  int nextSlot  = instance.getConfig().getIntegerList("tasks-slots").get(indexSlot + 1);
            int nextSlot = instance.getConfig().getIntegerList("tasks-slots").size() == indexSlot + 1 ? -1 : instance.getConfig().getIntegerList("tasks-slots").get(indexSlot + 1);
            if (indexSlot == -1)
                return;
            ITask nextTask = getTaskBySlot(nextSlot);
            if (nextTask != null) nextTask.setTaskActive(true);
        }
    }

    public void MineSpawner() {

        Sphere.totem(getSpawner().getLocation().clone(),2, 20);
        dropSpawner();

    }

    public void dropSpawner() {
        ItemStack item = new ItemStack(Material.SPAWNER);

        BlockStateMeta bsm = (BlockStateMeta) item.getItemMeta();

        CreatureSpawner cs2 = (CreatureSpawner) Spawner.getState();
        assert bsm != null;
        CreatureSpawner cs = (CreatureSpawner) bsm.getBlockState();
        cs.setSpawnedType(cs2.getSpawnedType());
        bsm.setBlockState(cs);
        item.setItemMeta(bsm);
        Spawner.getLocation().getWorld();


        String name = getMessage("spawner-name").replace("{spawner-name}", getTranslation("spawners-name." + cs.getSpawnedType()));
        item = InventoryGenerate.ItemSetName(item, "&a" + name);

        Spawner.setType(Material.AIR);
        Objects.requireNonNull(Spawner.getLocation().getWorld()).dropItem(Spawner.getLocation(), item);
        isDropped = true;
        SpawnersDb.remove(getSpawner().getLocation());
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

    public void setSpawner(Block spawner) {
        Spawner = spawner;
    }

    public String getListType(ITask task) {
        String str = "";
        for (String key : task.getTask().keySet()) {
            str += str != "" ? Config.getMessage("get-list-splitter") : "";
            if (task instanceof TaskBringTheMob)
                str += Config.getMessage("get-list-type").replace("{item}", Config.getTranslation("mobs." + key));
            else
                str += Config.getMessage("get-list-type").replace("{item}", Config.getTranslation("items." + key));

        }
        return str;
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

//    public HashMap<String, ITask> getTask() {
//        return Task;
//    }

    public List<ITask> getTasks() {
        return Tasks;
    }

    public void taskPut(ITask task, String type) {
        // Task.put(type, task);
        Tasks.add(task);
    }

    public boolean isAllTaskComplete() {
        return allTaskComplete;
    }

//    public void setTask(HashMap<String, ITask> task) {
//        Task = task;
//    }
}
