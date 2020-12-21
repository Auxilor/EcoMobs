package com.willfp.illusioner.nms;

import com.willfp.illusioner.IllusionerPlugin;
import com.willfp.illusioner.nms.api.EntityIllusionerWrapper;
import com.willfp.illusioner.nms.api.IllusionerWrapper;
import org.bukkit.Location;
import org.bukkit.entity.Illusioner;
import org.jetbrains.annotations.ApiStatus;

/**
 * Utility class to manage NMS illusioner
 */
public class NMSIllusioner {
    private static IllusionerWrapper illusionerWrapper;

    @ApiStatus.Internal
    public static boolean init() {
        try {
            final Class<?> class2 = Class.forName("com.willfp.illusioner." + IllusionerPlugin.NMS_VERSION + ".Illusioner");
            if (IllusionerWrapper.class.isAssignableFrom(class2)) {
                illusionerWrapper = (IllusionerWrapper) class2.getConstructor().newInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
            illusionerWrapper = null;
        }
        return illusionerWrapper != null;
    }

    /**
     * Spawn a new Illusioner Boss
     *
     * @param location     The location to spawn it at
     * @param maxHealth    The max health for the illusioner to have
     * @param attackDamage The attack damage for the illusioner
     * @param name         The name of the illusioner
     * @return The illusioner
     */
    public static EntityIllusionerWrapper spawn(Location location, double maxHealth, double attackDamage, String name) {
        assert illusionerWrapper != null;
        return illusionerWrapper.spawn(location, maxHealth, attackDamage, name);
    }

    /**
     * Adapt an existing Illusioner into an Illusioner boss
     *
     * @param illusioner   The Illusioner to adapt
     * @param maxHealth    The max health for the illusioner to have
     * @param attackDamage The attack damage for the illusioner
     * @param name         The name of the Illusioner
     * @return The illusioner
     */
    public static EntityIllusionerWrapper convertIllusioner(Illusioner illusioner, double maxHealth, double attackDamage, String name) {
        assert illusionerWrapper != null;
        return illusionerWrapper.adapt(illusioner, illusioner.getLocation(), maxHealth, attackDamage, name);
    }
}
