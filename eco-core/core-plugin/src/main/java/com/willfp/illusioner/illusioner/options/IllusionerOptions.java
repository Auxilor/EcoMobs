package com.willfp.illusioner.illusioner.options;

import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.tuplets.Pair;
import com.willfp.illusioner.config.IllusionerConfigs;
import com.willfp.illusioner.illusioner.BlockStructure;
import com.willfp.illusioner.illusioner.OptionedSound;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@ToString
public class IllusionerOptions extends PluginDependent {
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
    private final GameplayOptions gameplayOptions = new GameplayOptions(this.getPlugin());

    /**
     * If plugin-based illusioners should override vanilla illusioners.
     */
    @Getter
    private boolean override;

    /**
     * Create new illusioner options.
     *
     * @param plugin The plugin.
     */
    public IllusionerOptions(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
        reload();
    }

    /**
     * Reload options from config.
     */
    public void reload() {
        color = BarColor.valueOf(this.getPlugin().getConfigYml().getString("bossbar.color"));
        style = BarStyle.valueOf(this.getPlugin().getConfigYml().getString("bossbar.style"));
        name = this.getPlugin().getConfigYml().getString("name");
        xpBounds = new Pair<>(this.getPlugin().getConfigYml().getInt("xp.minimum"), this.getPlugin().getConfigYml().getInt("xp.maximum"));
        maxHealth = this.getPlugin().getConfigYml().getDouble("max-health");
        attackDamage = this.getPlugin().getConfigYml().getDouble("attack-damage");
        override = this.getPlugin().getConfigYml().getBool("override");

        spawnSounds = new HashSet<>();
        IllusionerConfigs.SOUNDS.getConfig().getConfigurationSection("spawn").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(IllusionerConfigs.SOUNDS.getString("spawn." + key + ".sound"));
            boolean broadcast = IllusionerConfigs.SOUNDS.getBool("spawn." + key + ".broadcast");
            float volume = (float) IllusionerConfigs.SOUNDS.getDouble("spawn." + key + ".volume");
            float pitch = (float) IllusionerConfigs.SOUNDS.getDouble("spawn." + key + ".pitch");
            spawnSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        deathSounds = new HashSet<>();
        IllusionerConfigs.SOUNDS.getConfig().getConfigurationSection("death").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(IllusionerConfigs.SOUNDS.getString("death." + key + ".sound"));
            boolean broadcast = IllusionerConfigs.SOUNDS.getBool("death." + key + ".broadcast");
            float volume = (float) IllusionerConfigs.SOUNDS.getDouble("death." + key + ".volume");
            float pitch = (float) IllusionerConfigs.SOUNDS.getDouble("death." + key + ".pitch");
            deathSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        spawnStructure = new BlockStructure(
                Material.valueOf(this.getPlugin().getConfigYml().getString("spawn.bottom-block")),
                Material.valueOf(this.getPlugin().getConfigYml().getString("spawn.middle-block")),
                Material.valueOf(this.getPlugin().getConfigYml().getString("spawn.top-block"))
        );

        gameplayOptions.reload();

        drops = new HashSet<>();
        IllusionerConfigs.DROPS.getConfig().getKeys(false).forEach(key -> {
            ItemStack itemStack = IllusionerConfigs.DROPS.getConfig().getItemStack(key);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                return;
            }
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
