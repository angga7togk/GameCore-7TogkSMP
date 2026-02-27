package com.angga7togk.gamecore.domain.model;

import java.util.Map;

import cn.nukkit.item.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FishingCollection {

    private String owner;
    private Long score;
    private Map<Integer, Item> contents;
}
