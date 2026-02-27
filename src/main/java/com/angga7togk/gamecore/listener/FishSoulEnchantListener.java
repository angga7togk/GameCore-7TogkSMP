package com.angga7togk.gamecore.listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.model.fishing.Fish;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.Server;

public class FishSoulEnchantListener implements Listener {

    private Loader loader;

    public FishSoulEnchantListener(Loader loader) {
        this.loader = loader;
    }

    @EventHandler
    public void onTransaction(InventoryTransactionEvent event) {
        InventoryTransaction transaction = event.getTransaction();
        Player player = transaction.getSource();

        for (InventoryAction action : transaction.getActions()) {
            if (action instanceof SlotChangeAction slotAction) {
                if (slotAction.getInventory() instanceof EnchantInventory enchantInv) {

                    // Kita kasih delay 1 tick biar item beneran masuk ke slot dulu
                    Server.getInstance().getScheduler().scheduleDelayedTask(loader, () -> {
                        updateEnchantResult(enchantInv, player);
                    }, 1);
                }
            }
        }
    }

    private void updateEnchantResult(EnchantInventory inv, Player player) {
        Item tools = inv.getItem(0); // Slot Tools
        Item reagent = inv.getItem(1); // Slot Lapis (Ikan)

        Fish fish = Fish.fromItem(reagent);

        // Jika ada item dan ada ikan ber-soul
        if (!tools.isNull() && fish != null && !fish.getFishEffect().isEmpty()) {

            // Clone item tools untuk jadi hasil
            Item result = tools.clone();

            // Masukkan jiwa ikan ke item result (panggil logic Fish lu)
            // Di sini kita pakai logic saveToItem versi tools atau manual NBT
            applySoulToItem(result, fish);

            // Munculkan hasil di slot 2 (Atau slot output yang tersedia di UI Bedrock)
            // Catatan: Di Enchant Table Bedrock, slot hasil biasanya ditangani oleh Client.
            // Kita paksa kirim item ke slot tersebut.
            inv.setItem(2, result);

            // Kirim pesan hint
            player.sendActionBar("§l§b> §eJiwa Ikan " + fish.getRarity().name() + " Terdeteksi! §b<");
        } else {
            // Jika ikan diambil, kosongkan slot hasil
            inv.setItem(2, Item.get(Item.AIR));
        }
    }

    private void applySoulToItem(Item item, Fish fish) {
        CompoundTag nbt = item.getNamedTag();
        if (nbt == null)
            nbt = new CompoundTag();

        // Buat tag jiwa
        ListTag<CompoundTag> soulList = new ListTag<>(
                "SoulAbilities");
        fish.getFishEffect().forEach(eff -> {
            soulList.add(new CompoundTag()
                    .putInt("id", eff.getId())
                    .putInt("amp", eff.getAmplifier()));
        });

        nbt.putList(soulList);
        item.setNamedTag(nbt); 

        // Update Lore
        String[] lore = item.getLore();
        List<String> newLore = new ArrayList<>(Arrays.asList(lore));
        newLore.add("");
        newLore.add("§r§b✨ Soul Infused: §e" + fish.getRarity().name());
        item.setLore(newLore.toArray(new String[0]));
    }
}