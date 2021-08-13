package com.willfp.ecobosses.bosses;

import com.google.common.collect.ImmutableMap;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.PluginDependent;
import com.willfp.eco.core.config.interfaces.Config;
import com.willfp.eco.core.tuples.Pair;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecobosses.bosses.effects.Effect;
import com.willfp.ecobosses.bosses.effects.Effects;
import com.willfp.ecobosses.bosses.util.bosstype.BossEntityUtils;
import com.willfp.ecobosses.bosses.util.bosstype.BossType;
import com.willfp.ecobosses.bosses.util.obj.BossbarProperties;
import com.willfp.ecobosses.bosses.util.obj.ExperienceOptions;
import com.willfp.ecobosses.bosses.util.obj.ImmunityOptions;
import com.willfp.ecobosses.bosses.util.obj.OptionedSound;
import com.willfp.ecobosses.bosses.util.obj.SpawnTotem;
import com.willfp.ecobosses.bosses.util.obj.TargetMode;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class EcoBoss extends PluginDependent<EcoPlugin> {
    /**
     * The name of the boss.
     */
    @Getter
    private final String name;

    /**
     * The config of the set.
     */
    @Getter(AccessLevel.PRIVATE)
    private final Config config;

    /**
     * The display name of the boss.
     */
    @Getter
    private final String displayName;

    /**
     * The base entity spawner.
     */
    private final BossType bossType;

    /**
     * If the boss bar is enabled.
     */
    @Getter
    private final boolean bossbarEnabled;

    /**
     * The BossBar properties.
     */
    @Getter
    private final BossbarProperties bossbarProperties;

    /**
     * If spawn totem is enabled.
     */
    @Getter
    private final boolean spawnTotemEnabled;

    /**
     * The spawn totem.
     */
    @Getter
    private final SpawnTotem spawnTotem;

    /**
     * Disabled world names for spawn totem.
     */
    @Getter
    private final List<String> spawnTotemDisabledWorldNames;

    /**
     * The max health.
     */
    @Getter
    private final int maxHealth;

    /**
     * The attack damage.
     */
    @Getter
    private final int attackDamage;


    /**
     * The follow range.
     */
    @Getter
    private final double followRange;

    /**
     * The movement speed multiplier.
     */
    @Getter
    private final double movementSpeedMultiplier;

    /**
     * The immunity options.
     */
    @Getter
    private final ImmunityOptions immunityOptions;

    /**
     * The drops.
     */
    @Getter
    private final Map<ItemStack, Double> drops;

    /**
     * The exp to drop.
     */
    @Getter
    private final ExperienceOptions experienceOptions;

    /**
     * If attacks should be called on injury.
     */
    @Getter
    private final boolean attackOnInjure;

    /**
     * Sounds played on injure.
     */
    @Getter
    private final List<OptionedSound> injureSounds;

    /**
     * Spawn sounds.
     */
    @Getter
    private final List<OptionedSound> spawnSounds;

    /**
     * Death sounds.
     */
    @Getter
    private final List<OptionedSound> deathSounds;

    /**
     * Summon sounds.
     */
    @Getter
    private final List<OptionedSound> summonSounds;

    /**
     * Spawn messages.
     */
    @Getter
    private final List<String> spawnMessages;

    /**
     * Death messages.
     */
    @Getter
    private final List<String> deathMessages;

    /**
     * Nearby players radius.
     */
    @Getter
    private final double nearbyRadius;

    /**
     * Nearby players commands.
     */
    @Getter
    private final Map<String, Double> nearbyPlayersCommands;

    /**
     * Top damager commands.
     */
    @Getter
    private final Map<Integer, List<Pair<Double, String>>> topDamagerCommands;

    /**
     * Incoming damage multipliers.
     */
    @Getter
    private final Map<EntityDamageEvent.DamageCause, Double> incomingMultipliers;

    /**
     * The currently living bosses of this type.
     */
    private final Map<LivingEntity, LivingEcoBoss> livingBosses;

    /**
     * The effect names and arguments.
     */
    private final Map<String, List<String>> effectNames;

    /**
     * The target distance.
     */
    @Getter
    private final double targetDistance;

    /**
     * The targeting mode.
     */
    @Getter
    private final TargetMode targetMode;

    /**
     * If the boss shouldn't get into boats and minecarts.
     */
    @Getter
    private final boolean disableBoats;

    /**
     * The time between auto spawns.
     */
    @Getter
    private final int autoSpawnInterval;

    /**
     * The time to live.
     */
    @Getter
    private final int timeToLive;

    /**
     * Locations that the boss can auto spawn at.
     */
    @Getter
    private final List<Location> autoSpawnLocations;

    /**
     * Create a new Boss.
     *
     * @param name   The name of the set.
     * @param config The set's config.
     * @param plugin Instance of EcoBosses.
     */
    public EcoBoss(@NotNull final String name,
                   @NotNull final Config config,
                   @NotNull final EcoPlugin plugin) {
        super(plugin);
        this.config = config;
        this.name = name;
        this.livingBosses = new HashMap<>();

        this.displayName = this.getConfig().getString("name");

        // Boss Type
        this.bossType = BossEntityUtils.getBossType(this.getConfig().getString("base-mob"));

        // Boss Bar
        this.bossbarEnabled = this.getConfig().getBool("bossbar.enabled");
        this.bossbarProperties = new BossbarProperties(
                BarColor.valueOf(this.getConfig().getString("bossbar.color").toUpperCase()),
                BarStyle.valueOf(this.getConfig().getString("bossbar.style").toUpperCase())
        );

        // Attributes
        this.attackDamage = this.getConfig().getInt("attack-damage");
        this.maxHealth = this.getConfig().getInt("max-health");
        this.followRange = this.getConfig().getInt("follow-range");
        this.movementSpeedMultiplier = this.getConfig().getInt("movement-speed");
        this.timeToLive = this.getConfig().getInt("time-to-live", -1);

        // Spawn Totem
        this.spawnTotemEnabled = this.getConfig().getBool("spawn-totem.enabled");
        this.spawnTotem = new SpawnTotem(
                Material.getMaterial(this.getConfig().getString("spawn-totem.bottom").toUpperCase()),
                Material.getMaterial(this.getConfig().getString("spawn-totem.middle").toUpperCase()),
                Material.getMaterial(this.getConfig().getString("spawn-totem.top").toUpperCase())
        );
        this.spawnTotemDisabledWorldNames = this.getConfig().getStrings("spawn-totem.world-blacklist").stream().map(String::toLowerCase).collect(Collectors.toList());

        // Rewards
        this.drops = new HashMap<>();
        for (String string : this.getConfig().getStrings("rewards.drops")) {
            YamlConfiguration tempConfig = new YamlConfiguration();
            double chance = 100;
            if (string.contains("::")) {
                String[] split = string.split("::");
                chance = Double.parseDouble(split[0]);
                string = split[1];
            }
            String tempConfigString = new String(Base64.getDecoder().decode(string));
            try {
                tempConfig.loadFromString(tempConfigString);
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
            ItemStack itemStack = tempConfig.getItemStack("drop-key");
            this.drops.put(itemStack, chance);
        }

        this.experienceOptions = new ExperienceOptions(
                this.getConfig().getInt("rewards.xp.minimum"),
                this.getConfig().getInt("rewards.xp.maximum")
        );

        // Immunities
        this.immunityOptions = new ImmunityOptions(
                this.getConfig().getBool("defence.immunities.fire"),
                this.getConfig().getBool("defence.immunities.suffocation"),
                this.getConfig().getBool("defence.immunities.drowning"),
                this.getConfig().getBool("defence.immunities.projectiles"),
                this.getConfig().getBool("defence.immunities.explosion")
        );

        // Multipliers
        this.incomingMultipliers = new HashMap<>();
        double melee = this.getConfig().getDouble("defence.incoming-multipliers.melee");
        this.incomingMultipliers.put(EntityDamageEvent.DamageCause.ENTITY_ATTACK, melee);
        this.incomingMultipliers.put(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, melee);

        double projectile = this.getConfig().getDouble("defence.incoming-multipliers.projectile");
        this.incomingMultipliers.put(EntityDamageEvent.DamageCause.PROJECTILE, projectile);

        // Attack on injure
        this.attackOnInjure = this.getConfig().getBool("attacks.on-injure");

        // Sounds
        this.injureSounds = new ArrayList<>();
        for (String string : this.getConfig().getStrings("sounds.injure")) {
            String[] split = string.split(":");
            this.injureSounds.add(new OptionedSound(
                    Sound.valueOf(split[0].toUpperCase()),
                    Float.parseFloat(split[1]) / 16,
                    Float.parseFloat(split[2])
            ));
        }

        this.deathSounds = new ArrayList<>();
        for (String string : this.getConfig().getStrings("sounds.death")) {
            String[] split = string.split(":");
            this.deathSounds.add(new OptionedSound(
                    Sound.valueOf(split[0].toUpperCase()),
                    Float.parseFloat(split[1]) / 16,
                    Float.parseFloat(split[2])
            ));
        }

        this.summonSounds = new ArrayList<>();
        for (String string : this.getConfig().getStrings("sounds.summon")) {
            String[] split = string.split(":");
            this.summonSounds.add(new OptionedSound(
                    Sound.valueOf(split[0].toUpperCase()),
                    Float.parseFloat(split[1]) / 16,
                    Float.parseFloat(split[2])
            ));
        }

        this.spawnSounds = new ArrayList<>();
        for (String string : this.getConfig().getStrings("sounds.spawn")) {
            String[] split = string.split(":");
            this.spawnSounds.add(new OptionedSound(
                    Sound.valueOf(split[0].toUpperCase()),
                    Float.parseFloat(split[1]) / 16,
                    Float.parseFloat(split[2])
            ));
        }

        // Messages
        this.spawnMessages = new ArrayList<>();
        for (String string : this.getConfig().getStrings("broadcast.spawn")) {
            this.spawnMessages.add(StringUtils.format(string));
        }
        this.deathMessages = new ArrayList<>();
        for (String string : this.getConfig().getStrings("broadcast.death")) {
            this.deathMessages.add(StringUtils.format(string));
        }

        // Top Damager Commands
        this.topDamagerCommands = new HashMap<>();
        for (int i = 1; i <= 3; i++) {
            this.topDamagerCommands.put(i, new ArrayList<>());
            for (String string : this.getConfig().getStrings("rewards.top-damager-commands." + i, false)) {
                double chance = 100;
                if (string.contains("::")) {
                    String[] split = string.split("::");
                    chance = Double.parseDouble(split[0]);
                    string = split[1];
                }
                List<Pair<Double, String>> commands = this.topDamagerCommands.get(i) == null ? new ArrayList<>() : this.topDamagerCommands.get(i);
                commands.add(new Pair<>(chance, string));
                this.topDamagerCommands.put(i, commands);
            }
        }

        // Nearby Rewards
        this.nearbyRadius = this.getConfig().getDouble("rewards.nearby-player-commands.radius");
        this.nearbyPlayersCommands = new HashMap<>();
        for (String string : this.getConfig().getStrings("rewards.nearby-player-commands.commands", false)) {
            double chance = 100;
            if (string.contains("::")) {
                String[] split = string.split("::");
                chance = Double.parseDouble(split[0]);
                string = split[1];
            }
            this.nearbyPlayersCommands.put(string, chance);
        }

        // Effects
        this.effectNames = new HashMap<>();
        for (String string : this.getConfig().getStrings("effects")) {
            String effectName = string.split(":")[0];
            List<String> args = Arrays.asList(string.replace(effectName + ":", "").split(":"));
            this.effectNames.put(effectName, args);
        }

        new HashMap<>(this.effectNames).forEach((string, args) -> {
            if (Effects.getEffect(string, args) == null) {
                this.effectNames.remove(string);
                Bukkit.getLogger().warning("Invalid effect specified in " + this.name);
            }
        });

        // Targeting
        this.targetDistance = this.getConfig().getDouble("attacks.target.range");
        this.targetMode = TargetMode.getByName(this.getConfig().getString("attacks.target.mode"));

        // Boat + Minecarts
        this.disableBoats = this.getConfig().getBool("defence.no-boats");

        // Auto Spawn
        this.autoSpawnInterval = this.getConfig().getInt("auto-spawn-interval");
        this.autoSpawnLocations = new ArrayList<>();
        for (String string : this.getConfig().getStrings("auto-spawn-locations")) {
            String[] split = string.split(":");
            World world = Bukkit.getWorld(split[0]);
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            autoSpawnLocations.add(new Location(world, x, y, z));
        }

        if (this.getConfig().getBool("enabled")) {
            EcoBosses.addBoss(this);
        }
    }

    /**
     * Create effect tickers for Living Boss.
     *
     * @return The effects.
     */
    public List<Effect> createEffects() {
        List<Effect> effects = new ArrayList<>();
        this.effectNames.forEach((string, args) -> {
            effects.add(Effects.getEffect(string, args));
        });

        return effects;
    }

    /**
     * Spawn the boss.
     *
     * @param location The location.
     */
    public void spawn(@NotNull final Location location) {
        location.getChunk().load();

        LivingEntity entity = bossType.spawnBossEntity(location);
        this.livingBosses.put(entity, new LivingEcoBoss(
                        this.getPlugin(),
                        entity,
                        this
                )
        );
    }

    /**
     * Get {@link LivingEcoBoss} from an entity.
     *
     * @param entity The entity.
     * @return The living boss, or null if not a boss.
     */
    public LivingEcoBoss getLivingBoss(@NotNull final LivingEntity entity) {
        return this.livingBosses.get(entity);
    }

    /**
     * Remove living boss.
     *
     * @param entity The entity.
     */
    public void removeLivingBoss(@Nullable final LivingEntity entity) {
        this.livingBosses.remove(entity);
    }

    /**
     * Get all living bosses.
     *
     * @return The living bosses.
     */
    public Map<LivingEntity, LivingEcoBoss> getLivingBosses() {
        return ImmutableMap.copyOf(this.livingBosses);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EcoBoss boss)) {
            return false;
        }

        return this.getName().equals(boss.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getName());
    }

    @Override
    public String toString() {
        return "EcoBoss{"
                + this.getName()
                + "}";
    }
}
