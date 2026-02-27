package com.angga7togk.fishingcontest.item.fishingrod;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;

public class ItemBlazingFishingRod extends ItemCustom {
    public ItemBlazingFishingRod() {
        super("nukkit:blazing_fishing_rod", "Blazing Fishing Rod", "blazing_fishing_rod");
    }

    public int scaleOffset() {
        return 16; // 16 * 16
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, CreativeItemCategory.EQUIPMENT)
                .renderOffsets(RenderOffsets.scaleOffset(scaleOffset()))
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public int getEnchantAbility() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx,
            double fy, double fz) {
        return this.onClickAir(player, player.getDirectionVector());
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.fishing != null) {
            if (!this.isUnbreakable()) {
                if (player.fishing.getTarget() > 0) {
                    this.meta = this.meta + 2;
                } else {
                    this.meta++;
                }
            }
            player.stopFishing(true);
        } else {
            player.startFishing(this);
        }
        return true;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FISHING_ROD;
    }
    
    @Override
    public boolean isTool() {
        return true;
    }
}
