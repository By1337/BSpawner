package org.by1337.bspawner.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {
    public static int playerHasItem(Player pl, Material mat){
        int amount = 0;
        for(ItemStack item : pl.getInventory()){
            if(item == null) continue;
            if(item.getType().equals(mat)){
                amount += item.getAmount();
            }
        }
        return amount;
    }

    public static void TakeItem(Player pl, Material mat, int amount){
        for(ItemStack item : pl.getInventory()){
            if(item == null) continue;
            if(item.getType().equals(mat)){
                if(amount <= 0){
                    break;
                }
                if(item.getAmount() > amount){
                    item.setAmount(item.getAmount() - amount);
                    amount = 0;
                }else {
                    amount -= item.getAmount();

                    pl.getInventory().setItem(pl.getInventory().first(item), null);
                }
            }
        }
    }


}
