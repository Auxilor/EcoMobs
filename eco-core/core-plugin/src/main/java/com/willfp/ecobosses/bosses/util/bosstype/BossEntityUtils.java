package com.willfp.ecobosses.bosses.util.bosstype;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.MythicMob;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@UtilityClass
@SuppressWarnings("unchecked")
public class BossEntityUtils {
    /**
     * Get boss type.
     *
     * @param id The name.
     * @return The boss type.
     */
    public static BossType getBossType(@NotNull String id) {

        if (id.startsWith("mythicmobs:")) {
            int level;

            try {
                level = Integer.parseInt(Arrays.stream(id.split("_")).toList().get(id.split("_").length - 1));
            } catch (NumberFormatException exception) {
                level = 1;
            }

            MythicMob mob = MythicMobs.inst().getMobManager().getMythicMob(id.replace("mythicmobs:", "")
                    .replace("_" + level, ""));

            if (mob != null) {
                return new MythicMobsBossType(mob, level);
            }
        }

        if (id.equalsIgnoreCase("charged_creeper")) {
            return new ChargedCreeperBossType();
        }

        try {
            Class<? extends LivingEntity> type = (Class<? extends LivingEntity>) EntityType.valueOf(id.toUpperCase()).getEntityClass();
            assert type != null;
            return new VanillaBossType(type);
        } catch (IllegalArgumentException ignored) {
        }

        return new VanillaBossType(Zombie.class);
    }
}
