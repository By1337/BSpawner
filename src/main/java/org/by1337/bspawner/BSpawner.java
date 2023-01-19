package org.by1337.bspawner;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.by1337.bspawner.Listener.InventoryClick.InventoryListener;
import org.by1337.bspawner.Listener.SpawnerListener;
import org.by1337.bspawner.Listener.TaskListener.BlockBreak;
import org.by1337.bspawner.Listener.TaskListener.BlockPlace;
import org.by1337.bspawner.Task.ITask;
import org.by1337.bspawner.Task.TaskBringTheMob;
import org.by1337.bspawner.command.Cmd;
import org.by1337.bspawner.command.CmdCompleter;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

import static org.by1337.bspawner.util.Config.*;


public final class BSpawner extends JavaPlugin implements Runnable {

    public static BSpawner instance;
    public static HashMap<Location, SpawnerTask> SpawnersDb = new HashMap<>();
    int autoSaveDelay = 30 * 60 * 5;

    @Override
    public void onEnable() {
        instance = this;
        File config = new File(this.getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            this.getLogger().info("Creating new config file, please wait");
            this.getConfig().options().copyDefaults(true);

            this.saveDefaultConfig();
        }
        LoadYamlConfiguration();
        try {
            LoadSpawners();
        } catch (IOException e) {
            Message.error(e.getMessage());
        }
        getServer().getPluginManager().registerEvents(new SpawnerListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
        getServer().getPluginManager().registerEvents(new BlockPlace(), this);
        getServer().getPluginManager().registerEvents(new BlockBreak(), this);
        Objects.requireNonNull(this.getCommand("bspawner")).setExecutor(new Cmd());
        Objects.requireNonNull(this.getCommand("bspawner")).setTabCompleter((new CmdCompleter()));
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, this, 4L, 4L);

    }

    @Override
    public void onDisable() {
        try {
            saveSpawnerDb();
        } catch (IOException e) {
            Message.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        for (SpawnerTask spawnerTask : SpawnersDb.values()) {
            for (ITask task : spawnerTask.getTasks()) {
                if (task instanceof TaskBringTheMob) {
                    if (task.isTaskCompleted())
                        continue;
                    if(spawnerTask.getSpawner() == null)
                        continue;
                    if(!task.isTaskActive())
                        continue;
                    if (task.taskCompletionCheck()) {
                        Message.sendAllNear("&aЗадание выполнено!", spawnerTask.getSpawner().getLocation());
                        spawnerTask.ActivateNextTask(task);

                    }


                }
            }
        }
        if (autoSaveDelay <= 0) {
            autoSaveDelay =  30 * 60 * 5;
            try {
                saveSpawnerDb();
                Message.logger("saving the database successfully!");
            } catch (IOException e) {
                Message.error(e.getMessage());
            }
        } else
            autoSaveDelay--;

    }
}
