package org.by1337.bspawner.util;

import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.by1337.bspawner.Task.*;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import static org.by1337.bspawner.BSpawner.*;

public class Config{
     static FileConfiguration translations;
     static File fileTranslations;
     static FileConfiguration spawnerDb;
     static File fileSpawnerDb;
    static FileConfiguration message;
    static File fileMessage;

    public static String getTranslation(String path){
        if(translations.getString(path) == null){
            return Message.messageBuilder("&cПеревода с таким пути нет!, There is no translation from this path!");
        }
            return Message.messageBuilder(translations.getString(path));
    }
    public static String getMessage(String path){
        if(message.getString(path) == null){
            return Message.messageBuilder("&cСообщения с таким пути нет!, There are no messages with this path!");
        }
        return Message.messageBuilder(message.getString(path));
    }
    public static void LoadYamlConfiguration(){
        fileTranslations = new File(instance.getDataFolder() + File.separator + "translation.yml");
        if (!fileTranslations.exists()) {
            instance.saveResource("translation.yml", true);
        }
        translations = YamlConfiguration.loadConfiguration(fileTranslations);

        fileSpawnerDb = new File(instance.getDataFolder() + File.separator + "spawnerdb.yml");
        if (!fileSpawnerDb.exists()) {
            instance.saveResource("spawnerdb.yml", true);
        }
        spawnerDb = YamlConfiguration.loadConfiguration(fileSpawnerDb);

        fileMessage = new File(instance.getDataFolder() + File.separator + "message.yml");
        if (!fileMessage.exists()) {
            instance.saveResource("message.yml", true);
        }
        message = YamlConfiguration.loadConfiguration(fileMessage);
    }
    public static void saveSpawnerDb() throws IOException {
        spawnerDb.set("spawners", null);
        for(Location loc : SpawnersDb.keySet()){
            SpawnerTask spawnerTask = SpawnersDb.get(loc);
            if(spawnerTask.isDropped())
                continue;
            String spawnerId = spawnerTask.getSpawnerId();
            if(loc.getWorld() == null){
                Message.error(loc + " is null!");
                continue;
            }

            spawnerDb.set("spawners." + spawnerId + ".location.x", loc.getBlockX());
            spawnerDb.set("spawners." + spawnerId + ".location.y", loc.getBlockY());
            spawnerDb.set("spawners." + spawnerId + ".location.z", loc.getBlockZ());
            spawnerDb.set("spawners." + spawnerId + ".location.world", loc.getWorld().getName());
            spawnerDb.set("spawners." + spawnerId + ".spawner-id", spawnerTask.getSpawnerId());
            for(ITask task : SpawnersDb.get(loc).getTasks()){
                spawnerDb.set("spawners." + spawnerId + ".task." + task.getTaskType() + "." + task.getSlot() + ".TaskActive", task.isTaskActive());
                spawnerDb.set("spawners." + spawnerId + ".task." + task.getTaskType() + "." + task.getSlot() + ".TaskCompleted", task.isTaskCompleted());
                spawnerDb.set("spawners." + spawnerId + ".task." + task.getTaskType() + "." + task.getSlot() + ".TaskId", task.getConfigId());

                for (String key : task.getTask().keySet()) {
                    spawnerDb.set("spawners." + spawnerId + ".task." + task.getTaskType() + "." + task.getSlot() + "." + key + "." + task.getKey()[0], task.getTask().get(key).get(task.getKey()[0]));
                    spawnerDb.set("spawners." + spawnerId + ".task." + task.getTaskType() + "." + task.getSlot() + "." + key + "." + task.getKey()[1], task.getTask().get(key).get(task.getKey()[1]));
                }
            }

        }
        spawnerDb.save(fileSpawnerDb);
    }
    public static void LoadSpawners() throws IOException {
        SpawnersDb.clear();
        if(spawnerDb.getConfigurationSection("spawners") == null)
            return;
        int loaded = 0;
        for(String SpawnerId : spawnerDb.getConfigurationSection("spawners").getKeys(false)){
            loaded++;
            String spawnerId = spawnerDb.getString("spawners." + SpawnerId + ".spawner-id");
            World world = Bukkit.getWorld(String.valueOf(spawnerDb.get("spawners." + SpawnerId + ".location.world")));
            double x = spawnerDb.getDouble("spawners." + SpawnerId + ".location.x");
            double y = spawnerDb.getDouble("spawners." + SpawnerId + ".location.y");
            double z = spawnerDb.getDouble("spawners." + SpawnerId + ".location.z");
            Location loc = new Location(world, x, y, z);
            SpawnerTask spawnerTask = new SpawnerTask(spawnerId);

            for(String taskId : spawnerDb.getConfigurationSection("spawners." + SpawnerId + ".task").getKeys(false)){
                if(taskId.equals("type-bring-items")){
                    ConfigUtil.LoadBringItems(spawnerTask, SpawnerId); continue;
                }
                if(taskId.equals("type-bring-the-mob")){
                    ConfigUtil.LoadBringTheMob(spawnerTask, SpawnerId, loc); continue;
                }
                if(taskId.equals("type-place-block")){
                    ConfigUtil.LoadPlaceBlock(spawnerTask, SpawnerId, loc); continue;
                }
                if(taskId.equals("type-break-block")){
                    ConfigUtil.LoadBreakBlock(spawnerTask, SpawnerId, loc); continue;
                }
            }
            SpawnersDb.put(loc, spawnerTask);
        }
        Message.logger("Loaded " + loaded + " spawners!");

    }

}
