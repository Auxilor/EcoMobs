package com.willfp.illusioner.util.internal.updater;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        /*
        if (UpdateChecker.isOutdated()) {
            if (event.getPlayer().hasPermission("illusioner.updateannounce")) {
                event.getPlayer().sendMessage(ConfigManager.getLang().getMessage("outdated").replace("%ver%", IllusionerPlugin.getInstance().getDescription().getVersion())
                        .replace("%newver%", UpdateChecker.getNewVersion()));
            }
        }
         */
    }
}
