package com.willfp.ecobosses.proxy.v1_17_R1;

import com.willfp.ecobosses.proxy.proxies.CustomIllusionerProxy;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Illusioner;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

public class CustomIllusioner extends net.minecraft.world.entity.monster.Illusioner implements CustomIllusionerProxy {
    /**
     * Instantiate a new custom illusioner entity.
     *
     * @param location The location to spawn it at.
     */
    public CustomIllusioner(@NotNull final Location location) {
        super(EntityType.ILLUSIONER, ((CraftWorld) location.getWorld()).getHandle());

        this.setPos(location.getX(), location.getY(), location.getZ());

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Raider.HoldGroundAttackGoal(this, 25));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new RangedBowAttackGoal<>(this, 0.5D, 20, 15.0F));
        this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, Raider.class)).setAlertOthers());
        this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal<>(this, IronGolem.class, false)).setUnseenMemoryTicks(300));
    }

    /**
     * Spawn illusioner.
     *
     * @param location The location.
     * @return The illusioner.
     */
    public static Illusioner spawn(@NotNull final Location location) {
        CustomIllusioner illusioner = new CustomIllusioner(location);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(illusioner, CreatureSpawnEvent.SpawnReason.CUSTOM);
        return (Illusioner) illusioner.getBukkitEntity();
    }
}
