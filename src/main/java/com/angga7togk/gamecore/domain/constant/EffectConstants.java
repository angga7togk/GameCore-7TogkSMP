package com.angga7togk.gamecore.domain.constant;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.angga7togk.gamecore.domain.types.Rarity;

import cn.nukkit.potion.Effect;

public class EffectConstants {

    public static final Map<Integer, String> EFFECT_NAME_MAP = Map.ofEntries(
            Map.entry(Effect.NO_EFFECT, "No Effect"),

            Map.entry(Effect.SPEED, "Speed"),
            Map.entry(Effect.SLOWNESS, "Slowness"),
            Map.entry(Effect.HASTE, "Haste"),
            Map.entry(Effect.FATIGUE, "Mining Fatigue"),
            Map.entry(Effect.STRENGTH, "Strength"),
            Map.entry(Effect.HEALING, "Instant Health"),
            Map.entry(Effect.HARMING, "Instant Damage"),
            Map.entry(Effect.JUMP, "Jump Boost"),
            Map.entry(Effect.NAUSEA, "Nausea"),
            Map.entry(Effect.REGENERATION, "Regeneration"),
            Map.entry(Effect.DAMAGE_RESISTANCE, "Resistance"),
            Map.entry(Effect.FIRE_RESISTANCE, "Fire Resistance"),
            Map.entry(Effect.WATER_BREATHING, "Water Breathing"),
            Map.entry(Effect.INVISIBILITY, "Invisibility"),
            Map.entry(Effect.BLINDNESS, "Blindness"),
            Map.entry(Effect.NIGHT_VISION, "Night Vision"),
            Map.entry(Effect.HUNGER, "Hunger"),
            Map.entry(Effect.WEAKNESS, "Weakness"),
            Map.entry(Effect.POISON, "Poison"),
            Map.entry(Effect.WITHER, "Wither"),
            Map.entry(Effect.HEALTH_BOOST, "Health Boost"),
            Map.entry(Effect.ABSORPTION, "Absorption"),
            Map.entry(Effect.SATURATION, "Saturation"),
            Map.entry(Effect.LEVITATION, "Levitation"),
            Map.entry(Effect.FATAL_POISON, "Fatal Poison"),
            Map.entry(Effect.CONDUIT_POWER, "Conduit Power"),
            Map.entry(Effect.SLOW_FALLING, "Slow Falling"),
            Map.entry(Effect.BAD_OMEN, "Bad Omen"),
            Map.entry(Effect.VILLAGE_HERO, "Hero of the Village"),
            Map.entry(Effect.DARKNESS, "Darkness"),
            Map.entry(Effect.TRIAL_OMEN, "Trial Omen"),
            Map.entry(Effect.WIND_CHARGED, "Wind Charged"),
            Map.entry(Effect.WEAVING, "Weaving"),
            Map.entry(Effect.OOZING, "Oozing"),
            Map.entry(Effect.INFESTED, "Infested"),
            Map.entry(Effect.RAID_OMEN, "Raid Omen"));

    private static final Map<Rarity, List<Integer>> RARITY_EFFECTS = Map.of(
            Rarity.COMMON, List.of(
                    Effect.SPEED,
                    Effect.HASTE,
                    Effect.JUMP,
                    Effect.NIGHT_VISION,
                    Effect.POISON),

            Rarity.UNCOMMON, List.of(
                    Effect.STRENGTH,
                    Effect.REGENERATION,
                    Effect.FIRE_RESISTANCE,
                    Effect.WATER_BREATHING,
                    Effect.HUNGER),

            Rarity.RARE, List.of(
                    Effect.RESISTANCE,
                    Effect.ABSORPTION,
                    Effect.SATURATION,
                    Effect.WITHER),

            Rarity.SPECIAL, List.of(
                    Effect.SLOW_FALLING,
                    Effect.CONDUIT_POWER,
                    Effect.HEALTH_BOOST,
                    Effect.BLINDNESS),

            Rarity.LEGENDARY, List.of(
                    Effect.INVISIBILITY));

    // ================= BASIC RANDOM =================

    public static Effect getRandomEffect(Rarity rarity) {
        List<Integer> list = RARITY_EFFECTS.get(rarity);

        if (list == null || list.isEmpty()) {
            return Effect.getEffect(Effect.SPEED);
        }

        return Effect.getEffect(list.get(
                ThreadLocalRandom.current().nextInt(list.size())));
    }

    // ================= WEIGHTED RANDOM (RARITY + EFFECT) =================

    public static Effect getWeightedRandomEffect() {
        Rarity rarity = Rarity.rollRarityWeighted();

        return getRandomEffect(rarity).setAmplifier(amplifierFromRarity(rarity)).setDuration(20 * 20);
    }

    private static int amplifierFromRarity(Rarity rarity) {
        return switch (rarity) {
            case COMMON, UNCOMMON -> 0;
            case RARE, SPECIAL -> 1;
            case MYTHIC -> 2;
            case LEGENDARY -> 3;
            default -> 0;
        };
    }
}