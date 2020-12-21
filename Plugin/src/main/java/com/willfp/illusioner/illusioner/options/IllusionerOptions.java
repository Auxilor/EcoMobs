package com.willfp.illusioner.illusioner.options;

import com.willfp.illusioner.config.ConfigManager;
import com.willfp.illusioner.illusioner.BlockStructure;
import com.willfp.illusioner.util.NumberUtils;
import com.willfp.illusioner.util.internal.OptionedSound;
import com.willfp.illusioner.util.tuplets.Pair;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class IllusionerOptions {
    private BarColor color;
    private BarStyle style;
    private String name;
    private Set<OptionedSound> spawnSounds;
    private Set<OptionedSound> deathSounds;
    private Pair<Integer, Integer> xpBounds;
    private BlockStructure spawnStructure;
    private double maxHealth;
    private double attackDamage;
    private Set<ItemStack> drops;
    private final GameplayOptions gameplayOptions = new GameplayOptions();
    private boolean override;

    public IllusionerOptions() {
        reload();
    }

    public void reload() {
        color = BarColor.valueOf(ConfigManager.getConfig().getString("bossbar.color"));
        style = BarStyle.valueOf(ConfigManager.getConfig().getString("bossbar.style"));
        name = ConfigManager.getConfig().getString("name");
        xpBounds = new Pair<>(ConfigManager.getConfig().getInt("xp.minimum"), ConfigManager.getConfig().getInt("xp.maximum"));
        maxHealth = ConfigManager.getConfig().getDouble("max-health");
        attackDamage = ConfigManager.getConfig().getDouble("attack-damage");
        override = ConfigManager.getConfig().getBool("override");

        spawnSounds = new HashSet<>();
        ConfigManager.getConfig().config.getConfigurationSection("sounds.spawn").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(ConfigManager.getConfig().getString("sounds.spawn." + key + ".sound"));
            boolean broadcast = ConfigManager.getConfig().getBool("sounds.spawn." + key + ".broadcast");
            float volume = (float) ConfigManager.getConfig().getDouble("sounds.spawn." + key + ".volume");
            float pitch = (float) ConfigManager.getConfig().getDouble("sounds.spawn." + key + ".pitch");
            spawnSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        deathSounds = new HashSet<>();
        ConfigManager.getConfig().config.getConfigurationSection("sounds.death").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(ConfigManager.getConfig().getString("sounds.death." + key + ".sound"));
            boolean broadcast = ConfigManager.getConfig().getBool("sounds.death." + key + ".broadcast");
            float volume = (float) ConfigManager.getConfig().getDouble("sounds.death." + key + ".volume");
            float pitch = (float) ConfigManager.getConfig().getDouble("sounds.death." + key + ".pitch");
            deathSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        spawnStructure = new BlockStructure(
                Material.valueOf(ConfigManager.getConfig().getString("spawn.bottom-block")),
                Material.valueOf(ConfigManager.getConfig().getString("spawn.middle-block")),
                Material.valueOf(ConfigManager.getConfig().getString("spawn.top-block"))
        );

        gameplayOptions.reload();

        drops = new HashSet<>();
        ConfigManager.getConfig().config.getConfigurationSection("drops").getKeys(false).forEach(key -> {
            ItemStack itemStack = ConfigManager.getConfig().getItemStack("drops." + key);
            drops.add(itemStack);
        });
    }

    public BarColor getColor() {
        return color;
    }

    public BarStyle getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }

    public Set<OptionedSound> getSpawnSounds() {
        return spawnSounds;
    }

    public Set<OptionedSound> getDeathSounds() {
        return deathSounds;
    }

    public int generateXp() {
        return NumberUtils.randInt(xpBounds.getFirst(), xpBounds.getSecond());
    }

    public BlockStructure getSpawnStructure() {
        return spawnStructure;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public Set<ItemStack> getDrops() {
        return drops;
    }

    public GameplayOptions getGameplayOptions() {
        return gameplayOptions;
    }

    public boolean isOverride() {
        return override;
    }
}
