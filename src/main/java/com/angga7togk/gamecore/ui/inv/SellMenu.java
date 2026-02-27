// package com.angga7togk.gamecore.ui.inv;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Random;

// import org.apache.logging.log4j.core.Core;

// import cn.nukkit.Player;
// import cn.nukkit.inventory.InventoryType;
// import cn.nukkit.item.Item;
// import cn.nukkit.item.ItemID;
// import cn.nukkit.nbt.tag.CompoundTag;
// import me.iwareq.fakeinventories.FakeInventory;

// public class SellMenu {

//     public static List<Player> sellMode = new ArrayList<>();

//     public static void openMenu(Player player) {
//         if (!sellMode.contains(player)) {
//             sellMode.add(player);
//         }
//         FakeInventory inv = new FakeInventory(InventoryType.DOUBLE_CHEST, "§lSell Item");

//         inv.setItem(45, Item.get(Item.BOOK)
//                 .setNamedTag(new CompoundTag().putString("button", "guide"))
//                 .setCustomName("§l§eGuide")
//                 .setLore("", "§7Sell Item adalah tempat", "§7untuk menjual item item kalian.",
//                         "§7Cukup masukan item ke", "§7Inventory Sell item,", "§7lalu close untuk menjualnya."));
//         for (int i = 46; i < 53; i++) {
//             inv.setItem(i, Item.get(-161, 0, 1).setNamedTag(new CompoundTag()
//                     .putString("button", "barrier")).setCustomName(""));
        
//         int price = getPriceTotal(inv.getContents());
//         inv.setItem(53, Item.get(ItemID.NETHER_STAR, 0, 1)
//                 .setNamedTag(new CompoundTag()
//                         .putString("button", "detail"))
//                 .setCustomName("§l§eDetail §7[Refresh]")
//                 .setLore("", "§aCount, §e" + getCountItem(inv.getContents()) + " Items",
//                         "§aPrice, §e" + ecoapi.format(price),
//                         "",
//                         "§aTotal, §l§e" + ecoapi.format(price)));
//         inv.setDefaultItemHandler((item, event) -> {
//             if (isButton(item)) {
//                 event.setCancelled();
//             }
//             int price2 = getPriceTotal(inv.getContents());
//             inv.setItem(53, Item.get(ItemID.NETHER_STAR, 0, 1)
//                     .setNamedTag(new CompoundTag()
//                             .putString("button", "detail"))
//                     .setCustomName("§l§eDetail §7[Refresh]")
//                     .setLore("", "§aCount, §e" + getCountItem(inv.getContents()) + " Items",
//                             "§aPrice, §e" + ecoapi.format(price2),
//                             "",
//                             "§aTotal, §l§e" + ecoapi.format(price2)));
//         });
//         inv.setCloseHandler((pl) -> onSell(pl, inv.getContents()));
//         player.addWindow(inv);
//     }

//     public void onSell(Player player, Map<Integer, Item> contents) {
//         EconomyService ecoapi = Core.get().getEconomy();
//         int count = 0;
//         int income = 0;
//         Map<Integer, Item> safeContent = new HashMap<>();
//         for (Map.Entry<Integer, Item> entry : contents.entrySet()) {
//             Item item = entry.getValue();
//             if (!isButton(item)) {
//                 income += getPriceItem(item);
//                 count += item.getCount();
//                 safeContent.put(entry.getKey(), item);
//             }
//         }
//         if (income > 0 && count > 0) {
//             ecoapi.addBalance(player.getName(), income);
//             player.sendMessage(prefix + "§aBerhasil menjual " + count + " barang seharga "
//                     + ecoapi.format(income) + ".");
//         }
//     }

//     public int getCountItem(Map<Integer, Item> contents) {
//         int count = 0;
//         for (Map.Entry<Integer, Item> entry : contents.entrySet()) {
//             Item item = entry.getValue();
//             if (!isButton(item)) {
//                 count += item.getCount();
//             }
//         }
//         return count;
//     }

//     public boolean isButton(Item item) {
//         return item.hasCompoundTag() && item.getNamedTag().containsString("button");
//     }

//     public int getPriceTotal(Map<Integer, Item> contents) {
//         int income = 0;
//         for (Map.Entry<Integer, Item> entry : contents.entrySet()) {
//             Item item = entry.getValue();
//             if (!isButton(item)) {
//                 income += getPriceItem(item);
//             }
//         }
//         return income;
//     }

//     public long getPriceItem(Item item) {
//         if (isButton(item)) {
//             return 0;
//         }
//         if (Server.getInstance().getPluginManager().getPlugin("FishingContest") != null) {
//             FishData fish = FishData.fromItem(item);
//             if (fish != null) {
//                 return fish.calculatePrice();
//             }
//         }

//         String itemIds = item.getNamespaceId();

//         if (!getConfig().exists("sell." + itemIds)) {
//             Random random = new Random();
//             int priceRandom = random.nextInt(25);
//             getConfig().set("sell." + itemIds, priceRandom);
//             getConfig().save();
//             return priceRandom * item.getCount();
//         }
//         return getConfig().getInt("sell." + itemIds) * item.getCount();
//     }

//     public void setPrice(Item item, int price) {
//         String itemIds = item.getNamespaceId();
//         getConfig().set("sell." + itemIds, price);
//         getConfig().save();
//     }
// }
