package com.angga7togk.gamecore.listener;

import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.enums.Rarity;
import com.angga7togk.gamecore.domain.model.fishing.Fish;
import com.angga7togk.gamecore.service.FishingService;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.inventory.EnchantInventory;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;

public class EventListener implements Listener {

    private final Loader loader;

    public EventListener(Loader loader) {
        this.loader = loader;
    }

    @EventHandler(ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // remove custom fish lama
        PlayerInventory inv = player.getInventory();
        for (Map.Entry<Integer, Item> entry : inv.getContents().entrySet()) {
            if (entry.getValue().getNamedTag().getBoolean("custom_fish")) {
                inv.clear(entry.getKey(), true);
            }
        }

        // remove custom fish lama
        PlayerEnderChestInventory enderInv = player.getEnderChestInventory();
        for (Map.Entry<Integer, Item> entry : enderInv.getContents().entrySet()) {
            if (entry.getValue().getNamedTag().getBoolean("custom_fish")) {
                enderInv.clear(entry.getKey(), true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuit(PlayerQuitEvent event) {
        FishingService.RarityPityFishing.removeFailStack(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTransaction(InventoryTransactionEvent event) {
        InventoryTransaction transaction = event.getTransaction();

        for (InventoryAction action : transaction.getActions()) {
            if (action instanceof SlotChangeAction slotChange) {

                // Cek apakah ini di Enchantment Table
                if (slotChange.getInventory() instanceof EnchantInventory enchantInventory) {
                    Item itemPut = slotChange.getSourceItem(); // Item yang ditaruh
                    Item itemTaken = slotChange.getTargetItem(); // Item yang diambil

                    // Di sini lu bisa cek kalau itemPut itu Ikan
                    // Dan slotChange.getInventory().getItem(0) itu Sword/Pickaxe
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchantCombine(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof EnchantInventory))
            return;

        Player player = event.getPlayer();
        Item targetItem = event.getInventory().getItem(0); // Item yang mau di-enchant (Slot 1)
        Item ingredient = event.getInventory().getItem(1); // Ikan (Slot 2 / Lapis Slot)

        // Cek apakah item kedua adalah ikan dari GameCore
        Fish fish = Fish.fromItem(ingredient);
        if (fish == null || fish.getFishEffect().isEmpty())
            return;

        // Cegah player klik slot hasil enchantment bawaan biar gak crash
        if (event.getSlot() == 2 || event.getSlot() == 3) {

            // LOGIC: Pindahkan Skill Ikan ke Item Target
            if (applyFishSoul(targetItem, fish)) {
                // Hapus ikan (dikonsumsi)
                event.getInventory().setItem(1, Item.get(Item.AIR));
                // Update item target
                event.getInventory().setItem(0, targetItem);

                player.sendMessage("§a✨ Jiwa Ikan " + fish.getRarity().name() + " telah merasuk ke dalam item lu!");
                player.getLevel().addSound(player, Sound.RANDOM_LEVELUP, 1.0f, 1.0f, player);

                event.setCancelled(true); // Cancel biar gak jalanin enchantment vanilla
            }
        }
    }

    private boolean applyFishSoul(Item item, Fish fish) {
        // Cek apakah item target adalah FishingRod GameCore atau Tools
        // Lu bisa tambahin pengecekan Pickaxe/Sword di sini

        cn.nukkit.nbt.tag.CompoundTag tag = item.getNamedTag();
        if (tag == null)
            tag = new cn.nukkit.nbt.tag.CompoundTag();

        // Ambil efek dari ikan dan pasang ke NBT Item Target
        // Kita simpan dalam tag "SoulAbilities"
        cn.nukkit.nbt.tag.ListTag<cn.nukkit.nbt.tag.CompoundTag> soulList = new cn.nukkit.nbt.tag.ListTag<>(
                "SoulAbilities");

        for (Effect effect : fish.getFishEffect()) {
            cn.nukkit.nbt.tag.CompoundTag eTag = new cn.nukkit.nbt.tag.CompoundTag()
                    .putInt("id", effect.getId())
                    .putInt("amp", effect.getAmplifier());
            soulList.add(eTag);
        }

        tag.putList(soulList);
        item.setNamedTag(tag);

        // Update Lore biar kelihatan ada soul-nya
        String[] oldLore = item.getLore();
        java.util.List<String> newLore = new java.util.ArrayList<>(java.util.Arrays.asList(oldLore));
        newLore.add("§r§b✨ Soul: §e" + fish.getRarity().name());
        item.setLore(newLore.toArray(new String[0]));

        return true;
    }
}
