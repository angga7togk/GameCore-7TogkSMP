package com.angga7togk.gamecore.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.angga7togk.gamecore.domain.constant.EffectConstants;
import com.angga7togk.gamecore.domain.constant.ParticleConstants;
import com.angga7togk.gamecore.domain.model.Fish;
import com.angga7togk.gamecore.domain.model.FishRegistryModel;
import com.angga7togk.gamecore.domain.types.Rarity;
import com.angga7togk.gamecore.item.FishRegistry;
import com.angga7togk.gamecore.utils.Utils;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;

public class FishingService {

    private static final String[] TITLES = {
            "§6-= BLESSED =-",
            "§d-= DIVINE =-",
            "§c-= GODLIKE =-",
            "§b-= CELESTIAL =-"
    };

    private static final String[][] PREMIUM_QUOTES = {
            {
                    "§5Ikan langka dengan kekuatan mistis",
                    "§5dan aura luar biasa.",
                    "§5Nilainya tak ternilai!"
            },
            {
                    "§6Makhluk kuno dari lautan terdalam",
                    "§6membawa keberuntungan besar.",
                    "§6Legenda hidup!"
            },
            {
                    "§bEnergi kosmik mengalir di tubuhnya",
                    "§bmenjadikannya sangat istimewa.",
                    "§bArtefak lautan!"
            }
    };

    public static Fish buildFishItem(Player player, Rarity rarity, boolean forcePremium) {
        FishRegistryModel model = FishRegistry.getRandom(
                player.getUniqueId(),
                player.getLevel().getDimension());

        Item item = model.getItem().clone();
        if (item.isNull())
            return null;

        if (rarity == null) {
            rarity = forcePremium
                    ? Rarity.random(Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC, Rarity.LEGENDARY)
                    : Rarity.random();
        }

        double weight = randomDouble(0.5, 8 + rarity.ordinal() * 4);
        double length = randomDouble(10, 40 + rarity.ordinal() * 25);

        List<Effect> effects = new ArrayList<>();
        if (fishEffectChance(rarity)) {
            effects.add(EffectConstants.getRandomEffect(rarity)
                    .setAmplifier(ThreadLocalRandom.current().nextInt(1, rarity.ordinal() + 2))
                    .setDuration(20 * 15));
        }

        boolean premium = forcePremium || premiumTagChance(rarity);

        String title = null;
        String particle = null;

        if (premium) {
            setGlowOnly(item);

            title = TITLES[ThreadLocalRandom.current().nextInt(TITLES.length)];

            ParticleEffect pe = ParticleConstants.getRandomEffect(rarity);
            if (pe != null)
                particle = pe.name();

            item.setCustomName("§l" + title.replace("-= ", "[").replace(" =-", "]")
                    + "§f " + rarity.color() + item.getName());
        } else {
            item.setCustomName("§l" + rarity.color() + item.getName());
        }

        updateLore(item, rarity, weight, length, effects, title, premium, particle);
        writeNBT(item, rarity, weight, length, effects, title, premium, particle);

        return new Fish(item, rarity, weight, length, effects, title, premium, particle);
    }

    // ===================== NBT =====================

    private static void writeNBT(Item item, Rarity rarity, double weight, double length,
            List<Effect> effects, String title, boolean premium, String particle) {

        CompoundTag tag = item.hasCompoundTag()
                ? item.getNamedTag()
                : new CompoundTag();

        tag.putBoolean("custom_fish_v2", true);
        tag.putString("fish_rarity", rarity.name());
        tag.putDouble("fish_weight", weight);
        tag.putDouble("fish_length", length);
        tag.putBoolean("fish_premium", premium);

        if (title != null)
            tag.putString("fish_title", title);

        if (particle != null)
            tag.putString("fish_particle", particle);

        if (!effects.isEmpty()) {
            ListTag<CompoundTag> list = new ListTag<>("fish_effects");

            for (Effect effect : effects) {
                CompoundTag e = new CompoundTag()
                        .putInt("id", effect.getId())
                        .putInt("amp", effect.getAmplifier());

                list.add(e);
            }

            tag.putList(list);
        }

        item.setNamedTag(tag);
    }

    // ===================== LORE =====================

    public static void updateLore(Item item, Rarity rarity, double weight, double length,
            List<Effect> effects, String title, boolean premium, String particle) {

        List<String> lore = new ArrayList<>();

        if (title != null) {
            lore.add(title);
            lore.add("");
        }

        lore.add("§7Rarity: " + rarity.color() + rarity.displayName());
        lore.add("§7Weight: §e" + String.format("%.2f", weight) + " kg");
        lore.add("§7Length: §b" + String.format("%.1f", length) + " cm");

        if (!effects.isEmpty()) {
            lore.add("");
            lore.add("§7Effect+:");

            for (Effect e : effects) {
                lore.add(" §d- " + Utils.toHumanReadable(e.getName())
                        + " " + Enchantment.getLevelString(e.getAmplifier() + 1));
            }
        }

        if (particle != null) {
            lore.add("");
            lore.add("§7Particle+: §b" + Utils.toHumanReadable(particle));
        }

        if (premium) {
            lore.add("");
            lore.add("§d-= PREMIUM FISH =-");
            lore.addAll(randomPremiumText());
        }

        item.setLore(lore.toArray(new String[0]));
    }

    // ===================== UTILS =====================

    private static List<String> randomPremiumText() {
        return Arrays.asList(
                PREMIUM_QUOTES[ThreadLocalRandom.current().nextInt(PREMIUM_QUOTES.length)]);
    }

    private static boolean premiumTagChance(Rarity r) {
        return switch (r) {
            case LEGENDARY, MYTHIC -> Utils.chance(0.5);
            default -> false;
        };
    }

    private static boolean fishEffectChance(Rarity r) {
        return switch (r) {
            case RARE, MYTHIC, SPECIAL -> Utils.chance(50);
            case LEGENDARY -> Utils.chance(70);
            default -> Utils.chance(30);
        };
    }

    private static void setGlowOnly(Item item) {
        CompoundTag nbt = item.hasCompoundTag()
                ? item.getNamedTag()
                : new CompoundTag();

        nbt.putList(new ListTag<CompoundTag>("ench"));
        item.setNamedTag(nbt);
    }

    private static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }
}