package com.willfp.illusioner.v1_15_R1;

import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import com.willfp.illusioner.nms.api.IllusionerWrapper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;

public class Illusioner implements IllusionerWrapper {
    @Override
    public EntityIllusionerWrapper spawn(Location location, double maxHealth, double attackDamage, String name) {
        EntityIllusioner illusioner = new EntityIllusioner(location, maxHealth, attackDamage, name);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(illusioner);
        return illusioner;
    }
}
