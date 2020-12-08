package com.willfp.illusioner.illusioner.options;

import com.willfp.illusioner.config.ConfigManager;
import com.willfp.illusioner.util.internal.OptionedSound;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;

public class GameplayOptions {
    private OptionedSound hitSound;
    private OptionedSound summonSound;

    private final Set<EffectOption> effectOptions = new HashSet<>();
    private final Set<SummonerOption> summonerOptions = new HashSet<>();

    private boolean shuffle;
    private double shuffleChance;

    private boolean ignoreExplosionDamage;

    public void reload() {
        hitSound = new OptionedSound(
                Sound.valueOf(ConfigManager.getConfig().getString("sounds.hit.sound")),
                (float) ConfigManager.getConfig().getDouble("sounds.hit.volume"),
                (float) ConfigManager.getConfig().getDouble("sounds.hit.pitch"),
                ConfigManager.getConfig().getBool("sounds.hit.broadcast")
        );

        summonSound = new OptionedSound(
                Sound.valueOf(ConfigManager.getConfig().getString("sounds.summon.sound")),
                (float) ConfigManager.getConfig().getDouble("sounds.summon.volume"),
                (float) ConfigManager.getConfig().getDouble("sounds.summon.pitch"),
                ConfigManager.getConfig().getBool("sounds.summon.broadcast")
        );

        shuffle = ConfigManager.getConfig().getBool("attacks.shuffle.enabled");
        shuffleChance = ConfigManager.getConfig().getDouble("attacks.shuffle.chance");
        ignoreExplosionDamage = ConfigManager.getConfig().getBool("ignore-explosion-damage");

        effectOptions.clear();
        ConfigManager.getConfig().config.getConfigurationSection("attacks.effects").getKeys(false).forEach(key -> {
            PotionEffectType type = PotionEffectType.getByName(ConfigManager.getConfig().getString("attacks.effects." + key + ".type"));
            int level = ConfigManager.getConfig().getInt("attacks.effects." + key + ".level");
            int duration = ConfigManager.getConfig().getInt("attacks.effects." + key + ".duration");
            double chance = ConfigManager.getConfig().getDouble("attacks.effects." + key + ".chance");
            effectOptions.add(new EffectOption(chance, level, duration, type));
        });

        summonerOptions.clear();
        ConfigManager.getConfig().config.getConfigurationSection("attacks.summons").getKeys(false).forEach(key -> {
            EntityType type = EntityType.valueOf(ConfigManager.getConfig().getString("attacks.summons." + key + ".type"));
            double chance = ConfigManager.getConfig().getDouble("attacks.summons." + key + ".chance");
            summonerOptions.add(new SummonerOption(chance, type));
        });
    }

    public OptionedSound getHitSound() {
        return hitSound;
    }

    public OptionedSound getSummonSound() {
        return summonSound;
    }

    public Set<EffectOption> getEffectOptions() {
        return effectOptions;
    }

    public Set<SummonerOption> getSummonerOptions() {
        return summonerOptions;
    }

    public double getShuffleChance() {
        return shuffleChance;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isIgnoreExplosionDamage() {
        return ignoreExplosionDamage;
    }

    public static class EffectOption {
        private final double chance;
        private final int level;
        private final PotionEffectType effectType;
        private final int duration;

        public EffectOption(double chance, int level, int duration, PotionEffectType effectType) {
            this.chance = chance;
            this.level = level;
            this.effectType = effectType;
            this.duration = duration;
        }

        public double getChance() {
            return chance;
        }

        public int getLevel() {
            return level;
        }

        public PotionEffectType getEffectType() {
            return effectType;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class SummonerOption {
        private final double chance;
        private final EntityType type;

        public SummonerOption(double chance, EntityType type) {
            this.chance = chance;
            this.type = type;
        }

        public double getChance() {
            return chance;
        }

        public EntityType getType() {
            return type;
        }
    }
}
