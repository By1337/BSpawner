package org.by1337.bspawner.Task;

import java.util.HashMap;
public class TaskBringItems implements ITask {

    private HashMap<String, HashMap<String, Integer>> taskMap;//mat -> bring:0, brought:0
    private boolean isTaskCompleted = false;
    private boolean TaskActive = false;
    private final String TaskId;

    int slot;
    public TaskBringItems(HashMap<String, HashMap<String, Integer>> taskMap, int slot, String TaskId) {
        this.taskMap = taskMap;
        this.slot = slot;
        this.TaskId = TaskId;
    }
    @Override
    public String[] getKey() {
        return new String[]{"bring", "brought"};
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
        return "type-bring-items";
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
        boolean isChecked = false;
        for (String key : taskMap.keySet()) {
            if (getBring(key) == getBrought(key)) {
                isChecked = true;
            } else {
                isChecked = false;
                break;
            }

        }
        isTaskCompleted = isChecked;
        return isChecked;
    }
    private int getBring(String mat) {
        if (!taskMap.containsKey(mat))
            return -1;
        return taskMap.get(mat).get("bring");

    }

    private int getBrought(String mat) {
        if (!taskMap.containsKey(mat))
            return -1;
        return taskMap.get(mat).get("brought");

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
    public void setComplete() {//mat -> bring:0, brought:0
        for(String mat : taskMap.keySet()){
            int bring = taskMap.get(mat).get("bring");
            taskMap.get(mat).put("brought", bring);
            setTaskActive(true);
            taskCompletionCheck();
        }
    }
}
