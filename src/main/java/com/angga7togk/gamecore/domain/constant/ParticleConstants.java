package com.angga7togk.gamecore.domain.constant;

import cn.nukkit.level.ParticleEffect;
import com.angga7togk.gamecore.domain.types.Rarity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleConstants {

    private static final Map<Rarity, List<ParticleEffect>> RARITY_EFFECTS = Map.of(
            Rarity.COMMON, List.of(
                    ParticleEffect.BASIC_SMOKE,
                    ParticleEffect.SNOWFLAKE),

            Rarity.UNCOMMON, List.of(
                    ParticleEffect.VILLAGER_HAPPY,
                    ParticleEffect.NOTE),

            Rarity.RARE, List.of(
                    ParticleEffect.ENCHANTING_TABLE_PARTICLE,
                    ParticleEffect.GLOW),

            Rarity.SPECIAL, List.of(
                    ParticleEffect.BLUE_FLAME,
                    ParticleEffect.SOUL),

            Rarity.MYTHIC, List.of(
                    ParticleEffect.ENDROD,
                    ParticleEffect.OBSIDIAN_GLOW_DUST,
                    ParticleEffect.SPARKLER),

            Rarity.LEGENDARY, List.of(
                    ParticleEffect.SPARKLER,
                    ParticleEffect.CRITICAL_HIT,
                    ParticleEffect.GLOW));

    public static ParticleEffect getRandomEffect(Rarity rarity) {
        List<ParticleEffect> list = RARITY_EFFECTS.get(rarity);

        if (list == null || list.isEmpty()) {
            return ParticleEffect.HEART;
        }

        return list.get(
                ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static ParticleEffect getWeightedRandomEffect() {
        Rarity rarity = Rarity.rollRarityWeighted();
        if (rarity.equals(Rarity.UNCOMMON))
            return getRandomEffect(Rarity.COMMON);

        return getRandomEffect(rarity);
    }
}