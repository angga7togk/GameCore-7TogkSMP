package com.angga7togk.gamecore.item;

import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.level.Level;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;

public abstract class BaseFishItem extends ItemCustom {

    public BaseFishItem(String id, String name, String texture) {
        super(id, name, texture);
    }

    public int scaleOffset() {
        return 16; // 16 * 16
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, CreativeItemCategory.EQUIPMENT)
                .creativeGroup("itemGroup.name.rawFood")
                .allowOffHand(true)
                .handEquipped(true)
                .renderOffsets(RenderOffsets.scaleOffset(scaleOffset()))
                .build();
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    public int getDimension() {
        return Level.DIMENSION_OVERWORLD;
    }

}
