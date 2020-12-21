package com.willfp.illusioner.v1_16_R1;

import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityIllusioner extends EntityIllagerIllusioner implements EntityIllusionerWrapper {
    private final String displayName;

    public EntityIllusioner(Location location, double maxHealth, double attackDamage, String name) {
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
    public BossBar createBossbar(Plugin plugin, BarColor color, BarStyle style) {
        BossBar bossBar = Bukkit.getServer().createBossBar(this.displayName, color, style);
        Bukkit.getServer().getOnlinePlayers().forEach(bossBar::addPlayer);
        LivingEntity entity = (LivingEntity) this.getBukkitEntity();

        new BukkitRunnable() {
            @Override
            public void run() {
                if(!entity.isDead()) {
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
