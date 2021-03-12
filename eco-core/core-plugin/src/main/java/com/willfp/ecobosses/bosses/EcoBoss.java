package com.willfp.ecobosses.bosses;

import com.willfp.eco.internal.config.AbstractUndefinedConfig;
import com.willfp.eco.util.internal.PluginDependent;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.ecobosses.bosses.util.bosstype.BossType;
import com.willfp.ecobosses.bosses.util.obj.BossbarProperties;
import com.willfp.ecobosses.bosses.util.obj.ImmunityOptions;
import com.willfp.ecobosses.bosses.util.obj.SpawnTotem;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class EcoBoss extends PluginDependent {
    /**
     * The name of the boss.
     */
    @Getter
    private final String name;

    /**
     * The config of the set.
     */
    @Getter(AccessLevel.PRIVATE)
    private final AbstractUndefinedConfig config;

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
     * The immunity options.
     */
    @Getter
    private final ImmunityOptions immunityOptions;

    /**
     * The drops.
     */
    @Getter
    private final List<ItemStack> drops;

    /**
     * Create a new Boss.
     *
     * @param name   The name of the set.
     * @param config The set's config.
     * @param plugin Instance of EcoBosses.
     */
    public EcoBoss(@NotNull final String name,
                   @NotNull final AbstractUndefinedConfig config,
                   @NotNull final AbstractEcoPlugin plugin) {
        super(plugin);
        this.config = config;
        this.name = name;

        if (this.getConfig().getBool("enabled")) {
            EcoBosses.addBoss(this);
        }
    }

    /**
     * Spawn the boss.
     *
     * @param location The location.
     */
    public void spawn(@NotNull final Location location) {
        LivingEntity entity = bossType.spawnBossEntity(location);
        entity.getPersistentDataContainer().set(this.getPlugin().getNamespacedKeyFactory().create("boss"), PersistentDataType.STRING, name);
        entity.setPersistent(true);

        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        assert maxHealth != null;
        maxHealth.setBaseValue(this.getMaxHealth());

        AttributeInstance attackDamage = entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        assert attackDamage != null;
        attackDamage.setBaseValue(this.getAttackDamage());

        createBossBar(entity);
    }

    private void createBossBar(@NotNull final LivingEntity entity) {
        BossBar bossBar = Bukkit.getServer().createBossBar(
                this.getDisplayName(),
                this.getBossbarProperties().getColor(),
                this.getBossbarProperties().getStyle(),
                (BarFlag) null
        );

        int radius = this.getPlugin().getConfigYml().getInt("bossbar-radius");

        this.getPlugin().getRunnableFactory().create(runnable -> {
            if (!entity.isDead()) {
                bossBar.getPlayers().forEach(bossBar::removePlayer);
                entity.getNearbyEntities(radius, radius, radius).forEach(entity1 -> {
                    if (entity1 instanceof Player) {
                        bossBar.addPlayer((Player) entity1);
                    }
                });
            } else {
                runnable.cancel();
            }
        }).runTaskTimer(0, 40);

        this.getPlugin().getRunnableFactory().create(runnable -> {
            if (!entity.isDead()) {
                bossBar.setProgress(entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            } else {
                bossBar.getPlayers().forEach(bossBar::removePlayer);
                bossBar.setVisible(false);
                runnable.cancel();
            }
        }).runTaskTimer(0, 1);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof EcoBoss)) {
            return false;
        }

        EcoBoss boss = (EcoBoss) o;
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
