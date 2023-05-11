package org.by1337.bspawner.Listener.TaskListener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.by1337.bspawner.Task.ITask;
import org.by1337.bspawner.Task.TaskPlaceBlock;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;

import java.util.HashMap;
import java.util.Objects;

import static org.by1337.bspawner.BSpawner.SpawnersDb;
import static org.by1337.bspawner.BSpawner.instance;

public class BlockPlace implements Listener {

    @EventHandler
    public void onPlaced(BlockPlaceEvent e){
        if(e.isCancelled())
            return;
        for(SpawnerTask spawnerTask : SpawnersDb.values()){
            for(ITask task : spawnerTask.getTasks()){
                if(task instanceof TaskPlaceBlock){
                    if(task.isTaskCompleted())
                        continue;
                    if(!task.isTaskActive())
                        continue;
                    if(spawnerTask.getBlock() == null)
                        continue;
                    if(!spawnerTask.getBlock().getLocation().getChunk().isLoaded())
                        continue;
                    if(!Objects.equals(spawnerTask.getBlock().getLocation().getWorld(), e.getBlock().getLocation().getWorld()))
                        continue;
                     double dist = e.getBlock().getLocation().distance(spawnerTask.getBlock().getLocation());
                     if(dist > instance.getConfig().getInt("distance"))
                         continue;
                    HashMap<String, HashMap<String, Integer>> taskMap = task.getTask();
                    if(taskMap.containsKey(String.valueOf(e.getBlock().getType()))){
                        int put = taskMap.get(String.valueOf(e.getBlock().getType())).get("put");
                        if(put == taskMap.get(String.valueOf(e.getBlock().getType())).get("amount"))
                            continue;
                        taskMap.get(String.valueOf(e.getBlock().getType())).put("put", put + 1);
                        e.getBlock().setType(Material.AIR);
                        if(task.taskCompletionCheck()){
                            Message.sendAllNear(Config.getMessage("task-completed"),spawnerTask.getBlock().getLocation());
                            spawnerTask.ActivateNextTask(task);
                        }

                    }
                }
            }
        }
    }
}
