package org.by1337.bspawner.Task;

import java.util.HashMap;
import java.util.Objects;

public class TaskBreakBlock implements ITask{

    private HashMap<String, HashMap<String, Integer>> taskMap = new HashMap<>();//block -> amount:0, broken:0

    private boolean isTaskCompleted = false;

    private final String TaskId;
    private boolean TaskActive = false;
    private final int slot;

    public TaskBreakBlock(HashMap<String, HashMap<String, Integer>> taskMap, int slot, String TaskId) {
        this.taskMap = taskMap;
        this.slot = slot;
        this.TaskId = TaskId;
    }

    @Override
    public String getTaskId() {
        return TaskId;
    }

    @Override
    public boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    @Override
    public String getTaskType() {
        return "type-break-block";
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
        boolean isCheck = false;
        for(String key : taskMap.keySet()){
            if(Objects.equals(taskMap.get(key).get("amount"), taskMap.get(key).get("broken"))){
                isCheck = true;
            }else {
                isCheck = false;
                break;
            }
        }
        isTaskCompleted = isCheck;
        return isCheck;
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
    public void setComplete() {//block -> amount:0, broken:0
        for(String mat : taskMap.keySet()){
            int amount = taskMap.get(mat).get("amount");
            taskMap.get(mat).put("broken", amount);
            setTaskActive(true);
            taskCompletionCheck();
        }
    }
}
