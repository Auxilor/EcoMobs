package com.willfp.illusioner.proxy.v1_16_R2;

import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.illusioner.illusioner.IllusionerManager;
import com.willfp.illusioner.proxy.proxies.EntityIllusionerProxy;
import net.minecraft.server.v1_16_R2.EntityHuman;
import net.minecraft.server.v1_16_R2.EntityIllagerIllusioner;
import net.minecraft.server.v1_16_R2.EntityIllagerWizard;
import net.minecraft.server.v1_16_R2.EntityInsentient;
import net.minecraft.server.v1_16_R2.EntityIronGolem;
import net.minecraft.server.v1_16_R2.EntityRaider;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.EntityVillagerAbstract;
import net.minecraft.server.v1_16_R2.GenericAttributes;
import net.minecraft.server.v1_16_R2.PathfinderGoalBowShoot;
import net.minecraft.server.v1_16_R2.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R2.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_16_R2.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_16_R2.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_16_R2.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_16_R2.PathfinderGoalRandomStroll;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftNamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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
     */
    public EntityIllusioner(@NotNull final Location location) {
        super(EntityTypes.ILLUSIONER, ((CraftWorld) location.getWorld()).getHandle());

        this.getBukkitEntity().getPersistentDataContainer().set(CraftNamespacedKey.fromString("illusioner:illusioner"), PersistentDataType.INTEGER, 1);

        this.displayName = IllusionerManager.OPTIONS.getName();

        this.setPosition(location.getX(), location.getY(), location.getZ());

        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(IllusionerManager.OPTIONS.getMaxHealth());
        this.setHealth((float) IllusionerManager.OPTIONS.getMaxHealth());

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(IllusionerManager.OPTIONS.getAttackDamage());

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityIllagerWizard.b());
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(2, new PathfinderGoalBowShoot<>(this, 1.0D, 20, 15.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(6, new PathfinderGoalBowShoot<>(this, 0.5D, 20, 15.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0]));
        this.targetSelector.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).a(300));
        this.targetSelector.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false)).a(300));
    }

    @Override
    public BossBar createBossbar(@NotNull final AbstractEcoPlugin plugin) {
        if (bossBar != null) {
            return bossBar;
        }
        BossBar bossBar = Bukkit.getServer().createBossBar(this.displayName, IllusionerManager.OPTIONS.getColor(), IllusionerManager.OPTIONS.getStyle(), (BarFlag) null);
        this.bossBar = bossBar;

        LivingEntity entity = (LivingEntity) this.getBukkitEntity();

        plugin.getRunnableFactory().create(runnable -> {
            if (!entity.isDead()) {
                bossBar.getPlayers().forEach(bossBar::removePlayer);
                entity.getNearbyEntities(50, 50, 50).forEach(entity1 -> {
                    if (entity1 instanceof Player) {
                        bossBar.addPlayer((Player) entity1);
                    }
                });
            } else {
                runnable.cancel();
            }
        }).runTaskTimer(0, 40);

        plugin.getRunnableFactory().create(runnable -> {
            if (!entity.isDead()) {
                bossBar.setProgress(entity.getHealth() / entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            } else {
                bossBar.getPlayers().forEach(bossBar::removePlayer);
                bossBar.setVisible(false);
                runnable.cancel();
            }
        }).runTaskTimer(0, 1);

        return bossBar;
    }
}
