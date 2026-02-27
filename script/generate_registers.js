const fs = require("fs");
const path = require("path");

const listFile = "script/fish_list.txt";
const outFile = "FishRegistry.java";

const lines = fs.readFileSync(listFile, "utf8")
    .split("\n")
    .map(e => e.trim())
    .filter(e => e && !e.startsWith("#"));

function toClassName(name) {
    return name.split("_")
        .map(w => w.charAt(0).toUpperCase() + w.slice(1))
        .join("");
}

let content = `package com.angga7togk.gamecore.item.fish;

import java.util.ArrayList;
import java.util.List;

import com.angga7togk.gamecore.domain.model.FishRegistryModel;
import com.angga7togk.gamecore.domain.types.Rarity;
import com.angga7togk.gamecore.item.fish.ItemFishNether.*;
import com.angga7togk.gamecore.item.fish.ItemFishOverworld.*;

import cn.nukkit.item.Item;
import cn.nukkit.item.customitem.CustomItem;

public class FishRegistry {

    public static final List<FishRegistryModel> FISH_MAP = new ArrayList<>();

    public static void registerAll() {
`;

for (const line of lines) {
    let clean = line;

    const rarityMatch = clean.match(/<([^>]+)>/);
    const rarities = rarityMatch
        ? rarityMatch[1].split(",").map(r => `Rarity.${r.trim().toUpperCase()}`)
        : ["Rarity.COMMON"];

    clean = clean.replace(/<[^>]+>/, "");
    clean = clean.replace("%nether%", "");

    const file = path.basename(clean, ".png");
    const className = "ItemFish" + toClassName(file);

    content += `        register(${className}.class, ${rarities.join(", ")});\n`;
}

content += `    }

    private static void register(Class<? extends CustomItem> clazz, Rarity... rarities) {
        try {
            Item.registerCustomItem(clazz, true);

            CustomItem item = clazz.getDeclaredConstructor().newInstance();
            FISH_MAP.add(new FishRegistryModel(item.getNamespaceId(), List.of(rarities)));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
`;

fs.writeFileSync(outFile, content);
console.log("Generated:", outFile);