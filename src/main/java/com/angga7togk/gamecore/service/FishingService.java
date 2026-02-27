package com.angga7togk.gamecore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.angga7togk.gamecore.domain.constant.EffectConstants;
import com.angga7togk.gamecore.domain.constant.ParticleConstants;
import com.angga7togk.gamecore.domain.enums.Rarity;
import com.angga7togk.gamecore.domain.model.fishing.Fish;
import com.angga7togk.gamecore.domain.model.fishing.FishRegistryModel;
import com.angga7togk.gamecore.domain.model.fishing.FishingRod;
import com.angga7togk.gamecore.item.FishRegistry;
import com.angga7togk.gamecore.utils.Utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;

public class FishingService {

    public static Fish chaugtFish(Player player, Rarity forcedRarity, boolean forcePremium) {
        Item itemInHand = player.getInventory().getItemInHand();
        FishingRod rod = FishingRod.fromItem(itemInHand);

        // Jika player mancing gak pake joran sistem kita (joran biasa)
        if (rod == null) {
            player.sendMessage("§c[!] Joran kamu terlalu lemah untuk menarik ikan di sini!");
            return null;
        }

        FishRegistryModel model = FishRegistry.getRandom(player.getUniqueId(), player.getLevel().getDimension());
        if (model == null)
            return null;

        Item fishItem = model.getItem().clone();
        Rarity rarity = (forcedRarity != null) ? forcedRarity : model.getRarity();

        // --- GENERATE DATA IKAN ---

        // Base weight & length tetap
        double baseWeight = randomDouble(0.5, 8 + rarity.ordinal() * 4);
        double baseLength = randomDouble(10, 40 + rarity.ordinal() * 25);

        // Multiplier System (Weight & Length)
        double weightMultiplier;
        double lengthMultiplier;

        // --- ESTIMASI HASIL GENERATE IKAN ---

        switch (rarity.name()) {
            case "UNKNOWN":
                weightMultiplier = 10.0;
                lengthMultiplier = 5.0;
                // Estimasi: 250kg - 350kg | Panjang: 5 - 10 Meter
                // Status: MONSTER (Joran level rendah pasti putus total)
                break;

            case "LEGENDARY":
                weightMultiplier = 5.0;
                lengthMultiplier = 3.0;
                // Estimasi: 100kg - 160kg | Panjang: 3 - 5 Meter
                // Status: RAKSASA (Butuh joran level tinggi)
                break;

            case "MYTHIC":
                weightMultiplier = 3.0;
                lengthMultiplier = 2.0;
                // Estimasi: 60kg - 90kg | Panjang: 2 - 3.5 Meter
                // Status: SANGAT BESAR (Joran Mid-level mulai kewalahan)
                break;

            case "SPECIAL":
                weightMultiplier = 2.5;
                lengthMultiplier = 1.8;
                // Estimasi: 40kg - 65kg | Panjang: 1.5 - 2.8 Meter
                // Status: BESAR (Perlu hati-hati)
                break;

            case "RARE":
                weightMultiplier = 2.0;
                lengthMultiplier = 1.5;
                // Estimasi: 25kg - 45kg | Panjang: 1 - 2 Meter
                // Status: MENENGAH (Joran awal-awal mulai sering putus di sini)
                break;

            default: // COMMON & UNCOMMON
                weightMultiplier = 1.0;
                lengthMultiplier = 1.0;
                // Estimasi: 0.5kg - 15kg | Panjang: 10cm - 80cm
                // Status: NORMAL (Aman untuk semua joran)
                break;
        }

        // Final Calculation
        double weight = (baseWeight * weightMultiplier) + (rod.getLevel() * 0.2);
        double length = (baseLength * lengthMultiplier) + (rod.getLevel() * 0.5);

        // --- VALIDASI BERAT (MAX WEIGHT CHECK) ---
        if (weight > rod.getMaxWeight()) {
            player.sendMessage("§c[!] Tali pancing putus! Ikan seberat §f" + Utils.formatWeight(weight) + " §clepas!");
            player.sendMessage("§7(Beban maks joran kamu: §f" + Utils.formatWeight(rod.getMaxWeight()) + "§7)");

            // --- EFEK SUARA GAGAL ---
            // 1. Suara Tali Putus (String Break / Bow Hit)
            player.getLevel().addSound(player, Sound.RANDOM_BOWHIT, 1.0f, 0.5f);

            // 2. Tambahan suara dentuman kecil (Anvil) biar kerasa beban beratnya
            player.getLevel().addSound(player, Sound.RANDOM_ANVIL_LAND, 0.6f, 1.2f);

            // Kasih XP "Pelajaran" biar joran tetep berkembang meski gagal
            rod.addXp(5);
            rod.saveToItem();
            return null;
        }

        // --- PROSES LANJUTAN (EFEK & PREMIUM) ---
        List<Effect> effects = new ArrayList<>();
        if (fishEffectChance(rarity, rod.getLevel())) {
            effects.add(EffectConstants.getRandomEffect(rarity)
                    .setAmplifier(amplifierFromRarity(rarity))
                    .setDuration(20 * 15));
        }

        boolean premium = forcePremium;
        if (!premium && (rarity == Rarity.LEGENDARY || rarity.name().equalsIgnoreCase("UNKNOWN"))) {
            double basePremiumChance = rarity.name().equalsIgnoreCase("UNKNOWN") ? 5.0 : 1.0;
            premium = Utils.chance(basePremiumChance + (rod.getLevel() * 0.1));
        }

        ParticleEffect particle = null;
        if (premium) {
            ParticleEffect pe = ParticleConstants.getRandomPremiumEffect();
            if (pe != null)
                particle = pe;
        }

        // --- FINALIZE FISH ---
        fishItem.setCustomName("§l" + rarity.color() + fishItem.getName());
        Fish fish = Fish.builder()
                .item(fishItem)
                .rarity(rarity)
                .weight(weight)
                .length(length)
                .fishEffect(effects)
                .premium(premium)
                .particle(particle)
                .build();

        fish.saveToItem();

        // --- UPDATE STATS JORAN ---
        rod.addStats(fish);
        rod.saveToItem();

        return fish;
    }

