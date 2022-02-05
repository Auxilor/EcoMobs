package com.willfp.ecobosses.bosses;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.core.config.updating.ConfigUpdater;
import com.willfp.ecobosses.EcoBossesPlugin;
import com.willfp.ecobosses.config.BaseBossConfig;
import com.willfp.ecobosses.config.CustomConfig;
import lombok.experimental.UtilityClass;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class EcoBosses {
    /**
     * Registered armor sets.
     */
    private static final BiMap<String, com.willfp.ecobosses.bosses.EcoBoss> BY_NAME = HashBiMap.create();

    /**
     * Sets that exist by default.
     */
    private static final List<String> DEFAULT_BOSSES = Arrays.asList(
            "dark_guardian",
            "tarantula",
            "steel_golem",
            "alpha_wolf"
    );

    /**
     * Get all registered {@link com.willfp.ecobosses.bosses.EcoBoss}es.
     *
     * @return A list of all {@link com.willfp.ecobosses.bosses.EcoBoss}es.
     */
    public static List<com.willfp.ecobosses.bosses.EcoBoss> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Get {@link com.willfp.ecobosses.bosses.EcoBoss} matching name.
     *
     * @param name The name to search for.
     * @return The matching {@link com.willfp.ecobosses.bosses.EcoBoss}, or null if not found.
     */
    public static com.willfp.ecobosses.bosses.EcoBoss getByName(@NotNull final String name) {
        return BY_NAME.get(name);
    }

    /**
     * Update all {@link com.willfp.ecobosses.bosses.EcoBoss}s.
     */
    @ConfigUpdater
    public static void update() {
        for (com.willfp.ecobosses.bosses.EcoBoss boss : values()) {
            removeBoss(boss);
        }

        for (String defaultSetName : DEFAULT_BOSSES) {
            new com.willfp.ecobosses.bosses.EcoBoss(defaultSetName, new BaseBossConfig(defaultSetName), EcoBossesPlugin.getInstance());
        }

        try {
            Files.walk(Paths.get(new File(EcoBossesPlugin.getInstance().getDataFolder(), "bosses/").toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString().replace(".yml", "");
                        new com.willfp.ecobosses.bosses.EcoBoss(
                                name,
                                new CustomConfig(name, YamlConfiguration.loadConfiguration(path.toFile())),
                                EcoBossesPlugin.getInstance()
                        );
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add new {@link com.willfp.ecobosses.bosses.EcoBoss} to EcoBosses.
     *
     * @param set The {@link com.willfp.ecobosses.bosses.EcoBoss} to add.
     */
    public static void addBoss(@NotNull final com.willfp.ecobosses.bosses.EcoBoss set) {
        BY_NAME.remove(set.getId());
        BY_NAME.put(set.getId(), set);
    }

    /**
     * Remove {@link com.willfp.ecobosses.bosses.EcoBoss} from EcoBosses.
     *
     * @param set The {@link com.willfp.ecobosses.bosses.EcoBoss} to remove.
     */
    public static void removeBoss(@NotNull final EcoBoss set) {
        BY_NAME.remove(set.getId());
    }
}
