package com.willfp.ecobosses.bosses.util.requirement;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.ecobosses.bosses.util.requirement.requirements.RequirementHasPermission;
import com.willfp.ecobosses.bosses.util.requirement.requirements.RequirementPlaceholderEquals;
import com.willfp.ecobosses.bosses.util.requirement.requirements.RequirementPlaceholderGreaterThan;
import com.willfp.ecobosses.bosses.util.requirement.requirements.RequirementPlaceholderLessThan;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@UtilityClass
@SuppressWarnings({"unused", "checkstyle:JavadocVariable"})
public class SpawnRequirements {
    /**
     * All registered requirements.
     */
    private static final BiMap<String, SpawnRequirement> BY_ID = HashBiMap.create();

    public static final SpawnRequirement HAS_PERMISSION = new RequirementHasPermission();
    public static final SpawnRequirement PLACEHOLDER_EQUALS = new RequirementPlaceholderEquals();
    public static final SpawnRequirement PLACEHOLDER_GREATER_THAN = new RequirementPlaceholderGreaterThan();
    public static final SpawnRequirement PLACEHOLDER_LESS_THAN = new RequirementPlaceholderLessThan();

    /**
     * Get all registered requirements.
     *
     * @return A list of all requirements.
     */
    public static List<SpawnRequirement> values() {
        return ImmutableList.copyOf(BY_ID.values());
    }

    /**
     * Get {@link SpawnRequirement} matching ID.
     *
     * @param name The ID to search for.
     * @return The matching {@link SpawnRequirement}, or null if not found.
     */
    public static SpawnRequirement getByID(@NotNull final String name) {
        return BY_ID.get(name);
    }

    /**
     * Add new {@link SpawnRequirement}.
     * <p>
     * Only for internal use, requirements are automatically added in the constructor.
     *
     * @param req The {@link SpawnRequirement} to add.
     */
    public static void addNewRequirement(@NotNull final SpawnRequirement req) {
        BY_ID.inverse().remove(req);
        BY_ID.put(req.getId(), req);
    }

    /**
     * Remove {@link SpawnRequirement}.
     *
     * @param req The {@link SpawnRequirement} to remove.
     */
    public static void removeRequirement(@NotNull final SpawnRequirement req) {
        BY_ID.inverse().remove(req);
    }
}