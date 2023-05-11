package org.by1337.bspawner.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CmdCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if(!player.hasPermission("bs.*"))
                return null;
            if (args.length == 1) {
                return List.of(
                        "reload",
                        "complete",
                        "unlock",
                        "drop"
                );
            }
            if (args.length == 2) {
                return List.of(
                        "-force"
                );
            }
        }
        return null;
    }
}
