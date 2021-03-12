package com.willfp.ecobosses.bosses.options;

import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecobosses.bosses.util.obj.attacks.EffectOption;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import com.willfp.ecobosses.config.EcoBossesConfigs;
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
                Sound.valueOf(EcoBossesConfigs.SOUNDS.getString("hit.sound")),
                (float) EcoBossesConfigs.SOUNDS.getDouble("hit.volume"),
                (float) EcoBossesConfigs.SOUNDS.getDouble("hit.pitch"),
                EcoBossesConfigs.SOUNDS.getBool("hit.broadcast")
        );

        summonSound = new OptionedSound(
                Sound.valueOf(EcoBossesConfigs.SOUNDS.getString("summon.sound")),
                (float) EcoBossesConfigs.SOUNDS.getDouble("summon.volume"),
                (float) EcoBossesConfigs.SOUNDS.getDouble("summon.pitch"),
                EcoBossesConfigs.SOUNDS.getBool("summon.broadcast")
        );

        shuffle = EcoBossesConfigs.ATTACKS.getBool("shuffle.enabled");
        shuffleChance = EcoBossesConfigs.ATTACKS.getDouble("shuffle.chance");

        ignoreExplosionDamage = this.getPlugin().getConfigYml().getBool("ignore-explosion-damage");
        ignoreFire = this.getPlugin().getConfigYml().getBool("ignore-fire-damage");
        ignoreSuffocation = this.getPlugin().getConfigYml().getBool("ignore-suffocation-damage");

        teleport = this.getPlugin().getConfigYml().getBool("teleport-on-damage.enabled");
        teleportRange = this.getPlugin().getConfigYml().getInt("teleport-on-damage.range");
        teleportChance = this.getPlugin().getConfigYml().getDouble("teleport-on-damage.chance");

        Sound sound = Sound.valueOf(EcoBossesConfigs.SOUNDS.getString("teleport.sound"));
        float volume = (float) EcoBossesConfigs.SOUNDS.getDouble("teleport.volume");
        float pitch = (float) EcoBossesConfigs.SOUNDS.getDouble("teleport.pitch");
        teleportSound = new OptionedSound(sound, volume, pitch, true);

        effectOptions.clear();
        EcoBossesConfigs.ATTACKS.getConfig().getConfigurationSection("effects").getKeys(false).forEach(key -> {
            PotionEffectType type = PotionEffectType.getByName(EcoBossesConfigs.ATTACKS.getString("effects." + key + ".type"));
            int level = EcoBossesConfigs.ATTACKS.getInt("effects." + key + ".level");
            int duration = EcoBossesConfigs.ATTACKS.getInt("effects." + key + ".duration");
            double chance = EcoBossesConfigs.ATTACKS.getDouble("effects." + key + ".chance");
            effectOptions.add(new EffectOption(chance, level, duration, type));
        });

        summonerOptions.clear();
        EcoBossesConfigs.ATTACKS.getConfig().getConfigurationSection("summons").getKeys(false).forEach(key -> {
            EntityType type = EntityType.valueOf(EcoBossesConfigs.ATTACKS.getString("summons." + key + ".type"));
            double chance = EcoBossesConfigs.ATTACKS.getDouble("summons." + key + ".chance");
            summonerOptions.add(new SummonerOption(chance, type));
        });
    }
}
