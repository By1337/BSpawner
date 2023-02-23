package org.by1337.bspawner.Listener.TaskListener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.by1337.bspawner.Task.ITask;
import org.by1337.bspawner.Task.TaskBreakBlock;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;

import java.util.HashMap;
import java.util.Objects;

import static org.by1337.bspawner.BSpawner.SpawnersDb;
import static org.by1337.bspawner.BSpawner.instance;

public class BlockBreak implements Listener {

    @EventHandler
    public void onPlaced(BlockBreakEvent e){
        if(e.isCancelled())
            return;
        for(SpawnerTask spawnerTask : SpawnersDb.values()){
            for(ITask task : spawnerTask.getTasks()){
                if(task instanceof TaskBreakBlock){
                    if(task.isTaskCompleted())
                        continue;
                    if(!task.isTaskActive())
                        continue;
                    if(spawnerTask.getSpawner() == null)
                        continue;
                    if(!spawnerTask.getSpawner().getLocation().getChunk().isLoaded())
                        continue;
                    if(!Objects.equals(spawnerTask.getSpawner().getLocation().getWorld(), e.getBlock().getLocation().getWorld()))
                        continue;
                    double dist = e.getBlock().getLocation().distance(spawnerTask.getSpawner().getLocation());
                    if(dist > instance.getConfig().getInt("distance"))
                        continue;
                    HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();
                    if(taskMap.containsKey(String.valueOf(e.getBlock().getType()))){
                        int broken = taskMap.get(String.valueOf(e.getBlock().getType())).get("broken");
                        if(broken == taskMap.get(String.valueOf(e.getBlock().getType())).get("amount"))
                            continue;
                        taskMap.get(String.valueOf(e.getBlock().getType())).put("broken", broken + 1);
                        e.setDropItems(false);
                        if(task.taskCompletionCheck()){
                            Message.sendAllNear(Config.getMessage("task-completed"),spawnerTask.getSpawner().getLocation());
                            spawnerTask.ActivateNextTask(task);
                        }

                    }
                }
            }
        }
    }
}
