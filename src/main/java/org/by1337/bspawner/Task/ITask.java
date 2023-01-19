package org.by1337.bspawner.Task;

import java.util.HashMap;

public interface ITask {

    boolean isTaskCompleted();

    String getTaskType();

    HashMap<String, HashMap<String, Integer>> getTask();
    void setTask(HashMap<String, HashMap<String, Integer>> task);
    int slot();

    boolean taskCompletionCheck();
    boolean isTaskActive();
    void setTaskActive(boolean active);
    void setComplete();
    String getTaskId();

}
