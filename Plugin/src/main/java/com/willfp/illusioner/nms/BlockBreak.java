package com.willfp.illusioner.nms;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.nms.api.BlockBreakWrapper;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

/**
 * Utility class to break a block as if the player had done it manually
 */
public class BlockBreak {
    private static BlockBreakWrapper blockBreakWrapper;

    @ApiStatus.Internal
    public static boolean init() {
        try {
            final Class<?> class2 = Class.forName("com.willfp.illusioner." + IllusionerPlugin.NMS_VERSION + ".BlockBreak");
            if (BlockBreakWrapper.class.isAssignableFrom(class2)) {
                blockBreakWrapper = (BlockBreakWrapper) class2.getConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            blockBreakWrapper = null;
        }
        return blockBreakWrapper != null;
    }

    /**
     * Break a block as a player
     *
     * @param player The player to break the block as
     * @param block  The block to break
     */
    public static void breakBlock(Player player, Block block) {
        assert blockBreakWrapper != null;
        blockBreakWrapper.breakBlock(player, block);
    }
}
