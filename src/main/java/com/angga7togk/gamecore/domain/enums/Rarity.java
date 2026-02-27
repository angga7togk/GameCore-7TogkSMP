package com.angga7togk.gamecore.domain.enums;

import java.util.List;
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
}
