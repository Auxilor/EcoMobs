package com.willfp.illusioner.illusioner.options;

import com.willfp.illusioner.illusioner.BlockStructure;
import com.willfp.illusioner.util.NumberUtils;
import com.willfp.illusioner.util.internal.OptionedSound;
import com.willfp.illusioner.util.tuplets.Pair;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class IllusionerOptions {
    private BarColor color;
    private BarStyle style;
    private String name;
    private Set<OptionedSound> spawnSounds;
    private Set<OptionedSound> deathSounds;
    private Pair<Integer, Integer> xpBounds;
    private BlockStructure spawnStructure;
    private double maxHealth;
    private double attackDamage;
    private Set<ItemStack> drops;
    private GameplayOptions gameplayOptions;

    public IllusionerOptions() {
        reload();
    }

    public void reload() {


        gameplayOptions.reload();
    }

    public BarColor getColor() {
        return color;
    }

    public BarStyle getStyle() {
        return style;
    }

    public String getName() {
        return name;
    }

    public Set<OptionedSound> getSpawnSounds() {
        return spawnSounds;
    }

    public Set<OptionedSound> getDeathSounds() {
        return deathSounds;
    }

    public int generateXp() {
        return NumberUtils.randInt(xpBounds.getFirst(), xpBounds.getSecond());
    }

    public BlockStructure getSpawnStructure() {
        return spawnStructure;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getAttackDamage() {
        return attackDamage;
    }

    public Set<ItemStack> getDrops() {
        return drops;
    }

    public GameplayOptions getGameplayOptions() {
        return gameplayOptions;
    }
}
