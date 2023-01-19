package org.by1337.bspawner.util;

import org.bukkit.Location;
import org.by1337.bspawner.Task.TaskBreakBlock;
import org.by1337.bspawner.Task.TaskBringItems;
import org.by1337.bspawner.Task.TaskBringTheMob;
import org.by1337.bspawner.Task.TaskPlaceBlock;


import java.util.HashMap;

import static org.by1337.bspawner.util.Config.spawnerDb;

public class ConfigUtil {///TaskBringTheMob
    public static void LoadBringItems(SpawnerTask spawnerTask, String SpawnerId){//type-bring-items
        for(String slot : spawnerDb.getConfigurationSection("spawners." + SpawnerId + ".task.type-bring-items").getKeys(false)){
            int IntSlot = Integer.parseInt(slot);
            HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//mat -> bring:0, brought:0
            for(String mat : spawnerDb.getConfigurationSection("spawners." + SpawnerId + ".task.type-bring-items." + slot).getKeys(false)){
                if(mat.equals("TaskActive") || mat.equals("TaskCompleted") || mat.equals("TaskId"))
                    continue;
                HashMap<String, Integer> item = new HashMap<>();
                int bring = spawnerDb.getInt("spawners." + SpawnerId + ".task.type-bring-items." + slot + "." + mat + ".bring");
                int brought = spawnerDb.getInt("spawners." + SpawnerId + ".task.type-bring-items." + slot + "." + mat + ".brought");
                item.put("bring", bring);
                item.put("brought", brought);
                taskMap.put(mat, item);
            }
            String id = spawnerDb.getString("spawners." + SpawnerId + ".task.type-bring-items." + slot + ".TaskId");
            TaskBringItems taskBringItems = new TaskBringItems(taskMap, IntSlot, id);
            boolean active = spawnerDb.getBoolean("spawners." + SpawnerId + ".task.type-bring-items." + slot + ".TaskActive");
            boolean completed = spawnerDb.getBoolean("spawners." + SpawnerId + ".task.type-bring-items." + slot + ".TaskCompleted");
            if(completed)
                taskBringItems.setComplete();
            taskBringItems.setTaskActive(active);
            spawnerTask.taskPut(taskBringItems, "type-bring-items");
        }
    }

    public static void LoadBringTheMob(SpawnerTask spawnerTask, String SpawnerId, Location location){///TaskBringTheMob
        for(String slot : spawnerDb.getConfigurationSection("spawners." + SpawnerId + ".task.type-bring-the-mob").getKeys(false)){
            int IntSlot = Integer.parseInt(slot);
            HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//mob type -> amount:0, completed:0 | 0 false 1 true
            for(String mob : spawnerDb.getConfigurationSection("spawners." + SpawnerId + ".task.type-bring-the-mob." + slot).getKeys(false)){
                if(mob.equals("TaskActive") || mob.equals("TaskCompleted") || mob.equals("TaskId"))
                    continue;
                HashMap<String, Integer> item = new HashMap<>();
                int amount = spawnerDb.getInt("spawners." + SpawnerId + ".task.type-bring-the-mob." + slot + "." + mob + ".amount");
                int completed = spawnerDb.getInt("spawners." + SpawnerId + ".task.type-bring-the-mob." + slot + "." + mob + ".completed");
                item.put("amount", amount);
                item.put("completed", completed);
                taskMap.put(mob, item);
            }
            String id = spawnerDb.getString("spawners." + SpawnerId + ".task.type-bring-the-mob." + slot + ".TaskId");
            TaskBringTheMob taskBringTheMob = new TaskBringTheMob(taskMap, location,  IntSlot, id);
            boolean active = spawnerDb.getBoolean("spawners." + SpawnerId + ".task.type-bring-the-mob." + slot + ".TaskActive");
            boolean completed = spawnerDb.getBoolean("spawners." + SpawnerId + ".task.type-bring-the-mob." + slot + ".TaskCompleted");
            if(completed)
                taskBringTheMob.setComplete();
            taskBringTheMob.setTaskActive(active);
            spawnerTask.taskPut(taskBringTheMob, "type-bring-the-mob");
        }
    }

    public static void LoadPlaceBlock(SpawnerTask spawnerTask, String key, Location location){///TaskPlaceBlock
        for(String slot : spawnerDb.getConfigurationSection("spawners." + key + ".task.type-place-block").getKeys(false)){
            int IntSlot = Integer.parseInt(slot);
            HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//block -> amount:0, put:0
            for(String mat : spawnerDb.getConfigurationSection("spawners." + key + ".task.type-place-block." + slot).getKeys(false)){
                if(mat.equals("TaskActive") || mat.equals("TaskCompleted") || mat.equals("TaskId"))
                    continue;
                HashMap<String, Integer> item = new HashMap<>();
                int amount = spawnerDb.getInt("spawners." + key + ".task.type-place-block." + slot + "." + mat + ".amount");
                int put = spawnerDb.getInt("spawners." + key + ".task.type-place-block." + slot + "." + mat + ".put");
                item.put("amount", amount);
                item.put("put", put);
                taskMap.put(mat, item);
            }
            String id = spawnerDb.getString("spawners." + key + ".task.type-place-block." + slot + ".TaskId");
            TaskPlaceBlock taskPlaceBlock = new TaskPlaceBlock(taskMap, location, IntSlot, id);
            boolean active = spawnerDb.getBoolean("spawners." + key + ".task.type-place-block." + slot + ".TaskActive");
            boolean completed = spawnerDb.getBoolean("spawners." + key + ".task.type-place-block." + slot + ".TaskCompleted");
            if(completed)
                taskPlaceBlock.setComplete();
            taskPlaceBlock.setTaskActive(active);
            spawnerTask.taskPut(taskPlaceBlock, "type-place-block");
        }
    }

    public static void LoadBreakBlock(SpawnerTask spawnerTask, String key, Location location){///TaskBreakBlock
        for(String slot : spawnerDb.getConfigurationSection("spawners." + key + ".task.type-break-block").getKeys(false)){
            int IntSlot = Integer.parseInt(slot);
            HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//block -> amount:0, broken:0
            for(String mat : spawnerDb.getConfigurationSection("spawners." + key + ".task.type-break-block." + slot).getKeys(false)){
                if(mat.equals("TaskActive") || mat.equals("TaskCompleted") || mat.equals("TaskId"))
                    continue;
                HashMap<String, Integer> item = new HashMap<>();
                int amount = spawnerDb.getInt("spawners." + key + ".task.type-break-block." + slot + "." + mat + ".amount");
                int broken = spawnerDb.getInt("spawners." + key + ".task.type-break-block." + slot + "." + mat + ".broken");
                item.put("amount", amount);
                item.put("broken", broken);
                taskMap.put(mat, item);
            }
            String id = spawnerDb.getString("spawners." + key + ".task.type-break-block." + slot + ".TaskId");
            TaskBreakBlock taskBreakBlock = new TaskBreakBlock(taskMap, location, IntSlot, id);
            boolean active = spawnerDb.getBoolean("spawners." + key + ".task.type-break-block." + slot + ".TaskActive");
            boolean completed = spawnerDb.getBoolean("spawners." + key + ".task.type-break-block." + slot + ".TaskCompleted");
            if(completed)
                taskBreakBlock.setComplete();
            taskBreakBlock.setTaskActive(active);
            spawnerTask.taskPut(taskBreakBlock, "type-break-block");
        }
    }
}
