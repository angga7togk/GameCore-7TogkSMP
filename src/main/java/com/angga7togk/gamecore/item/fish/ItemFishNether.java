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

    public class ItemFishAbyssalLurker extends ItemFishNether {

        public ItemFishAbyssalLurker() {
            super("nukkit:abyssal_lurker", "Abyssal Lurker", "abyssal_lurker");
        }

    }

    public class ItemFishAnglerFish extends ItemFishNether {

        public ItemFishAnglerFish() {
            super("nukkit:angler_fish", "Angler Fish", "angler_fish");
        }

    }

    public class ItemFishBlazePike extends ItemFishNether {

        public ItemFishBlazePike() {
            super("nukkit:blaze_pike", "Blaze Pike", "blaze_pike");
        }

    }

    public class ItemFishChargedBullhead extends ItemFishNether {

        public ItemFishChargedBullhead() {
            super("nukkit:charged_bullhead", "Charged Bullhead", "charged_bullhead");
        }

    }

    public class ItemFishCoalCrucian extends ItemFishNether {

        public ItemFishCoalCrucian() {
            super("nukkit:coal_crucian", "Coal Crucian", "coal_crucian");
        }

    }

    public class ItemFishEnderShad extends ItemFishNether {

        public ItemFishEnderShad() {
            super("nukkit:ender_shad", "Ender Shad", "ender_shad");
        }

    }

    public class ItemFishExplosiveCrucian extends ItemFishNether {

        public ItemFishExplosiveCrucian() {
            super("nukkit:explosive_crucian", "Explosive Crucian", "explosive_crucian");
        }

    }

    public class ItemFishFungiCatfish extends ItemFishNether {

        public ItemFishFungiCatfish() {
            super("nukkit:fungi_catfish", "Fungi Catfish", "fungi_catfish");
        }

    }

    public class ItemFishMagmaJellyfish extends ItemFishNether {

        public ItemFishMagmaJellyfish() {
            super("nukkit:magma_jellyfish", "Magma Jellyfish", "magma_jellyfish");
        }

    }

    public class ItemFishMandarinfish extends ItemFishNether {

        public ItemFishMandarinfish() {
            super("nukkit:mandarinfish", "Mandarinfish", "mandarinfish");
        }

    }

    public class ItemFishMudTuna extends ItemFishNether {

        public ItemFishMudTuna() {
            super("nukkit:mud_tuna", "Mud Tuna", "mud_tuna");
        }

    }

    public class ItemFishNetherSturgeon extends ItemFishNether {

        public ItemFishNetherSturgeon() {
            super("nukkit:nether_sturgeon", "Nether Sturgeon", "nether_sturgeon");
        }

    }

    public class ItemFishObsidianBream extends ItemFishNether {

        public ItemFishObsidianBream() {
            super("nukkit:obsidian_bream", "Obsidian Bream", "obsidian_bream");
        }

    }

    public class ItemFishPearlSardine extends ItemFishNether {

        public ItemFishPearlSardine() {
            super("nukkit:pearl_sardine", "Pearl Sardine", "pearl_sardine");
        }

    }

    public class ItemFishPike extends ItemFishNether {

        public ItemFishPike() {
            super("nukkit:pike", "Pike", "pike");
        }

    }

    public class ItemFishQuartzChub extends ItemFishNether {

        public ItemFishQuartzChub() {
            super("nukkit:quartz_chub", "Quartz Chub", "quartz_chub");
        }

    }

    public class ItemFishWitheredCrucian extends ItemFishNether {

        public ItemFishWitheredCrucian() {
            super("nukkit:withered_crucian", "Withered Crucian", "withered_crucian");
        }

    }

    public class ItemFishMawsonia extends ItemFishNether {

        public ItemFishMawsonia() {
            super("nukkit:mawsonia", "Mawsonia", "mawsonia");
        }

    }

}
