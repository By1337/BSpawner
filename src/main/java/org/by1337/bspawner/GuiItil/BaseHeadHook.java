package org.by1337.bspawner.GuiItil;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.by1337.bspawner.util.Message;
import org.by1337.bspawner.util.SkullUtils;

public class BaseHeadHook {

    public static ItemStack getItem(String... arguments) {
        if(arguments == null){
            Message.error("material is null!");
            return new ItemStack(Material.PLAYER_HEAD);
        }

        try {
            return SkullUtils.getSkull(arguments[0].replace("basehead-", ""));
        } catch (Exception exception) {
            Message.error("Something went wrong while trying to get base64 head: " + arguments[0]);
            return new ItemStack(Material.PLAYER_HEAD);
        }
    }
}
