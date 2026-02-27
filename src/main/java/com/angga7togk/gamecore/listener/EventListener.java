package com.angga7togk.gamecore.listener;

import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.types.Rarity;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;

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
        Rarity.removeFailStack(event.getPlayer().getUniqueId());
    }
}
