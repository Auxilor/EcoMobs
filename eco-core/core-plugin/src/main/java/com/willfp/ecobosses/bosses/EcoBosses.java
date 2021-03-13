package com.willfp.ecobosses.bosses;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.willfp.eco.util.config.updating.annotations.ConfigUpdater;
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
    private static final BiMap<String, EcoBoss> BY_NAME = HashBiMap.create();

    /**
     * Sets that exist by default.
     */
    private static final List<String> DEFAULT_BOSSES = Arrays.asList(
            "illusioner",
            "tarantula",
            "steel_golem"
    );

    /**
     * Get all registered {@link EcoBoss}es.
     *
     * @return A list of all {@link EcoBoss}es.
     */
    public static List<EcoBoss> values() {
        return ImmutableList.copyOf(BY_NAME.values());
    }

    /**
     * Get {@link EcoBoss} matching name.
     *
     * @param name The name to search for.
     * @return The matching {@link EcoBoss}, or null if not found.
     */
    public static EcoBoss getByName(@NotNull final String name) {
        return BY_NAME.get(name);
    }

    /**
     * Update all {@link EcoBoss}s.
     */
    @ConfigUpdater
    public static void update() {
        for (EcoBoss boss : values()) {
            removeBoss(boss);
        }

        for (String defaultSetName : DEFAULT_BOSSES) {
            new EcoBoss(defaultSetName, new BaseBossConfig(defaultSetName), EcoBossesPlugin.getInstance());
        }

        try {
            Files.walk(Paths.get(new File(EcoBossesPlugin.getInstance().getDataFolder(), "bosses/").toURI()))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        String name = path.getFileName().toString().replace(".yml", "");
                        new EcoBoss(
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
     * Add new {@link EcoBoss} to EcoBosses.
     *
     * @param set The {@link EcoBoss} to add.
     */
    public static void addBoss(@NotNull final EcoBoss set) {
        BY_NAME.remove(set.getName());
        BY_NAME.put(set.getName(), set);
    }

    /**
     * Remove {@link EcoBoss} from EcoBosses.
     *
     * @param set The {@link EcoBoss} to remove.
     */
    public static void removeBoss(@NotNull final EcoBoss set) {
        BY_NAME.remove(set.getName());
    }
}
