package com.angga7togk.gamecore.domain.model.fishing;

import java.util.List;

import com.angga7togk.gamecore.domain.enums.Rarity;

import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FishRegistryModel {

    private String namespaceId;
    private int dimension;
    private List<Rarity> rarities;

    public Item getItem() {
        return Item.fromString(namespaceId);
    }
    
    public Rarity getRarity(){
        return Rarity.random(rarities);
    }
    
}
