package com.willfp.illusioner.proxy.v1_16_R3;

import com.willfp.illusioner.proxy.proxies.EntityIllusionerProxy;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.EntityIllagerIllusioner;
import net.minecraft.server.v1_16_R3.EntityIllagerWizard;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityIronGolem;
import net.minecraft.server.v1_16_R3.EntityRaider;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.EntityVillagerAbstract;
import net.minecraft.server.v1_16_R3.GenericAttributes;
import net.minecraft.server.v1_16_R3.PathfinderGoalBowShoot;
import net.minecraft.server.v1_16_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_16_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomStroll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public class EntityIllusioner extends EntityIllagerIllusioner implements EntityIllusionerProxy {
    /**
     * The display name for the illusioner.
     */
    private final String displayName;

    /**
     * The boss bar linked to the illusioner.
     */
    private BossBar bossBar = null;

    /**
     * Instantiate a new illusioner entity.
     *
     * @param location     The location to spawn it at.
     * @param maxHealth    The max health for the illusioner to have.
     * @param attackDamage The attack damage for the illusioner to have.
     * @param name         The name of the illusioner.
     */
    public EntityIllusioner(@NotNull final Location location,
                            final double maxHealth,
                            final double attackDamage,
                            @NotNull final String name) {
        super(EntityTypes.ILLUSIONER, ((CraftWorld) location.getWorld()).getHandle());
        this.displayName = name;

        this.setPosition(location.getX(), location.getY(), location.getZ());

        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(maxHealth);
        this.setHealth((float) maxHealth);

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(attackDamage);

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityIllagerWizard.b());
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(2, new PathfinderGoalBowShoot<>(this, 1.0D, 20, 15.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(6, new PathfinderGoalBowShoot(this, 0.5D, 20, 15.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0]));
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget(this, EntityVillagerAbstract.class, false)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget(this, EntityIronGolem.class, false)).a(300));
    }

    @Override
    public BossBar createBossbar(@NotNull final Plugin plugin,
                                 @NotNull final BarColor color,
                                 @NotNull final BarStyle style) {
        if (bossBar != null) {
            return bossBar;
        }
        BossBar bossBar = Bukkit.getServer().createBossBar(this.displayName, color, style, (BarFlag) null);
        this.bossBar = bossBar;

        LivingEntity entity = (LivingEntity) this.getBukkitEntity();

        entity.getNearbyEntities(50, 50, 50).forEach(entity1 -> {
            if (entity1 instanceof Player) {
                bossBar.addPlayer((Player) entity1);
            }
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!entity.isDead()) {
                    bossBar.setProgress(entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                } else {
                    bossBar.getPlayers().forEach(bossBar::removePlayer);
                    bossBar.setVisible(false);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);

        return bossBar;
    }
}
