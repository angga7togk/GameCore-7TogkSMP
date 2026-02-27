package com.angga7togk.gamecore.domain.constant;

import cn.nukkit.level.ParticleEffect;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleConstants {

        // Kumpulan particle pilihan untuk ikan kasta tertinggi (Legendary & Unknown)
        private static final List<ParticleEffect> PREMIUM_PARTICLES = List.of(
                        ParticleEffect.BLUE_FLAME,
                        ParticleEffect.SOUL,
                        ParticleEffect.ENDROD,
                        ParticleEffect.OBSIDIAN_GLOW_DUST,
                        ParticleEffect.SPARKLER,
                        ParticleEffect.CRITICAL_HIT,
                        ParticleEffect.GLOW,
                        ParticleEffect.DRAGON_BREATH_TRAIL,
                        ParticleEffect.DRAGON_BREATH_FIRE,
                        ParticleEffect.TOTEM,
                        ParticleEffect.CONDUIT,
                        ParticleEffect.ENCHANTING_TABLE_PARTICLE,
                        ParticleEffect.HEART,
                        ParticleEffect.VILLAGER_HAPPY);

        /**
         * Mengambil 1 particle secara acak dari list premium.
         */
        public static ParticleEffect getRandomPremiumEffect() {
                return PREMIUM_PARTICLES.get(
                                ThreadLocalRandom.current().nextInt(PREMIUM_PARTICLES.size()));
        }
}