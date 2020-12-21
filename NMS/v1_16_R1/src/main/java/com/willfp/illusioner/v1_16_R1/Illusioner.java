package com.willfp.illusioner.v1_16_R1;

import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import com.willfp.illusioner.nms.api.IllusionerWrapper;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftIllusioner;

public class Illusioner implements IllusionerWrapper {
    @Override
    public EntityIllusionerWrapper spawn(Location location, double maxHealth, double attackDamage, String name) {
        EntityIllusioner illusioner = new EntityIllusioner(location, maxHealth, attackDamage, name);
        ((CraftWorld) location.getWorld()).getHandle().addEntity(illusioner);
        return illusioner;
    }

    @Override
    public EntityIllusionerWrapper adapt(org.bukkit.entity.Illusioner illusioner, Location location, double maxHealth, double attackDamage, String name) {
        if(illusioner instanceof CraftIllusioner) {
            if(((CraftIllusioner) illusioner).getHandle() instanceof EntityIllusionerWrapper) return null;
        } else return null;
        illusioner.remove();
        return spawn(location, maxHealth, attackDamage, name);
    }
}
