package com.willfp.illusioner.v1_16_R2;

import com.willfp.illusioner.nms.api.OpenInventoryWrapper;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class OpenInventory implements OpenInventoryWrapper {
    @Override
    public Object getOpenInventory(Player player) {
        return ((CraftPlayer) player).getHandle().activeContainer;
    }
}
