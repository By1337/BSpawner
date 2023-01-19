package org.by1337.bspawner.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.by1337.bspawner.Task.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static org.by1337.bspawner.BSpawner.instance;

public class TasksGenerate {

  //  @SuppressWarnings("unchecked")
    public static void Generate(SpawnerTask spawnerTask) {
        Set<String> SetTaskTypes = instance.getConfig().getConfigurationSection("tasks").getKeys(false);
        List<String> taskTypes = new ArrayList<>(SetTaskTypes);
        int firstSlot = instance.getConfig().getIntegerList("tasks-slots").get(0);
        for(Integer slot : instance.getConfig().getIntegerList("tasks-slots")){
            String taskType = taskTypes.get(ThreadLocalRandom.current().nextInt(taskTypes.size()));
            switch (taskType){
                case "type-bring-items": {
                    TaskBringItems task = bringItemsGenerate(slot);
                    if(slot == firstSlot)
                        task.setTaskActive(true);
                    spawnerTask.taskPut(task, "type-bring-items");

                    break;
                }
                case "type-bring-the-mob":{
                    TaskBringTheMob task = taskBringTheMob(spawnerTask.getSpawner().getLocation(),slot);
                    if(slot == firstSlot)
                        task.setTaskActive(true);
                    spawnerTask.taskPut(task, "type-bring-the-mob");

                    break;
                }
                case "type-place-block":{
                    TaskPlaceBlock task = taskPlaceBlock(spawnerTask.getSpawner().getLocation(),slot);
                    if(slot == firstSlot)
                        task.setTaskActive(true);
                    spawnerTask.taskPut(task, "type-place-block");

                    break;
                }
                case "type-break-block":{
                    TaskBreakBlock task = taskBreakBlock(spawnerTask.getSpawner().getLocation(),slot);
                    if(slot == firstSlot)
                        task.setTaskActive(true);
                    spawnerTask.taskPut(task, "type-break-block");

                    break;
                }
            }
        }

      //  return Task;
    }

    private static TaskBringItems bringItemsGenerate (int slot){
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
           // taskMap.get(key).put("id", Integer.valueOf(task));
        }

        return new TaskBringItems(taskMap,slot, task);
    }

    private static TaskBringTheMob taskBringTheMob(Location loc,int slot){
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//mob type -> amount:0, completed:0 | 0 false 1 true

        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-bring-the-mob").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-bring-the-mob." + task).getKeys(false)){
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("amount", instance.getConfig().getInt("tasks.type-bring-the-mob." + task + "." + key));
            taskMap.get(key).put("completed", 0);
           // taskMap.get(key).put("id", Integer.valueOf(task));
        }

        return new TaskBringTheMob(taskMap, loc,slot, task);
    }

    private static TaskPlaceBlock taskPlaceBlock(Location loc,int slot){
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//block -> amount:0, put:0

        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-place-block").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-place-block." + task).getKeys(false)){
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("amount", instance.getConfig().getInt("tasks.type-place-block." + task + "." + key));
            taskMap.get(key).put("put", 0);
           // taskMap.get(key).put("id", Integer.valueOf(task));
        }

        return new TaskPlaceBlock(taskMap, loc,slot, task);
    }
    private static TaskBreakBlock taskBreakBlock(Location loc,int slot){
        HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//block -> amount:0, broken:0

        Set<String> tasksBringItems = instance.getConfig().getConfigurationSection("tasks.type-break-block").getKeys(false);
        List<String> tasksBringItemsList = new ArrayList<>(tasksBringItems);
        String task = tasksBringItemsList.get(ThreadLocalRandom.current().nextInt(tasksBringItemsList.size()));

        for(String key : instance.getConfig().getConfigurationSection("tasks.type-break-block." + task).getKeys(false)){
            if(key.equals("info"))
                continue;
            taskMap.put(key, new HashMap<>());
            taskMap.get(key).put("amount", instance.getConfig().getInt("tasks.type-break-block." + task + "." + key));
            taskMap.get(key).put("broken", 0);
          //  taskMap.get(key).put("id", Integer.valueOf(task));
        }

        return new TaskBreakBlock(taskMap, loc,slot, task);
    }
}