    private static int amplifierFromRarity(Rarity rarity) {
        return switch (rarity) {
            case COMMON, UNCOMMON -> 0;
            case RARE, SPECIAL -> 1;
            case MYTHIC, LEGENDARY -> 2;
            default -> rarity.name().equalsIgnoreCase("UNKNOWN") ? 3 : 0; // UNKNOWN dapet Amp 3 (Level 4)
        };
    }

    private static boolean fishEffectChance(Rarity r, int rodLevel) {
        if (r.name().equalsIgnoreCase("UNKNOWN"))
            return true; // Unknown pasti dapet efek
        double baseChance = switch (r) {
            case COMMON -> 10;
            case RARE, MYTHIC, SPECIAL -> 40;
            case LEGENDARY -> 60;
            default -> 20;
        };
        return Utils.chance(baseChance + (rodLevel * 0.5));
    }

    private static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    /* -------------------------------------------------------------------------- */
    /* RARITY PITY SYSTEM CLASS */
    /* -------------------------------------------------------------------------- */

    public static class RarityPityFishing {
        private static final Map<UUID, Integer> FAIL_STACK = new HashMap<>();

        public static Rarity pityRandom(UUID player, List<Rarity> pool) {
            int fail = FAIL_STACK.getOrDefault(player, 0);
            double bonus = Math.min(20, fail * 0.5); // max +20% bonus luck
            double roll = ThreadLocalRandom.current().nextDouble(100);

            // CHANCE CONFIG (Bisa lu sesuaikan)
            double unknownChance = 0.01 + (bonus * 0.05); // Super Rare!
            double legendaryChance = 0.15 + (bonus * 0.4);
            double mythicChance = 1.0 + (bonus * 0.7);
            double specialChance = 3.0 + (bonus * 1.5);
            double rareChance = 6.0 + (bonus * 2.0);

            double cursor = 0;

            // 1. Roll UNKNOWN
            if (pool.stream().anyMatch(r -> r.name().equalsIgnoreCase("UNKNOWN"))) {
                cursor += unknownChance;
                if (roll < cursor)
                    return reset(player, findRarity(pool, "UNKNOWN"));
            }

            // 2. Roll LEGENDARY
            if (pool.contains(Rarity.LEGENDARY)) {
                cursor += legendaryChance;
                if (roll < cursor)
                    return reset(player, Rarity.LEGENDARY);
            }

            // 3. Roll MYTHIC
            if (pool.contains(Rarity.MYTHIC)) {
                cursor += mythicChance;
                if (roll < cursor)
                    return reset(player, Rarity.MYTHIC);
            }

            // 4. Roll SPECIAL
            if (pool.contains(Rarity.SPECIAL)) {
                cursor += specialChance;
                if (roll < cursor)
                    return reset(player, Rarity.SPECIAL);
            }

            // 5. Roll RARE
            if (pool.contains(Rarity.RARE)) {
                cursor += rareChance;
                if (roll < cursor)
                    return reset(player, Rarity.RARE);
            }

            FAIL_STACK.put(player, fail + 1);
            return Rarity.random(pool);
        }

        private static Rarity findRarity(List<Rarity> pool, String name) {
            return pool.stream().filter(r -> r.name().equalsIgnoreCase(name)).findFirst().orElse(Rarity.COMMON);
        }

        private static Rarity reset(UUID player, Rarity r) {
            FAIL_STACK.put(player, 0);
            return r;
        }

        public static void removeFailStack(UUID player) {
            FAIL_STACK.remove(player);
        }
    }
}