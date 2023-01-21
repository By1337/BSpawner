package org.by1337.bspawner.Listener;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.by1337.bspawner.GuiItil.InventoryGenerate;
import org.by1337.bspawner.util.*;

import java.util.Objects;

import static org.by1337.bspawner.BSpawner.SpawnersDb;


public class SpawnerListener implements Listener {

    @EventHandler
    public void onPlaceEvent(BlockPlaceEvent e){
        if(e.getBlock().getType().equals(Material.SPAWNER)){
            BlockStateMeta bsm = (BlockStateMeta) e.getItemInHand().getItemMeta();
            assert bsm != null;
            CreatureSpawner cs = (CreatureSpawner) bsm.getBlockState();

            String str = String.valueOf(cs.getSpawnedType().getName());

            BlockState blockState = e.getBlock().getState();
            if ((blockState instanceof CreatureSpawner)) {
                EntityType ct = EntityType.fromName(str);
                if (ct != null) {
                    ((CreatureSpawner)blockState).setSpawnedType(ct);
                    blockState.update(true);
                }
            }
        }
    }
    @EventHandler
    public void BreakSpawner(BlockBreakEvent e){
        if(e.isCancelled() || !e.getBlock().getType().equals(Material.SPAWNER))
            return;
        SpawnersDb.remove(e.getBlock().getLocation());
    }

    @EventHandler
    public void PlayerClick(PlayerInteractEvent e){
        Player pl = e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if(Objects.requireNonNull(e.getClickedBlock()).getType() == Material.SPAWNER && e.getHand() == EquipmentSlot.OFF_HAND){
                if(!WorldGuardUtil.isEmptyRegion(e.getClickedBlock().getLocation(), pl.getUniqueId())){
                    Message.sendMsg(pl, Config.getMessage("guard-spawner"));
                    return;
                }
                if(SpawnersDb.containsKey(e.getClickedBlock().getLocation())){
                    SpawnersDb.get(e.getClickedBlock().getLocation()).setSpawner(e.getClickedBlock());
                    InventoryGenerate.MenuGenerate(SpawnersDb.get(e.getClickedBlock().getLocation()));
                    pl.openInventory(SpawnersDb.get(e.getClickedBlock().getLocation()).getInv());
                }else {
                    SpawnersDb.put(e.getClickedBlock().getLocation(), new SpawnerTask(e.getClickedBlock()));
                    InventoryGenerate.MenuGenerate(SpawnersDb.get(e.getClickedBlock().getLocation()));
                    pl.openInventory(SpawnersDb.get(e.getClickedBlock().getLocation()).getInv());
                }
            }
        }

    }

    @EventHandler
    public void explode(EntityExplodeEvent e){
        e.blockList().removeIf(b -> b.getType().equals(Material.SPAWNER));
    }

    @EventHandler
    public void onChange(EntityChangeBlockEvent e) {
        if (e.getBlock().getType().equals(Material.SPAWNER))
            e.setCancelled(true);
    }

}
