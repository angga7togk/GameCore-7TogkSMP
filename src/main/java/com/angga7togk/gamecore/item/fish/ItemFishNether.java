package com.angga7togk.gamecore.item.fish;

import com.angga7togk.gamecore.item.BaseFishItem;

import cn.nukkit.level.Level;

public abstract class ItemFishNether extends BaseFishItem {

    public ItemFishNether(String id, String name, String texture) {
        super(id, name, texture);
    }

    @Override
    public int getDimension() {
        return Level.DIMENSION_NETHER;
    }

    // FISH CLASS

    public static class ItemFishAbyssalLurker extends ItemFishNether {

        public ItemFishAbyssalLurker() {
            super("gamecore:abyssal_lurker", "Abyssal Lurker", "abyssal_lurker");
        }

    }

    public static class ItemFishAnglerFish extends ItemFishNether {

        public ItemFishAnglerFish() {
            super("gamecore:angler_fish", "Angler Fish", "angler_fish");
        }

    }

    public static class ItemFishBlazePike extends ItemFishNether {

        public ItemFishBlazePike() {
            super("gamecore:blaze_pike", "Blaze Pike", "blaze_pike");
        }

    }

    public static class ItemFishChargedBullhead extends ItemFishNether {

        public ItemFishChargedBullhead() {
            super("gamecore:charged_bullhead", "Charged Bullhead", "charged_bullhead");
        }

    }

    public static class ItemFishCoalCrucian extends ItemFishNether {

        public ItemFishCoalCrucian() {
            super("gamecore:coal_crucian", "Coal Crucian", "coal_crucian");
        }

    }

    public static class ItemFishEnderShad extends ItemFishNether {

        public ItemFishEnderShad() {
            super("gamecore:ender_shad", "Ender Shad", "ender_shad");
        }

    }

    public static class ItemFishExplosiveCrucian extends ItemFishNether {

        public ItemFishExplosiveCrucian() {
            super("gamecore:explosive_crucian", "Explosive Crucian", "explosive_crucian");
        }

    }

    public static class ItemFishFungiCatfish extends ItemFishNether {

        public ItemFishFungiCatfish() {
            super("gamecore:fungi_catfish", "Fungi Catfish", "fungi_catfish");
        }

    }

    public static class ItemFishMagmaJellyfish extends ItemFishNether {

        public ItemFishMagmaJellyfish() {
            super("gamecore:magma_jellyfish", "Magma Jellyfish", "magma_jellyfish");
        }

    }

    public static class ItemFishMandarinfish extends ItemFishNether {

        public ItemFishMandarinfish() {
            super("gamecore:mandarinfish", "Mandarinfish", "mandarinfish");
        }

    }

    public static class ItemFishMudTuna extends ItemFishNether {

        public ItemFishMudTuna() {
            super("gamecore:mud_tuna", "Mud Tuna", "mud_tuna");
        }

    }

    public static class ItemFishNetherSturgeon extends ItemFishNether {

        public ItemFishNetherSturgeon() {
            super("gamecore:nether_sturgeon", "Nether Sturgeon", "nether_sturgeon");
        }

    }

    public static class ItemFishObsidianBream extends ItemFishNether {

        public ItemFishObsidianBream() {
            super("gamecore:obsidian_bream", "Obsidian Bream", "obsidian_bream");
        }

    }

    public static class ItemFishPearlSardine extends ItemFishNether {

        public ItemFishPearlSardine() {
            super("gamecore:pearl_sardine", "Pearl Sardine", "pearl_sardine");
        }

    }

    public static class ItemFishPike extends ItemFishNether {

        public ItemFishPike() {
            super("gamecore:pike", "Pike", "pike");
        }

    }

    public static class ItemFishQuartzChub extends ItemFishNether {

        public ItemFishQuartzChub() {
            super("gamecore:quartz_chub", "Quartz Chub", "quartz_chub");
        }

    }

    public static class ItemFishWitheredCrucian extends ItemFishNether {

        public ItemFishWitheredCrucian() {
            super("gamecore:withered_crucian", "Withered Crucian", "withered_crucian");
        }

    }

    public static class ItemFishMawsonia extends ItemFishNether {

        public ItemFishMawsonia() {
            super("gamecore:mawsonia", "Mawsonia", "mawsonia");
        }

    }

}
