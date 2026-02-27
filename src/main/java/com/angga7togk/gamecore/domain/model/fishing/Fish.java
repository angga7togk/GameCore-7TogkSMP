package com.angga7togk.gamecore.domain.model.fishing;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.angga7togk.gamecore.domain.constant.EffectConstants;
import com.angga7togk.gamecore.domain.enums.Rarity;
import com.angga7togk.gamecore.utils.Utils;

import cn.nukkit.item.Item;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.potion.Effect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class Fish {

    public static final String NBT_TAG_NAME = "GameCoreFish";

    // Daftar Title khusus ikan Premium
    private static final String[] PREMIUM_TITLES = {
            "§6[RAJA LAUTAN]", "§b[PENJAGA PALUNG]", "§d[MAKHLUK MISTIS]", "§c[MONSTER PURBA]",
            "§e[CAHAYA SAMUDERA]", "§5[TERKUTUK]", "§a[PENGUASA ARUS]", "§f[LEGENDA HIDUP]",
            "§b[SPIRIT AIR]", "§6[HARTA KARUN]", "§c[SANG PREDATOR]", "§7[PENGHUNI GELAP]",
            "§e[DIBERKATI]", "§d[KOSMIK]", "§3[ATLANTIS]", "§g[EMAS MURNI]",
            "§b[OMBAK TENANG]", "§4[DARAH MERAH]", "§1[PENYELAM DALAM]", "§2[HUTAN BAKAU]"
    };

    private static final String[][] PREMIUM_QUOTES = {
            { "Ikan langka dengan kekuatan mistis,", "dan aura luar biasa. Nilainya tak ternilai!" },
            { "Makhluk kuno dari lautan terdalam,", "membawa keberuntungan besar. Legenda hidup!" },
            { "Energi kosmik mengalir di tubuhnya,", "menjadikannya sangat istimewa. Artefak lautan!" },
            { "Sisiknya berkilau seperti bintang jatuh,", "menyimpan rahasia peradaban yang telah sirna." },
            { "Dahulu dipuja sebagai penjaga samudera,", "kini terperangkap dalam jaring keberuntunganmu." },
            { "Hanya muncul saat bulan purnama merah,", "membawa pesan dari kegelapan palung terdalam." },
            { "Tubuhnya bergetar dengan frekuensi gaib,", "mampu menenangkan badai yang paling dahsyat." },
            { "Bukan sekadar ikan, melainkan perwujudan,", "dari mimpi buruk para pelaut zaman dahulu." },
            { "Ditempa oleh tekanan air yang luar biasa,", "menjadikannya sekeras baja dan seindah permata." },
            { "Tatapannya seolah menembus jiwamu,", "mengetahui setiap ambisi yang kau sembunyikan." }
    };

    private final Item item;
    private final Rarity rarity;
    private final double weight;
    private final double length;
    private final List<Effect> fishEffect;
    private final boolean premium;
    private final ParticleEffect particle;

    public static Fish fromItem(Item item) {
        if (item == null || item.isNull())
            return null;

        CompoundTag rootTag = item.getNamedTag();
        if (rootTag == null || !rootTag.contains(NBT_TAG_NAME))
            return null;

        CompoundTag tag = rootTag.getCompound(NBT_TAG_NAME);

        Rarity rarity = Rarity.valueOf(tag.getString("fish_rarity"));
        double weight = tag.getDouble("fish_weight");
        double length = tag.getDouble("fish_length");

        List<Effect> effects = new ArrayList<>();
        if (tag.contains("fish_effects")) {
            ListTag<CompoundTag> list = tag.getList("fish_effects", CompoundTag.class);
            for (CompoundTag e : list.getAll()) {
                effects.add(Effect.getEffect(e.getInt("id"))
                        .setAmplifier(e.getInt("amp"))
                        .setDuration(35));
            }
        }

        return Fish.builder()
                .item(item)
                .rarity(rarity)
                .weight(weight)
                .length(length)
                .fishEffect(effects)
                .particle(tag.contains("fish_particle") ? ParticleEffect.valueOf(tag.getString("fish_particle")) : null)
                .premium(tag.getBoolean("fish_premium"))
                .build();
    }

    public Item saveToItem() {
        CompoundTag rootTag = item.getNamedTag() != null ? item.getNamedTag() : new CompoundTag();
        CompoundTag fishTag = new CompoundTag();

        fishTag.putString("fish_rarity", rarity.name());
        fishTag.putDouble("fish_weight", weight);
        fishTag.putDouble("fish_length", length);
        fishTag.putBoolean("fish_premium", premium);
        if (particle != null)
            fishTag.putString("fish_particle", particle.name());

        if (fishEffect != null && !fishEffect.isEmpty()) {
            ListTag<CompoundTag> effectList = new ListTag<>("fish_effects");
            for (Effect effect : fishEffect) {
                effectList.add(new CompoundTag()
                        .putInt("id", effect.getId())
                        .putInt("amp", effect.getAmplifier()));
            }
            fishTag.putList(effectList);
        }

        rootTag.putCompound(NBT_TAG_NAME, fishTag);
        if (premium)
            rootTag.putList(new ListTag<CompoundTag>("ench"));

        item.setNamedTag(rootTag);

        // --- Lore Generation ---
        List<String> lore = new ArrayList<>();

        // BARIS TITLE: Hanya muncul jika PREMIUM = true
        if (premium) {
            String randomTitle = PREMIUM_TITLES[ThreadLocalRandom.current().nextInt(PREMIUM_TITLES.length)];
            lore.add("§r" + randomTitle);
        }

        lore.add("§r§7- Kelangkaan: " + rarity.color() + rarity.name());
        lore.add("§r§7- Berat: §f" + Utils.formatWeight(this.weight));
        lore.add("§r§7- Panjang: §f" + Utils.formatLength(this.length));

        if (fishEffect != null && !fishEffect.isEmpty()) {
            lore.add("");
            lore.add("§r§eEfek Khusus:");
            for (Effect e : fishEffect) {
                String effectName = EffectConstants.EFFECT_NAME_MAP.getOrDefault(e.getId(), "Unknown");
                lore.add("§r§7- " + Utils.toHumanReadable(effectName) + " " + (e.getAmplifier() + 1));
            }
        }

        if (particle != null) {
            lore.add("§r§7- Aura: §d" + Utils.toHumanReadable(particle.name()));
        }

        // QUOTE: Hanya muncul jika PREMIUM = true
        if (premium) {
            lore.add("");
            String[] quote = PREMIUM_QUOTES[ThreadLocalRandom.current().nextInt(PREMIUM_QUOTES.length)];
            for (String line : quote) {
                lore.add("§r§7" + line);
            }
        }

        item.setLore(lore.toArray(new String[0]));
        return item;
    }
}