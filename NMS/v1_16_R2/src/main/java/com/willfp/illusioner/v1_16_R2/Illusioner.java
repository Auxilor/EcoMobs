package com.willfp.illusioner.v1_16_R2;

import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import com.willfp.illusioner.nms.api.IllusionerWrapper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftIllusioner;

public class Illusioner implements IllusionerWrapper {
    @Override
    public EntityIllusionerWrapper spawn(Location location, double maxHealth, double attackDamage, String name) {
        EntityIllusioner illusioner = new EntityIllusioner(location, maxHealth, attackDamage, name);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(illusioner);
        return illusioner;
    }

    @Override
    public EntityIllusionerWrapper adapt(org.bukkit.entity.Illusioner illusioner, Location location, double maxHealth, double attackDamage, String name) {
        EntityIllusioner internalIllusioner = new EntityIllusioner(location, maxHealth, attackDamage, name);
        if(!(illusioner instanceof CraftIllusioner)) return null;
        ((CraftIllusioner) illusioner).setHandle(internalIllusioner);
        return internalIllusioner;
    }
}
