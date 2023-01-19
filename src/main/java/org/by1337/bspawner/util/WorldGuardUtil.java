package org.by1337.bspawner.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class WorldGuardUtil {
    public static boolean isEmptyRegion(Location loc, UUID uuid){

        com.sk89q.worldedit.util.Location WGlocation = BukkitAdapter.adapt(loc);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(WGlocation);


        if (set.getRegions().isEmpty())
            return true;
        else{
            List<ProtectedRegion> regions = new ArrayList<>(set.getRegions());
            regions.removeIf(rrg -> rrg.getOwners().contains(uuid));
            regions.removeIf(rrg -> rrg.getMembers().contains(uuid));
            return regions.size() == 0;
        }
    }
}
