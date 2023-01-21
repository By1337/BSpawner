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
                Message.sendMsg(player, "&cНеизвестная команда!");
                return true;
            }
            if (args.length == 0) {
                Message.sendMsg(player, "&cМало аргументов");
                return true;
            }
            if (args[0].equals("drop")) {
                Block spawner = player.getTargetBlockExact(10);
                if (spawner == null) {
                    Message.sendMsg(player, "&cВы не смотрите на блок!");
                    return true;
                }
                if (!spawner.getType().equals(Material.SPAWNER)) {
                    Message.sendMsg(player, "&cВы смотрите не на спавнер!");
                    return true;
                }
                for (Location loc : SpawnersDb.keySet()) {
                    if (SpawnersDb.get(loc).getSpawner() == null)
                        continue;
                    if (SpawnersDb.get(loc).getSpawner().equals(spawner)) {
                        SpawnersDb.get(loc).MineSpawner();
                        Message.sendMsg(player, "&aСпавнер был добыт!");
                        return true;
                    }
                }
                Message.sendMsg(player, "&cЗадания в этом спавнере ещё не сгенерированы");
                return true;
            }
            if (args[0].equals("unlock")) {
                Block spawner = player.getTargetBlockExact(10);
                if (spawner == null) {
                    Message.sendMsg(player, "&cВы не смотрите на блок!");
                    return true;
                }
                if (!spawner.getType().equals(Material.SPAWNER)) {
                    Message.sendMsg(player, "&cВы смотрите не на спавнер!");
                    return true;
                }
                for (Location loc : SpawnersDb.keySet()) {
                    if (SpawnersDb.get(loc).getSpawner() == null)
                        continue;
                    if (SpawnersDb.get(loc).getSpawner().equals(spawner)) {
                        for (ITask task : SpawnersDb.get(loc).getTasks())
                            task.setTaskActive(true);
                        Message.sendMsg(player, "&aВсе задания в спавнере были разблокированы!");
                        return true;
                    }
                }
                Message.sendMsg(player, "&cЗадания в этом спавнере ещё не сгенерированы");
                return true;
            }
            if (args[0].equals("complete")) {
                Block spawner = player.getTargetBlockExact(10);
                if (spawner == null) {
                    Message.sendMsg(player, "&cВы не смотрите на блок!");
                    return true;
                }
                if (!spawner.getType().equals(Material.SPAWNER)) {
                    Message.sendMsg(player, "&cВы смотрите не на спавнер!");
                    return true;
                }
                for (Location loc : SpawnersDb.keySet()) {
                    if (SpawnersDb.get(loc).getSpawner() == null)
                        continue;
                    if (SpawnersDb.get(loc).getSpawner().equals(spawner)) {
                        for (ITask task : SpawnersDb.get(loc).getTasks())
                            task.setComplete();
                        Message.sendMsg(player, "&aВсе задания в спавнере были выполнены!");
                        return true;
                    }
                }
                Message.sendMsg(player, "&cЗадания в этом спавнере ещё не сгенерированы");
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
                        Message.sendMsg(player, "&aКонфиг файлы перезагружены! &e с флагом -force");
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
                Message.sendMsg(player, "&aКонфиг файлы перезагружены!");
                return true;
            }

        }

        return false;
    }
}
