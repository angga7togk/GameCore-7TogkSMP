package com.angga7togk.gamecore.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.angga7togk.gamecore.domain.enums.Rarity;
import com.angga7togk.gamecore.domain.model.fishing.FishRegistryModel;
import com.angga7togk.gamecore.item.fish.ItemFishNether.*;
import com.angga7togk.gamecore.item.fish.ItemFishOverworld.*;
import com.angga7togk.gamecore.service.FishingService;

import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItem;
import cn.nukkit.level.DimensionEnum;

public class FishRegistry {

    // dimensiId :
    public static final Map<Integer, List<FishRegistryModel>> FISH_MAP = new HashMap<>();

    static {
        for (DimensionEnum dim : DimensionEnum.values()) {
            FISH_MAP.put(dim.getDimensionData().getDimensionId(), new ArrayList<>());
        }
    }

    public static FishRegistryModel getRandom(UUID player, int dimensiId) {
        List<FishRegistryModel> pool = FISH_MAP.get(dimensiId);
        if (pool == null || pool.isEmpty())
            return null;

        List<Rarity> rarityPool = pool.stream()
                .flatMap(f -> f.getRarities().stream())
                .distinct()
                .toList();

        Rarity rolled = FishingService.RarityPityFishing.pityRandom(player, rarityPool);

        List<FishRegistryModel> filtered = pool.stream()
                .filter(f -> f.getRarities().contains(rolled))
                .toList();

        if (filtered.isEmpty())
            return randomFish(pool);

        return filtered.get(ThreadLocalRandom.current().nextInt(filtered.size()));
    }

    private static FishRegistryModel randomFish(List<FishRegistryModel> pool) {
        return pool.get(ThreadLocalRandom.current().nextInt(pool.size()));
    }

    public static void registerAll() {
        register(ItemFishFishRaw.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishFishSalmonRaw.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishFishClownfishRaw.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishFishPufferfishRaw.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishAbyssalLurker.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishAmethystCatfish.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishAngelfish.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishAnglerFish.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishBlazePike.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishBlueJellyfish.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishBoneFish.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishBrownShroomfin.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishCatfish.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishCaveTrout.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishChargedBullhead.class, Rarity.SPECIAL, Rarity.MYTHIC, Rarity.LEGENDARY);
        register(ItemFishChorusKoi.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishCoalCrucian.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishCopperMinnow.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishCrystalMullet.class, Rarity.UNCOMMON, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishCursedKoi.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishDiamondKoi.class, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishEmeraldSalmon.class, Rarity.UNCOMMON, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishEnderShad.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishExplosiveCrucian.class, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishFlarefinKoi.class, Rarity.SPECIAL, Rarity.MYTHIC, Rarity.LEGENDARY);
        register(ItemFishFrostMinnow.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishFungiCatfish.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishGlacierAnchovy.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishGoldenKoi.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC, Rarity.LEGENDARY);
        register(ItemFishGreenJellyfish.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishIronBass.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishMagikarp.class, Rarity.COMMON, Rarity.LEGENDARY);
        register(ItemFishMagmaJellyfish.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishMandarinfish.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishMudTuna.class, Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishNetherSturgeon.class, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishObsidianBream.class, Rarity.SPECIAL, Rarity.MYTHIC, Rarity.LEGENDARY);
        register(ItemFishPearlSardine.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishPike.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishPiranha.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishQuartzChub.class, Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishRedShroomfin.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishRuffe.class, Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishSandyBass.class, Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishSnowyWalleye.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishSparklingEel.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishSpecularSnapper.class, Rarity.RARE, Rarity.SPECIAL);
        register(ItemFishSpongeEater.class, Rarity.UNCOMMON, Rarity.RARE);
        register(ItemFishSpookyfin.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishSquid.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishSunfish.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishSwampPlaice.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishWitheredCrucian.class, Rarity.RARE, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishCoelacanth.class, Rarity.MYTHIC, Rarity.LEGENDARY);
        register(ItemFishDiplurus.class, Rarity.MYTHIC, Rarity.LEGENDARY);
        register(ItemFishMawsonia.class, Rarity.SPECIAL, Rarity.MYTHIC);
        register(ItemFishPalaeoniscus.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishRedKoi.class, Rarity.COMMON, Rarity.UNCOMMON);
        register(ItemFishYellowKoi.class, Rarity.COMMON, Rarity.UNCOMMON);
    }

    private static void register(Class<? extends CustomItem> clazz, Rarity... rarities) {
        try {
            Item.registerCustomItem(clazz, true);

            CustomItem item = clazz.getDeclaredConstructor().newInstance();
            if (item instanceof BaseFishItem i) {
                FISH_MAP
                        .get(i.getDimension())
                        .add(new FishRegistryModel(
                                item.getNamespaceId(),
                                i.getDimension(),
                                List.of(rarities)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
