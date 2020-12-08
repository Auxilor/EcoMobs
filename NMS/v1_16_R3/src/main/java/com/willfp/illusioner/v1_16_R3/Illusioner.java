package com.willfp.illusioner.v1_16_R3;

import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import com.willfp.illusioner.nms.api.IllusionerWrapper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class Illusioner implements IllusionerWrapper {
    @Override
    public EntityIllusionerWrapper spawn(Location location, double maxHealth, double attackDamage) {
        EntityIllusioner illusioner = new EntityIllusioner(location, maxHealth, attackDamage);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(illusioner);
        return illusioner;
    }
}
