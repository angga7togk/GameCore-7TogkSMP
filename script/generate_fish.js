const fs = require("fs");
const path = require("path");

const listFile = "script/fish_list.txt";
const outDir = "./generated";

if (!fs.existsSync(outDir)) fs.mkdirSync(outDir);

const lines = fs.readFileSync(listFile, "utf8")
    .split("\n")
    .map(e => e.trim())
    .filter(e => e && !e.startsWith("#"));

function toClassName(name) {
    return name.split("_")
        .map(w => w.charAt(0).toUpperCase() + w.slice(1))
        .join("");
}

function toTitle(name) {
    return name.split("_")
        .map(w => w.charAt(0).toUpperCase() + w.slice(1))
        .join(" ");
}

const overworld = [];
const nether = [];

for (const line of lines) {
    const clean = line.split("<")[0];
    const isNether = clean.includes("%nether%");

    const file = path.basename(clean.replace("%nether%", ""), ".png");
    const className = "ItemFish" + toClassName(file);
    const title = toTitle(file);

    const entry = {
        file,
        className,
        title
    };

    (isNether ? nether : overworld).push(entry);
}

function generateFile(filename, parentClass, dimension, list) {
    let classes = "";

    for (const f of list) {
        classes += `
    class ${f.className} extends ${parentClass} {

        public ${f.className}() {
            super("nukkit:${f.file}", "${f.title}", "${f.file}");
        }

    }
`;
    }

    return `package com.angga7togk.gamecore.item.fish;

import com.angga7togk.gamecore.item.BaseFishItem;
import cn.nukkit.level.DimensionEnum;

public abstract class ${parentClass} extends BaseFishItem {

    public ${parentClass}(String id, String name, String texture) {
        super(id, name, texture);
    }

    @Override
    public DimensionEnum getDimension() {
        return DimensionEnum.${dimension};
    }

    // FISH CLASS
${classes}
}
`;
}

fs.writeFileSync(
    path.join(outDir, "ItemFishOverworld.java"),
    generateFile("ItemFishOverworld.java", "ItemFishOverworld", "OVERWORLD", overworld)
);

fs.writeFileSync(
    path.join(outDir, "ItemFishNether.java"),
    generateFile("ItemFishNether.java", "ItemFishNether", "NETHER", nether)
);

console.log("DONE 🔥 Overworld:", overworld.length, "Nether:", nether.length);