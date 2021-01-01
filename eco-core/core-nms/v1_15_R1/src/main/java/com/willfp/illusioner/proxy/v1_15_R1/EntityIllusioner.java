package com.willfp.illusioner.proxy.v1_15_R1;

import com.willfp.illusioner.proxy.proxies.EntityIllusionerProxy;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityIllagerIllusioner;
import net.minecraft.server.v1_15_R1.EntityIllagerWizard;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.EntityIronGolem;
import net.minecraft.server.v1_15_R1.EntityRaider;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVillagerAbstract;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import net.minecraft.server.v1_15_R1.PathfinderGoalBowShoot;
import net.minecraft.server.v1_15_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_15_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_15_R1.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_15_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalRandomStroll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
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
        Bukkit.getServer().getOnlinePlayers().forEach(bossBar::addPlayer);
        LivingEntity entity = (LivingEntity) this.getBukkitEntity();

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
