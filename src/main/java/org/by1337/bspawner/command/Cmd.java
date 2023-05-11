package org.by1337.bspawner.command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.by1337.bspawner.Task.ITask;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static org.by1337.bspawner.BSpawner.SpawnersDb;
import static org.by1337.bspawner.BSpawner.instance;
public class Cmd implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("bs.*")) {
                Message.sendMsg(player, Config.getMessage("unknown-command"));
                return true;
            }
            if (args.length == 0) {
                Message.sendMsg(player, Config.getMessage("few-arguments"));
                return true;
            }
            if (args[0].equals("drop")) {
                Block spawner = player.getTargetBlockExact(10);
                if (spawner == null) {
                    Message.sendMsg(player, Config.getMessage("no-block"));
                    return true;
                }
                if (!spawner.getType().equals(Material.SPAWNER)) {
                    Message.sendMsg(player, Config.getMessage("no-spawner"));
                    return true;
                }
                for (Location loc : SpawnersDb.keySet()) {
                    if (SpawnersDb.get(loc).getBlock() == null)
                        continue;
                    if (SpawnersDb.get(loc).getBlock().equals(spawner)) {
                        SpawnersDb.get(loc).MineSpawner();
                        Message.sendMsg(player, Config.getMessage("spawner-mined"));
                        return true;
                    }
                }
                Message.sendMsg(player, Config.getMessage("task-not-generated"));
                return true;
            }
            if (args[0].equals("unlock")) {
                Block spawner = player.getTargetBlockExact(10);
                if (spawner == null) {
                    Message.sendMsg(player, Config.getMessage("no-block"));
                    return true;
                }
                if (!spawner.getType().equals(Material.SPAWNER)) {
                    Message.sendMsg(player, Config.getMessage("no-spawner"));
                    return true;
                }
                for (Location loc : SpawnersDb.keySet()) {
                    if (SpawnersDb.get(loc).getBlock() == null)
                        continue;
                    if (SpawnersDb.get(loc).getBlock().equals(spawner)) {
                        for (ITask task : SpawnersDb.get(loc).getTasks())
                            task.setTaskActive(true);
                        Message.sendMsg(player, Config.getMessage("task-unlocked"));
                        return true;
                    }
                }
                Message.sendMsg(player, Config.getMessage("task-not-generated"));
                return true;
            }
            if (args[0].equals("complete")) {
                Block spawner = player.getTargetBlockExact(10);
                if (spawner == null) {
                    Message.sendMsg(player, Config.getMessage("no-block"));
                    return true;
                }
                if (!spawner.getType().equals(Material.SPAWNER)) {
                    Message.sendMsg(player, Config.getMessage("no-spawner"));
                    return true;
                }
                for (Location loc : SpawnersDb.keySet()) {
                    if (SpawnersDb.get(loc).getBlock() == null)
                        continue;
                    if (SpawnersDb.get(loc).getBlock().equals(spawner)) {
                        for (ITask task : SpawnersDb.get(loc).getTasks())
                            task.setComplete();
                        Message.sendMsg(player, Config.getMessage("all-task-completed"));
                        return true;
                    }
                }
                Message.sendMsg(player, Config.getMessage("task-not-generated"));
                return true;
            }

            if(args[0].equals("reload")){
                if(args.length == 2){
                    if(args[1].equals("-force")){
                        instance.reloadConfig();
                        Config.LoadYamlConfiguration();
                        try {
                            Config.LoadSpawners();
                        } catch (IOException e) {
                            Message.error(e.getMessage());
                        }
                        Message.sendMsg(player, Config.getMessage("config-reloaded-force"));
                        return true;
                    }
                }
                instance.reloadConfig();
                Config.LoadYamlConfiguration();
                try {
                    Config.saveSpawnerDb();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Message.sendMsg(player, Config.getMessage("config-reloaded"));
                return true;
            }

        }

        return false;
    }
}
