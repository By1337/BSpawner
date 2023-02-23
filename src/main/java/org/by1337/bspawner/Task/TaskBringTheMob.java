package org.by1337.bspawner.Task;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.by1337.bspawner.BSpawner.instance;

public class TaskBringTheMob implements ITask{


    private HashMap<String, HashMap<String, Integer>> taskMap;//mob type -> amount:0, completed:0 | 0 false 1 true

    private boolean isTaskCompleted = false;
    private final Location locSpawner;
    private boolean TaskActive = false;
    private final String TaskId;
    int slot;
    public TaskBringTheMob(HashMap<String, HashMap<String, Integer>> taskMap, Location locSpawner, int slot, String TaskId) {
        this.taskMap = taskMap;
        this.locSpawner = locSpawner;
        this.slot = slot;
        this.TaskId = TaskId;
    }

    @Override
    public String[] getKey() {
        return new String[]{"amount", "completed"};
    }
    @Override
    public String getConfigId() {
        return TaskId;
    }

    @Override
    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    @Override
    public String getTaskType() {
        return "type-bring-the-mob";
    }

    @Override
    public HashMap<String, HashMap<String, Integer>> getTask() {
        return taskMap;
    }
    @Override
    public void setTask(HashMap<String, HashMap<String, Integer>> task) {
        taskMap = task;
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public boolean taskCompletionCheck() {
        if(!locSpawner.getChunk().isLoaded() || isTaskCompleted)
            return false;
        boolean isCompleted = false;
        List<Entity> allMobs = new ArrayList<>();
        for(String key : taskMap.keySet()){
            EntityType mob;
            try {
                mob = EntityType.valueOf(key);
            }catch (IllegalArgumentException e){
                Message.error(String.format(Config.getMessage("entity-error"), key));
                continue;
            }

            List<Entity> mobs = mobChecker(mob);
            if(mobs.size() != 0){
                int amount = taskMap.get(key).get("amount");

                if(amount <= mobs.size()){
                    allMobs.addAll(mobs);
                    isCompleted = true;
                }
            }else{
                isCompleted = false;
                break;
            }

        }
        if(isCompleted){
            for(Entity entity : allMobs){
                taskMap.get(String.valueOf(entity.getType())).put("completed", 1);
                entity.remove();
            }
        }
        isTaskCompleted = isCompleted;
        return isCompleted;
    }

    private List<Entity> mobChecker(EntityType mob){
        List<Entity> list = new ArrayList<>();
        if(locSpawner == null || locSpawner.getWorld() == null)
            return list;
        for(Entity entity : locSpawner.getWorld().getNearbyEntities(locSpawner, instance.getConfig().getInt("distance"), instance.getConfig().getInt("distance"), instance.getConfig().getInt("distance"))){
            if(entity.getType().equals(mob)){
                list.add(entity);
            }
        }
        return list;
    }
    @Override
    public boolean isTaskActive() {
        return TaskActive;
    }

    @Override
    public void setTaskActive(boolean active) {
        TaskActive = active;
    }
    @Override
    public void setComplete() {//mob type -> amount:0, completed:0 | 0 false 1 true
        for(String mat : taskMap.keySet()){
            taskMap.get(mat).put("completed", 1);
            setTaskActive(true);
            isTaskCompleted = true;
        }
    }
}
