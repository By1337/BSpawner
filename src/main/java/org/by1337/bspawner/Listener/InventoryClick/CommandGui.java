package org.by1337.bspawner.Listener.InventoryClick;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.by1337.bspawner.util.Config;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SpawnerTask;

import static org.by1337.bspawner.BSpawner.instance;

public class CommandGui {
    public static void command(SpawnerTask spawnerTask, Player pl, String key) {

    }

    public static void run(SpawnerTask spawnerTask, Player pl, String path) {//path == пути до списка команд commands или deny_commands
        for (String str : instance.getConfig().getStringList(path)) {
            if (str.contains("[MESSAGE]"))
                Message.sendMsg(pl, str.replace("[MESSAGE] ", ""));
            if (str.contains("[CLOSE]"))
                pl.closeInventory();
            if (str.contains("[DROP_SPAWNER]"))
                spawnerTask.MineSpawner();
            if (str.contains("[SKIP]"))
                spawnerTask.skipTask();
            if (str.contains("[CHANGE]"))
                spawnerTask.ChangeTask();
            if(str.contains("[CONSOLE]")){
                String command = str.replace("[CONSOLE] ", "");
                command = Message.setPlaceholders(pl, command);
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
            }
        }
    }

    public static boolean clickRequirement(SpawnerTask spawnerTask, Player pl, String key) {//key == имени предмета gui.имя
        if (instance.getConfig().getConfigurationSection("gui." + key + ".click_requirement") == null)
            return true;
        for (String checkName : instance.getConfig().getConfigurationSection("gui." + key + ".click_requirement").getKeys(false)) {//checkName == имени проверки gui.имя.click_requirement.имя_проверки
            if (instance.getConfig().getString("gui." + key + ".click_requirement." + checkName + ".type").equalsIgnoreCase("internal"))
                if (!internalCheck(spawnerTask, "gui." + key + ".click_requirement." + checkName)) {
                    run(spawnerTask, pl, "gui." + key + ".click_requirement." + checkName + ".deny_commands");
                    return false;
                }
            if (instance.getConfig().getString("gui." + key + ".click_requirement." + checkName + ".type").equalsIgnoreCase("javascript")) {
                if(!javascriptCheck(spawnerTask, pl, "gui." + key + ".click_requirement." + checkName)){
                    run(spawnerTask, pl, "gui." + key + ".click_requirement." + checkName + ".deny_commands");
                    return false;
                }
            }

        }
        return true;
    }

    private static boolean javascriptCheck(SpawnerTask spawnerTask, Player pl, String patch) {
        String input = instance.getConfig().getString(patch + ".input");
        assert input != null;
        String[] args = input.split(" ");
        args[0] = Message.setPlaceholders(pl, args[0]);
        args[1] = Message.setPlaceholders(pl, args[1]);
        int x = 0;
        int x1 = 0;
        try {
            x = Integer.parseInt(args[0]);
            x1 = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            Message.error(String.format(Config.getMessage("number-format-exception"), e.getLocalizedMessage(), patch));
            return false;
        }
        if (args[1].equals(">"))
            return x > x1;
        if (args[1].equals(">="))
            return x >= x1;
        if (args[1].equals("=="))
            return x == x1;
        if (args[1].equals("<="))
            return x <= x1;
        if (args[1].equals("<"))
            return x < x1;
        Message.error(String.format(Config.getMessage("unknown-operator"), args[1]));
        return false;
    }

    private static boolean internalCheck(SpawnerTask spawnerTask, String patch) {
        String input = instance.getConfig().getString(patch + ".input");
        assert input != null;
        if (input.equalsIgnoreCase("AllTaskCompleted")) {
            return spawnerTask.CheckAllTaskCompleted() == instance.getConfig().getBoolean(patch + ".output");
        }
        return false;
    }
}
