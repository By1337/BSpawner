package org.by1337.bspawner.Task;

import java.util.HashMap;
import java.util.Objects;

public class TaskPlaceBlock implements ITask{

    private HashMap<String, HashMap<String, Integer>> taskMap;//block -> amount:0, put:0

    private boolean isTaskCompleted = false;
    private boolean TaskActive = false;
    private final int slot;
    private final String TaskId;

    public TaskPlaceBlock(HashMap<String, HashMap<String, Integer>> taskMap, int slot, String TaskId) {
        this.taskMap = taskMap;
        this.slot = slot;
        this.TaskId = TaskId;
    }
    @Override
    public String[] getKey() {
        return new String[]{"amount", "put"};
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
        return "type-place-block";
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
        for(String mat : taskMap.keySet()){
            if(Objects.equals(taskMap.get(mat).get("amount"), taskMap.get(mat).get("put")))
                isCheck = true;
            else {
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
    public void setComplete() {//block -> amount:0, put:0
        for(String mat : taskMap.keySet()){
            int amount = taskMap.get(mat).get("amount");
            taskMap.get(mat).put("put", amount);
            setTaskActive(true);
            taskCompletionCheck();
        }
    }
}
