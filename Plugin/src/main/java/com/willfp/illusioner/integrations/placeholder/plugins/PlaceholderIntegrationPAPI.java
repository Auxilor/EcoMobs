package com.willfp.illusioner.integrations.placeholder.plugins;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.integrations.placeholder.PlaceholderIntegration;
import com.willfp.illusioner.integrations.placeholder.PlaceholderManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI integration
 */
public class PlaceholderIntegrationPAPI extends PlaceholderExpansion implements PlaceholderIntegration {
    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return "Auxilor";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "illusioner";
    }

    @Override
    public @NotNull String getVersion() {
        return IllusionerPlugin.getInstance().getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        return PlaceholderManager.getResult(player, identifier);
    }

    @Override
    public void registerIntegration() {
        this.register();
    }

    @Override
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public String translate(String text, Player player) {
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
