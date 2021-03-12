package com.willfp.ecobosses.bosses.options;

import com.willfp.eco.util.NumberUtils;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.tuples.Pair;
import com.willfp.ecobosses.config.EcoBossesConfigs;
import com.willfp.ecobosses.bosses.util.obj.SpawnTotem;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private SpawnTotem spawnStructure;

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
    private List<ItemStack> drops;

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
        EcoBossesConfigs.SOUNDS.getConfig().getConfigurationSection("spawn").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(EcoBossesConfigs.SOUNDS.getString("spawn." + key + ".sound"));
            boolean broadcast = EcoBossesConfigs.SOUNDS.getBool("spawn." + key + ".broadcast");
            float volume = (float) EcoBossesConfigs.SOUNDS.getDouble("spawn." + key + ".volume");
            float pitch = (float) EcoBossesConfigs.SOUNDS.getDouble("spawn." + key + ".pitch");
            spawnSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        deathSounds = new HashSet<>();
        EcoBossesConfigs.SOUNDS.getConfig().getConfigurationSection("death").getKeys(false).forEach(key -> {
            Sound sound = Sound.valueOf(EcoBossesConfigs.SOUNDS.getString("death." + key + ".sound"));
            boolean broadcast = EcoBossesConfigs.SOUNDS.getBool("death." + key + ".broadcast");
            float volume = (float) EcoBossesConfigs.SOUNDS.getDouble("death." + key + ".volume");
            float pitch = (float) EcoBossesConfigs.SOUNDS.getDouble("death." + key + ".pitch");
            deathSounds.add(new OptionedSound(sound, volume, pitch, broadcast));
        });

        spawnStructure = new SpawnTotem(
                Material.valueOf(this.getPlugin().getConfigYml().getString("spawn.bottom-block")),
                Material.valueOf(this.getPlugin().getConfigYml().getString("spawn.middle-block")),
                Material.valueOf(this.getPlugin().getConfigYml().getString("spawn.top-block"))
        );

        gameplayOptions.reload();

        drops = new ArrayList<>();
        for (String key : EcoBossesConfigs.DROPS.getConfig().getKeys(false)) {
            ItemStack itemStack = EcoBossesConfigs.DROPS.getConfig().getItemStack(key);
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }
            drops.add(itemStack);
        }
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
