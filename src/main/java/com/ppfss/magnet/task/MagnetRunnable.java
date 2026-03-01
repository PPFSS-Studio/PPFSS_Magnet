// PPFSS_Magnet Plugin 
// Авторские права (c) 2025 PPFSS
// Лицензия: MIT

package com.ppfss.magnet.task;

import com.ppfss.magnet.config.Config;
import com.ppfss.magnet.model.FilterData;
import com.ppfss.magnet.model.FilterType;
import com.ppfss.magnet.model.MagnetData;
import com.ppfss.magnet.model.ParticleData;
import com.ppfss.magnet.service.MagnetService;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class MagnetRunnable extends BukkitRunnable {
    private final MagnetService magnetService;
    private final Function<Player, Boolean> hasMagnet;
    @Setter
    private ParticleData particleData;
    private final Config config;

    public MagnetRunnable(MagnetService magnetService, Function<Player, Boolean> hasMagnet) {
        this.magnetService = magnetService;
        this.hasMagnet = hasMagnet;

        this.particleData = Config.getInstance().getParticleData();
        this.config = Config.getInstance();
    }

    @Override
    public void run() {
        Map<UUID, MagnetData> active = magnetService.getActiveMagnets();

        active.forEach((uuid, data) -> {
            Player player = Bukkit.getPlayer(uuid);

            if (player == null) {
                magnetService.removeActiveMagnet(uuid);
                return;
            }

            if (!hasMagnet.apply(player)) {
                magnetService.removeActiveMagnet(uuid);
                return;
            }

            moveAllNearbyItems(player, data);
        });
    }

    private void moveAllNearbyItems(@NotNull Player player, @NotNull MagnetData data) {
        Location playerLocation = player.getLocation();
        Location destination = playerLocation.clone().add(0, 1, 0);
        Vector playerVector = playerLocation.toVector();
        double teleportDistance = 0.6;
        double teleportDistanceSquared = teleportDistance * teleportDistance;
        int radius = data.radius();
        double radiusSquared = (double) radius * radius;
        double strength = data.strength();
        int limit = data.limit();
        if (limit == 0) limit = Integer.MAX_VALUE;
        boolean particlesEnabled = particleData.isEnabled();

        int processed = 0;
        for (var entity : player.getNearbyEntities(radius, radius, radius)) {
            if (!(entity instanceof Item item)) continue;
            if (!filterItems(item)) continue;
            if (processed >= limit) break;
            processed++;

            Location itemLocation = item.getLocation();
            double distanceSquared = itemLocation.distanceSquared(playerLocation);

            if (distanceSquared > radiusSquared) continue;

            if (distanceSquared < teleportDistanceSquared) {
                item.teleport(destination);
                continue;
            }

            Vector direction = playerVector.clone()
                    .subtract(itemLocation.toVector())
                    .normalize();

            item.setVelocity(direction.multiply(strength));

            if (particlesEnabled) {
                player.spawnParticle(
                        particleData.getType(),
                        itemLocation,
                        3,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }
        }
    }


    private boolean filterItems(Item item){
        FilterData filterData = config.getFilterData();
        if (filterData == null || !filterData.isEnabled()) {
            return true;
        }

        Material type = item.getItemStack().getType();
        if (type.isAir()) return false;

        boolean listed = filterData.getBlocklist() != null && filterData.getBlocklist().contains(type);
        if (filterData.getType() == FilterType.WHITELIST) {
            return listed;
        }

        return !listed;
    }


}
