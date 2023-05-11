package org.by1337.bspawner.util;

import org.bukkit.Location;
import org.by1337.bspawner.Task.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.by1337.bspawner.BSpawner.instance;

public class TasksGenerate {

    public static ITask getRandomTask(int slot, Location loc){
        Set<String> SetTaskTypes = instance.getConfig().getConfigurationSection("tasks").getKeys(false);
        List<String> taskTypes = new ArrayList<>(SetTaskTypes);

        String taskType = taskTypes.get(ThreadLocalRandom.current().nextInt(taskTypes.size()));
        switch (taskType){
            case "type-bring-items": {
                return bringItemsGenerate(slot);
            }
            case "type-bring-the-mob":{
                return taskBringTheMob(loc,slot);
            }
            case "type-place-block":{
                return taskPlaceBlock(loc,slot);
            }
            case "type-break-block":{
                return taskBreakBlock(loc,slot);
            }
            default:{
                Message.error(taskType + " unknown task type!");
                return bringItemsGenerate(slot);
            }
        }
    }
    public static void Generate(SpawnerTask spawnerTask) {
        int firstSlot = instance.getConfig().getIntegerList("tasks-slots").get(0);

        for(Integer slot : instance.getConfig().getIntegerList("tasks-slots")){
            ITask task = getRandomTask(slot, spawnerTask.getBlock().getLocation());
            if(slot == firstSlot)
                task.setTaskActive(true);
            spawnerTask.taskPut(task, task.getTaskType());
        }
    }

    public static TaskBringItems bringItemsGenerate (int slot){
        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-bring-items").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//mat -> bring:0, brought:0

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-bring-items." + task).getKeys(false)){
            instance.getConfig().getString("items." + key + ".material");
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("bring", instance.getConfig().getInt("tasks.type-bring-items." + task + "." + key));
            taskMap.get(key).put("brought", 0);
        }

        return new TaskBringItems(taskMap,slot, task);
    }

    public static TaskBringTheMob taskBringTheMob(Location loc,int slot){
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();

        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-bring-the-mob").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-bring-the-mob." + task).getKeys(false)){
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("amount", instance.getConfig().getInt("tasks.type-bring-the-mob." + task + "." + key));
            taskMap.get(key).put("completed", 0);
        }

        return new TaskBringTheMob(taskMap, loc,slot, task);
    }

    public static TaskPlaceBlock taskPlaceBlock(Location loc,int slot){
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();

        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-place-block").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-place-block." + task).getKeys(false)){
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("amount", instance.getConfig().getInt("tasks.type-place-block." + task + "." + key));
            taskMap.get(key).put("put", 0);
        }
        return new TaskPlaceBlock(taskMap,slot, task);
    }
    public static TaskBreakBlock taskBreakBlock(Location loc,int slot){
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();

        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-break-block").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-break-block." + task).getKeys(false)){
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("amount", instance.getConfig().getInt("tasks.type-break-block." + task + "." + key));
            taskMap.get(key).put("broken", 0);
        }

        return new TaskBreakBlock(taskMap,slot, task);
    }
}
