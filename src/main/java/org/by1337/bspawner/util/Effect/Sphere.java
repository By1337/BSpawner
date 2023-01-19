package org.by1337.bspawner.util.Effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.by1337.bspawner.util.Message;


public class Sphere {
    public static void totem(Location loc, double radius, int viewDistance) {
        for (double h = 0; h <= radius; h += 0.5) {

            for (double pz = 0; pz <= 10; pz += 0.2) {
                double x = radius * Math.cos(pz);
                double z = radius * Math.sin(pz);
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, viewDistance, viewDistance, viewDistance)) {
                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        p.spawnParticle(Particle.TOTEM, new Location(loc.getWorld(), loc.getX() + x , loc.getY() + h , loc.getZ() + z), 0);
                        p.spawnParticle(Particle.TOTEM, new Location(loc.getWorld(), loc.getX() + x , loc.getY() - h , loc.getZ() + z), 0);
                    }
                }
            }
        }

    }
}
