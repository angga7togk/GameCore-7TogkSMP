package com.angga7togk.gamecore.domain.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.angga7togk.gamecore.utils.Utils;

public enum Rarity {

    COMMON("§7", 80),
    UNCOMMON("§a", 50),
    RARE("§d", 12),
    SPECIAL("§b", 6),
    MYTHIC("§5", 3),
    LEGENDARY("§c", 1),
    UNKNOWN("§4", 0.3F);

    private final String color;
    private final float weight;

    Rarity(String color, float weight) {
        this.color = color;
        this.weight = weight;
    }

    public String color() {
        return color;
    }

    public float weight() {
        return weight;
    }

    public String displayName() {
        return Utils.toHumanReadable(this.name());
    }

    public long price() {
        long base = 50;
        return (long) (base * Math.pow(2.3, this.ordinal()));
    }

    /**
     * Cocok digunakan random yang ga komplek banget bukan RNG
     */
    public static Rarity rollRarityWeighted() {
        double total = 0;

        for (Rarity r : values()) {
            total += r.weight();
        }

        double rand = ThreadLocalRandom.current().nextDouble(total);
        double cur = 0;

        for (Rarity r : values()) {
            cur += r.weight();
            if (rand < cur) {
                return r;
            }
        }

        return Rarity.COMMON;
    }

    public static Rarity random() {
        return random(List.of(COMMON, UNCOMMON, RARE, SPECIAL, MYTHIC, LEGENDARY));
    }

    public static Rarity random(Rarity... rarities) {
        return random(List.of(rarities));
    }

    public static Rarity random(List<Rarity> pool) {
        double bias = 1.7; // makin gede makin susah
        double total = 0;

        for (Rarity r : pool) {
            total += Math.pow(r.weight, bias);
        }

        double rand = ThreadLocalRandom.current().nextDouble(total);
        double cur = 0;

        for (Rarity r : pool) {
            cur += Math.pow(r.weight, bias);
            if (rand < cur)
                return r;
        }

        return COMMON;
    }

    /**
     * Pity Random RNG
     */

    private static final Map<UUID, Integer> FAIL_STACK = new HashMap<>();

    public static Rarity pityRandom(UUID player, List<Rarity> pool) {
        int fail = FAIL_STACK.getOrDefault(player, 0);

        double bonus = Math.min(20, fail * 0.5); // max +20%

        double roll = ThreadLocalRandom.current().nextDouble(100);

        double legendaryChance = 0.15 + bonus * 0.4;
        double mythicChance = 1.0 + bonus * 0.7;
        double specialChance = 3.0 + bonus * 1.5;
        double rareChance = 6.0 + bonus * 2.0;

        double cursor = 0;

        if (pool.contains(LEGENDARY)) {
            cursor += legendaryChance;
            if (roll < cursor)
                return reset(player, LEGENDARY);
        }

        if (pool.contains(MYTHIC)) {
            cursor += mythicChance;
            if (roll < cursor)
                return reset(player, MYTHIC);
        }

        if (pool.contains(SPECIAL)) {
            cursor += specialChance;
            if (roll < cursor)
                return reset(player, SPECIAL);
        }

        if (pool.contains(RARE)) {
            cursor += rareChance;
            if (roll < cursor)
                return reset(player, RARE);
        }

        FAIL_STACK.put(player, fail + 1);
        return random(pool);
    }

    private static Rarity reset(UUID player, Rarity r) {
        FAIL_STACK.put(player, 0);
        return r;
    }

    public static void removeFailStack(UUID player) {
        FAIL_STACK.remove(player);
    }
}
