package com.willfp.ecobosses.bosses.listeners;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.ecobosses.bosses.EcoBoss;
import com.willfp.ecobosses.bosses.util.BossUtils;
import org.bukkit.entity.Boat;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Minecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

public class PassiveListeners extends PluginDependent<EcoPlugin> implements Listener {
    /**
     * Create new attack listeners.
     *
     * @param plugin Instance of EcoBosses.
     */
    public PassiveListeners(@NotNull final EcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Called when a player attacks a boss.
     *
     * @param event The event to listen for.
     */
    @EventHandler(ignoreCancelled = true)
    public void onAttackBoss(@NotNull final EntityMountEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) {
            return;
        }

        if (!(event.getMount() instanceof Boat || event.getMount() instanceof Minecart)) {
            return;
        }

        EcoBoss boss = BossUtils.getBoss(entity);

        if (boss == null) {
            return;
        }

        if (boss.isDisableBoats()) {
            event.setCancelled(true);
        }
    }
}
