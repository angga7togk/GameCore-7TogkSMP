const fs = require("fs");
const path = require("path");

const listFile = "fish_list.txt";
const outFile = "item_texture.json";

const lines = fs.readFileSync(listFile, "utf8")
    .split("\n")
    .map(e => e.trim())
    .filter(Boolean);

const json = {
    resource_pack_name: "7Togk Fish Pack",
    texture_name: "atlas.items",
    texture_data: {}
};

for (const line of lines) {
    const file = path.basename(line, ".png");

    json.texture_data[file] = {
        textures: `textures/items/fish/${file}.png`
    };
}

fs.writeFileSync(outFile, JSON.stringify(json, null, 2));
console.log("Generated:", outFile);
