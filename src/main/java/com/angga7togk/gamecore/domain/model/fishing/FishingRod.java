package com.angga7togk.gamecore.domain.model.fishing;

import com.angga7togk.gamecore.domain.enums.Rarity;
import com.angga7togk.gamecore.utils.Utils;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Data
@AllArgsConstructor
@Builder
public class FishingRod {

    public static final String NBT_TAG_NAME = "GameCoreFishingRod";
    public static final int DEFAULT_HOOK = 1;
    public static final double STARTING_MAX_WEIGHT = 5.0; // Beban awal pas level 1

    private final Item item;
    private int level;
    private int xp;
    private double maxWeight; // Sekarang disimpan di NBT karena nilainya random

    @Builder.Default
    private int hook = DEFAULT_HOOK;

    @Builder.Default
    private Map<Rarity, Integer> rarityStats = new HashMap<>();

    @Builder.Default
    private int totalCaught = 0;
    @Builder.Default
    private int premiumCaught = 0;

    @Builder.Default
    private double maxWeightCaught = 0.0;
    @Builder.Default
    private double maxLengthCaught = 0.0;

    public static FishingRod fromItem(Item item) {
        CompoundTag rootTag = item.getNamedTag();

        if (rootTag == null || !rootTag.contains(NBT_TAG_NAME)) {
            return FishingRod.builder()
                    .item(item)
                    .level(1)
                    .xp(0)
                    .maxWeight(STARTING_MAX_WEIGHT)
                    .build();
        }

        CompoundTag tag = rootTag.getCompound(NBT_TAG_NAME);

        Map<Rarity, Integer> stats = new HashMap<>();
        if (tag.contains("rarity_stats")) {
            CompoundTag rarityTag = tag.getCompound("rarity_stats");
            for (Rarity r : Rarity.values()) {
                if (rarityTag.contains(r.name())) {
                    stats.put(r, rarityTag.getInt(r.name()));
                }
            }
        }

        return FishingRod.builder()
                .item(item)
                .level(tag.getInt("level"))
                .xp(tag.getInt("xp"))
                .maxWeight(tag.getDouble("max_weight")) // Ambil nilai yang sudah tersimpan
                .hook(tag.getInt("hook"))
                .totalCaught(tag.getInt("total_caught"))
                .premiumCaught(tag.getInt("premium_caught"))
                .maxWeightCaught(tag.getDouble("max_weight_record"))
                .maxLengthCaught(tag.getDouble("max_length_record"))
                .rarityStats(stats)
                .build();
    }

    public int getNextLevelXp() {
        return (int) (Math.pow(this.level, 2) * 100);
    }

    /**
     * Tambah XP dan cek Level Up secara otomatis dengan bonus berat random
     */
    public void addXp(int amount) {
        this.xp += amount;
        while (this.xp >= getNextLevelXp()) {
            this.xp -= getNextLevelXp();
            this.level++;
            
            // RANDOM BONUS: Nambah beban maks antara 0.5kg sampai 2.5kg setiap level up
            double bonus = ThreadLocalRandom.current().nextDouble(0.5, 2.6);
            this.maxWeight += bonus;
        }
    }

    public void saveToItem() {
        CompoundTag nbt = item.getNamedTag() != null ? item.getNamedTag() : new CompoundTag();

        CompoundTag fishingTag = new CompoundTag()
                .putInt("level", this.level)
                .putInt("xp", this.xp)
                .putDouble("max_weight", this.maxWeight) // Simpan biar permanen
                .putInt("hook", this.hook)
                .putInt("total_caught", this.totalCaught)
                .putInt("premium_caught", this.premiumCaught)
                .putDouble("max_weight_record", this.maxWeightCaught)
                .putDouble("max_length_record", this.maxLengthCaught);

        CompoundTag rarityTag = new CompoundTag();
        this.rarityStats.forEach((rarity, count) -> rarityTag.putInt(rarity.name(), count));
        fishingTag.putCompound("rarity_stats", rarityTag);

        nbt.putCompound(NBT_TAG_NAME, fishingTag);

        List<String> lore = new ArrayList<>();
        lore.add("§r§bInformasi Joran:");
        lore.add("§r§7- Level: §a" + this.level + " §f(" + this.xp + "/" + getNextLevelXp() + " XP)");
        lore.add("§r§7- Beban Maks: §c" + Utils.formatWeight(this.maxWeight));
        lore.add("§r§7- Kail: §6" + this.hook + " Ikan");

        lore.add("");
        lore.add("§r§eRekor Terbaik:");
        lore.add("§r§7- Terberat: §f" + (this.maxWeightCaught > 0 ? Utils.formatWeight(this.maxWeightCaught) : "-"));
        lore.add("§r§7- Terpanjang: §f" + (this.maxLengthCaught > 0 ? Utils.formatLength(this.maxLengthCaught) : "-"));

        lore.add("");
        lore.add("§r§dStatistik Tangkapan:");
        for (Rarity r : Rarity.values()) {
            int count = rarityStats.getOrDefault(r, 0);
            if (count > 0)
                lore.add("§r§7- " + r.color() + r.name() + ": §f" + count);
        }

        if (this.premiumCaught > 0)
            lore.add("§r§7- §6Premium: §f" + this.premiumCaught);
        lore.add("§r§7- Total: §b" + this.totalCaught);

        item.setNamedTag(nbt);
        item.setLore(lore.toArray(new String[0]));
    }

    public void addStats(Fish fish){
        addStats(fish.getRarity(), fish.getWeight(), fish.getLength(), fish.isPremium());
    }

    public void addStats(Rarity rarity, double weight, double length, boolean isPremium) {
        this.totalCaught++;
        if (isPremium)
            this.premiumCaught++;

        if (weight > this.maxWeightCaught)
            this.maxWeightCaught = weight;
        if (length > this.maxLengthCaught)
            this.maxLengthCaught = length;

        int currentCount = this.rarityStats.getOrDefault(rarity, 0);
        this.rarityStats.put(rarity, currentCount + 1);

        // Berikan XP
        int xpGain = (rarity.ordinal() + 1) * 15; // Ditinggiin dikit biar gak kelamaan
        this.addXp(xpGain);
    }
}