package com.angga7togk.gamecore.utils;

import java.util.concurrent.ThreadLocalRandom;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.StringItem;
import cn.nukkit.utils.Binary;

public class Utils {

    // ================= ITEM -> STRING =================

    public static String itemToString(Item item) {

        if (item == null || item.getId() == Item.AIR) {
            return "AIR";
        }

        String id = item instanceof StringItem ? item.getNamespaceId() : String.valueOf(item.getId());
        int damage = item.getDamage();
        int count = item.getCount();

        String nbtHex = "";

        if (item.hasCompoundTag()) {
            byte[] nbtBytes = item.getCompoundTag();
            nbtHex = Binary.bytesToHexString(nbtBytes);
        }

        return id + ";" + damage + ";" + count + ";" + nbtHex;
    }

    // ================= STRING -> ITEM =================

    public static Item stringToItem(String data) {

        if (data == null || data.equals("AIR")) {
            return Item.AIR_ITEM.clone();
        }

        try {
            String[] split = data.split(";", 4);

            String id = split[0];
            int damage = Integer.parseInt(split[1]);
            int count = Integer.parseInt(split[2]);
            String nbtHex = split.length >= 4 ? split[3] : "";

            Item item = Item.fromString(id);
            item.setDamage(damage);
            item.setCount(count);

            if (!nbtHex.isEmpty()) {
                byte[] nbtBytes = Binary.hexStringToBytes(nbtHex);
                item.setCompoundTag(nbtBytes);
            }

            return item;

        } catch (Exception e) {
            e.printStackTrace();
            return Item.AIR_ITEM.clone();
        }
    }

    public static boolean chance(double percent) {
        double roll = ThreadLocalRandom.current().nextDouble(100);

        double penalty = Math.max(0, 50 - percent) * 0.35;
        return roll < (percent - penalty);
    }

    public static String getPing(Player player) {
        int myPing = player.getPing();
        if (myPing >= 600) {
            return "§r" + myPing + "ms §4(Sangat Buruk)";
        } else if (myPing >= 350) {
            return "§r" + myPing + "ms §c(Buruk)";
        } else if (myPing >= 180) {
            return "§r" + myPing + "ms §e(Delay)";
        } else {
            return myPing >= 60 ? "§r" + myPing + "ms §a(Bagus)"
                    : "§r" + myPing + "ms §a(Sangat Bagus)";
        }
    }

    public static String getTPS() {
        float tps = Server.getInstance().getTicksPerSecond();
        if ((double) tps <= 5.0D) {
            return "§r" + tps + " §4(Sangat Buruk)";
        } else if ((double) tps <= 7.5D) {
            return "§r" + tps + " §c(Buruk)";
        } else if ((double) tps <= 10.5D) {
            return "§r" + tps + " §e(Delay)";
        } else {
            return (double) tps <= 18.0D ? "§r" + tps + " §a(Bagus)"
                    : "§r" + tps + " §a(Sangat Bagus)";
        }
    }

    /**
     * Convert ENUM_STYLE or CONSTANT_CASE string
     * into Human Readable format.
     *
     * Example:
     * SPECIAL -> Special
     * VERY_RARE -> Very Rare
     * ULTRA_MYTHIC -> Ultra Mythic
     */
    public static String toHumanReadable(String input) {
        if (input == null || input.isEmpty())
            return "";

        String[] parts = input.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty())
                continue;

            sb.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }

        return sb.toString().trim();
    }

}
