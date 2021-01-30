package com.willfp.illusioner.illusioner.options;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.illusioner.config.IllusionerConfigs;
import com.willfp.illusioner.illusioner.OptionedSound;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

@ToString
public class GameplayOptions extends PluginDependent {
    /**
     * The sound played when the illusioner takes damage.
     */
    @Getter
    private OptionedSound hitSound;

    /**
     * The sound played when the illusioner spawns.
     */
    @Getter
    private OptionedSound summonSound;

    /**
     * The potion effect options.
     */
    @Getter
    private final Set<EffectOption> effectOptions = new HashSet<>();

    /**
     * The mob summon options.
     */
    @Getter
    private final Set<SummonerOption> summonerOptions = new HashSet<>();

    /**
     * If the illusioner should shuffle hotbars.
     */
    @Getter
    private boolean shuffle;

    /**
     * The chance of the illusioner shuffling a hotbar.
     */
    @Getter
    private double shuffleChance;

    /**
     * If the illusioner is immune to explosion damage.
     */
    @Getter
    private boolean ignoreExplosionDamage;

    /**
     * If the illusioner is immune to fire damage.
     */
    @Getter
    private boolean ignoreFire;

    /**
     * If the illusioner is immune to suffocation damage.
     */
    @Getter
    private boolean ignoreSuffocation;

    /**
     * If the illusioner can teleport.
     */
    @Getter
    private boolean teleport;

    /**
     * Teleport range.
     */
    @Getter
    private int teleportRange;

    /**
     * Teleport chance.
     */
    @Getter
    private double teleportChance;

    /**
     * Teleport sound.
     */
    @Getter
    private OptionedSound teleportSound;

    /**
     * Gameplay options.
     *
     * @param plugin The plugin.
     */
    public GameplayOptions(@NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
    }

    /**
     * Reload the options.
     */
    public void reload() {
        hitSound = new OptionedSound(
                Sound.valueOf(IllusionerConfigs.SOUNDS.getString("hit.sound")),
                (float) IllusionerConfigs.SOUNDS.getDouble("hit.volume"),
                (float) IllusionerConfigs.SOUNDS.getDouble("hit.pitch"),
                IllusionerConfigs.SOUNDS.getBool("hit.broadcast")
        );

        summonSound = new OptionedSound(
                Sound.valueOf(IllusionerConfigs.SOUNDS.getString("summon.sound")),
                (float) IllusionerConfigs.SOUNDS.getDouble("summon.volume"),
                (float) IllusionerConfigs.SOUNDS.getDouble("summon.pitch"),
                IllusionerConfigs.SOUNDS.getBool("summon.broadcast")
        );

        shuffle = IllusionerConfigs.ATTACKS.getBool("shuffle.enabled");
        shuffleChance = IllusionerConfigs.ATTACKS.getDouble("shuffle.chance");

        ignoreExplosionDamage = this.getPlugin().getConfigYml().getBool("ignore-explosion-damage");
        ignoreFire = this.getPlugin().getConfigYml().getBool("ignore-fire-damage");
        ignoreSuffocation = this.getPlugin().getConfigYml().getBool("ignore-suffocation-damage");

        teleport = this.getPlugin().getConfigYml().getBool("teleport-on-damage.enabled");
        teleportRange = this.getPlugin().getConfigYml().getInt("teleport-on-damage.range");
        teleportChance = this.getPlugin().getConfigYml().getDouble("teleport-on-damage.chance");

        Sound sound = Sound.valueOf(IllusionerConfigs.SOUNDS.getString("teleport.sound"));
        float volume = (float) IllusionerConfigs.SOUNDS.getDouble("teleport.volume");
        float pitch = (float) IllusionerConfigs.SOUNDS.getDouble("teleport.pitch");
        teleportSound = new OptionedSound(sound, volume, pitch, true);

        effectOptions.clear();
        IllusionerConfigs.ATTACKS.getConfig().getConfigurationSection("effects").getKeys(false).forEach(key -> {
            PotionEffectType type = PotionEffectType.getByName(IllusionerConfigs.ATTACKS.getString("effects." + key + ".type"));
            int level = IllusionerConfigs.ATTACKS.getInt("effects." + key + ".level");
            int duration = IllusionerConfigs.ATTACKS.getInt("effects." + key + ".duration");
            double chance = IllusionerConfigs.ATTACKS.getDouble("effects." + key + ".chance");
            effectOptions.add(new EffectOption(chance, level, duration, type));
        });

        summonerOptions.clear();
        IllusionerConfigs.ATTACKS.getConfig().getConfigurationSection("summons").getKeys(false).forEach(key -> {
            EntityType type = EntityType.valueOf(IllusionerConfigs.ATTACKS.getString("summons." + key + ".type"));
            double chance = IllusionerConfigs.ATTACKS.getDouble("summons." + key + ".chance");
            summonerOptions.add(new SummonerOption(chance, type));
        });
    }

    public static class EffectOption {
        /**
         * The chance of the effect being applied.
         */
        @Getter
        private final double chance;

        /**
         * The level of the effect.
         */
        @Getter
        private final int level;

        /**
         * The potion effect type.
         */
        @Getter
        private final PotionEffectType effectType;

        /**
         * The duration, in ticks.
         */
        @Getter
        private final int duration;

        /**
         * Create a new effect option.
         *
         * @param chance     The chance.
         * @param level      The level.
         * @param duration   The duration in ticks.
         * @param effectType The effect.
         */
        public EffectOption(final double chance,
                            final int level,
                            final int duration,
                            @NotNull final PotionEffectType effectType) {
            this.chance = chance;
            this.level = level;
            this.effectType = effectType;
            this.duration = duration;
        }
    }

    public static class SummonerOption {
        /**
         * The chance of a mob being spawned.
         */
        @Getter
        private final double chance;

        /**
         * The type of entity to summon.
         */
        @Getter
        private final EntityType type;

        /**
         * Create a new summoner option.
         *
         * @param chance The chance.
         * @param type   The entity type.
         */
        public SummonerOption(final double chance,
                              @NotNull final EntityType type) {
            this.chance = chance;
            this.type = type;
        }
    }
}
