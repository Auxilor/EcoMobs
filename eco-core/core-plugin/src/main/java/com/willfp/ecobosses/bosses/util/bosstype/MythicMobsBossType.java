package com.willfp.ecobosses.bosses.util.bosstype;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.AbstractWorld;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class MythicMobsBossType extends BossType {

    /**
     * The entity type.
     */
    private final MythicMob boss;

    /**
     * Level of MythicMobs mob
     */
    private final int level;

    /**
     * Create new vanilla boss type.
     *
     * @param boss The MythicMob.
     */
    public MythicMobsBossType(MythicMob boss, int level) {
        this.boss = boss;
        this.level = level;
    }


    @Override
    public LivingEntity spawnBossEntity(@NotNull Location location) {
        try {
            return (LivingEntity) MythicMobs.inst().getAPIHelper().spawnMythicMob(boss, location, level);
        } catch (InvalidMobTypeException e) {
            e.printStackTrace();
        }
        return null;
    }


}
