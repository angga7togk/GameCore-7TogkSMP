package com.angga7togk.gamecore.domain.model;

import java.util.ArrayList;
import java.util.List;

import com.angga7togk.gamecore.domain.types.Rarity;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Fish {

    private final Item item;
    private final Rarity rarity;
    private final double weight;
    private final double length;

    private final List<Effect> fishEffect;

    private final String title;
    private final boolean premium;
    private final String particle;

    public static Fish fromItem(Item item) {
        if (item == null || item.isNull())
            return null;

        CompoundTag tag = item.getNamedTag();
        if (tag == null)
            return null;

        if (!tag.getBoolean("custom_fish_v2"))
            return null;

        Rarity rarity = Rarity.valueOf(tag.getString("fish_rarity"));
        double weight = tag.getDouble("fish_weight");
        double length = tag.getDouble("fish_length");

        List<Effect> effects = new ArrayList<>();

        if (tag.contains("fish_effects")) {
            ListTag<CompoundTag> list = tag.getList("fish_effects", CompoundTag.class);

            for (CompoundTag e : list.getAll()) {
                effects.add(
                        Effect.getEffect(e.getInt("id"))
                                .setAmplifier(e.getInt("amp"))
                                .setDuration(20 + 15));
            }
        }

        String title = null;
        if (tag.contains("fish_title")) {
            title = tag.getString("fish_title");
        }

        String particle = null;
        if (tag.contains("fish_particle")) {
            particle = tag.getString("fish_particle");
        }

        boolean premium = tag.getBoolean("fish_premium");

        return new Fish(item, rarity, weight, length, effects, title, premium, particle);
    }

    /**
     * Menghitung harga jual ikan (Money/Economy)
     */
    public long calculatePrice() {
        // Ambil harga dasar dari rarity
        long basePrice = rarity.price();

        // Hitung bonus ukuran dengan double dulu agar presisi, baru cast ke long
        double sizeBonus = (this.weight * 0.8) + (this.length * 0.4);

        // Tentukan multiplier premium
        double premiumMultiplier = premium ? 2.0 : 1.0;

        // Total akhir di-cast ke long
        return (long) ((basePrice + sizeBonus) * premiumMultiplier);
    }

    /**
     * Menghitung skor untuk keperluan Leaderboard/Contest
     */
    public long calculateScore() {
        // Rarity multiplier (Ordinal dimulai dari 0, jadi +1 agar Common tidak 0)
        long rarityMultiplier = (long) (rarity.ordinal() + 1) * 100L;

        // Hitung skor fisik dengan double
        double physicalScore = (this.weight * 1.5) + (this.length * 1.2);

        // Bonus Premium
        long premiumBonus = premium ? 500L : 0L;

        // Total skor di-cast ke long
        return rarityMultiplier + (long) physicalScore + premiumBonus;
    }

}
