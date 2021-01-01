package com.willfp.illusioner.illusioner.options;

import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.config.Configs;
import com.willfp.eco.util.tuplets.Pair;
import com.willfp.illusioner.config.IllusionerConfigs;
import com.willfp.illusioner.illusioner.BlockStructure;
import com.willfp.illusioner.illusioner.OptionedSound;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class IllusionerOptions {
    /**
     * The boss bar color.
     */
    @Getter
    private BarColor color;

    /**
     * The boss bar style.
     */
    @Getter
    private BarStyle style;

    /**
     * The name of the illusioner.
     */
    @Getter
    private String name;

    /**
     * The spawn sounds.
     */
    @Getter
    private Set<OptionedSound> spawnSounds;

    /**
     * The death sounds.
     */
    @Getter
    private Set<OptionedSound> deathSounds;

    /**
     * The xp bounds.
     */
    private Pair<Integer, Integer> xpBounds;

    /**
     * The spawn block structure.
     */
    @Getter
    private BlockStructure spawnStructure;

    /**
     * The max health.
     */
    @Getter
    private double maxHealth;

    /**
     * The attack damage.
     */
    @Getter
    private double attackDamage;

    /**
     * The drops.
     */
    @Getter
    private Set<ItemStack> drops;

    /**
     * The gameplay options.
     */
    @Getter
    private final GameplayOptions gameplayOptions = new GameplayOptions();

    /**
     * If plugin-based illusioners should override vanilla illusioners.
     */
    @Getter
    private boolean override;

    /**
     * Create new illusioner options.
     */
    public IllusionerOptions() {
        reload();
    }

    /**
     * Reload options from config.
     */
    public void reload() {
        color = BarColor.valueOf(Configs.CONFIG.getString("bossbar.color"));
        style = BarStyle.valueOf(Configs.CONFIG.getString("bossbar.style"));
        name = Configs.CONFIG.getString("name");
        xpBounds = new Pair<>(Configs.CONFIG.getInt("xp.minimum"), Configs.CONFIG.getInt("xp.maximum"));
        maxHealth = Configs.CONFIG.getDouble("max-health");
        attackDamage = Configs.CONFIG.getDouble("attack-damage");
        override = Configs.CONFIG.getBool("override");

        spawnSounds = new HashSet<>();
        IllusionerConfigs.SOUNDS.getConfig().getConfigurationSection("spawn").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(Configs.CONFIG.getString("spawn." + key + ".sound"));
            boolean broadcast = Configs.CONFIG.getBool("spawn." + key + ".broadcast");
            float volume = (float) Configs.CONFIG.getDouble("spawn." + key + ".volume");
            float pitch = (float) Configs.CONFIG.getDouble("spawn." + key + ".pitch");
            spawnSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        deathSounds = new HashSet<>();
        IllusionerConfigs.SOUNDS.getConfig().getConfigurationSection("death").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(Configs.CONFIG.getString("death." + key + ".sound"));
            boolean broadcast = Configs.CONFIG.getBool("death." + key + ".broadcast");
            float volume = (float) Configs.CONFIG.getDouble("death." + key + ".volume");
            float pitch = (float) Configs.CONFIG.getDouble("death." + key + ".pitch");
            deathSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        spawnStructure = new BlockStructure(
                Material.valueOf(Configs.CONFIG.getString("spawn.bottom-block")),
                Material.valueOf(Configs.CONFIG.getString("spawn.middle-block")),
                Material.valueOf(Configs.CONFIG.getString("spawn.top-block"))
        );

        gameplayOptions.reload();

        drops = new HashSet<>();
        IllusionerConfigs.DROPS.getConfig().getKeys(false).forEach(key -> {
            ItemStack itemStack = Configs.CONFIG.getConfig().getItemStack(key);
            drops.add(itemStack);
        });
    }

    /**
     * Generate xp to drop.
     *
     * @return The amount of xp to drop.
     */
    public int generateXp() {
        return NumberUtils.randInt(xpBounds.getFirst(), xpBounds.getSecond());
    }
}
