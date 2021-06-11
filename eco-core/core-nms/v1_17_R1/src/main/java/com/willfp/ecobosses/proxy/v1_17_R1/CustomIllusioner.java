package com.willfp.ecobosses.proxy.v1_17_R1;

import com.willfp.ecobosses.proxy.proxies.CustomIllusionerProxy;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.monster.EntityIllagerIllusioner;
import net.minecraft.world.entity.monster.EntityIllagerWizard;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.EntityRaider;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Illusioner;
import org.jetbrains.annotations.NotNull;

public class CustomIllusioner extends EntityIllagerIllusioner implements CustomIllusionerProxy {
    /**
     * Instantiate a new custom illusioner entity.
     *
     * @param location The location to spawn it at.
     */
    public CustomIllusioner(@NotNull final Location location) {
        super(EntityTypes.O, ((CraftWorld) location.getWorld()).getHandle());

        this.setPosition(location.getX(), location.getY(), location.getZ());

        this.bO.a(0, new PathfinderGoalFloat(this));
        this.bO.a(1, new EntityIllagerWizard.b());
        this.bO.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.bO.a(2, new PathfinderGoalBowShoot<>(this, 1.0D, 20, 15.0F));
        this.bO.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bO.a(0, new PathfinderGoalFloat(this));
        this.bO.a(6, new PathfinderGoalBowShoot<>(this, 0.5D, 20, 15.0F));
        this.bO.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.bO.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.bO.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.bP.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0]));
        this.bP.a(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).a(300));
        this.bP.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).a(300));
        this.bP.a(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false)).a(300));
    }

    /**
     * Spawn illusioner.
     *
     * @param location The location.
     * @return The illusioner.
     */
    public static Illusioner spawn(@NotNull final Location location) {
        CustomIllusioner illusioner = new CustomIllusioner(location);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(illusioner);
        return (Illusioner) illusioner.getBukkitEntity();
    }
}
