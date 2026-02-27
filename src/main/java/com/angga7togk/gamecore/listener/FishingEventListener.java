package com.angga7togk.gamecore.listener;

import java.util.HashMap;
import com.angga7togk.gamecore.domain.enums.Rarity;
import com.angga7togk.gamecore.domain.model.fishing.Fish;
import com.angga7togk.gamecore.item.fishingrod.ItemBlazingFishingRod;
import com.angga7togk.gamecore.service.FishingService;
import com.angga7togk.gamecore.utils.Prefix;
import com.angga7togk.gamecore.utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.ProjectileHitEvent;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.LavaParticle;
import cn.nukkit.network.protocol.EntityEventPacket;

public class FishingEventListener implements Listener {

    private static final HashMap<String, Long> cooldown = new HashMap<>();

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        Player player = event.getPlayer();

        if (event.getHook() == null || !event.getHook().caught) {
            return;
        }

        long now = System.currentTimeMillis();
        if (cooldown.containsKey(player.getName()) && now - cooldown.get(player.getName()) < 800) {
            return;
        }
        cooldown.put(player.getName(), now);

        Fish fish = FishingService.chaugtFish(player, null, false);

        if (fish == null) return;

        if (player.getInventory().canAddItem(fish.getItem())) {
            player.getInventory().addItem(fish.getItem());
            // Beritahu Nukkit jangan spawn item loot vanilla lagi
            event.setLoot(null);
        } else {
            player.getLevel().dropItem(player, fish.getItem());
            player.sendMessage("§c[!] Inventory penuh, ikan terjatuh!");
        }

        handleFeedback(player, fish);
    }

    private void handleFeedback(Player player, Fish fish) {
        String fishName = Utils.toHumanReadable(fish.getItem().getName());
        Rarity rarity = fish.getRarity();

        player.sendMessage("§a[!] Berhasil menangkap " + rarity.color() + fishName +
                " §7(§f" + Utils.formatWeight(fish.getWeight()) + "§7)");

        if (rarity.ordinal() >= Rarity.RARE.ordinal()) {
            player.getServer().broadcastMessage(Prefix.INFO + "§b" + player.getName() + " §7mendapatkan §l"
                    + rarity.color() + fishName + "§r §f(§e" + Utils.formatWeight(fish.getWeight()) + "§f)§6!!!");

            player.getLevel().addSound(player, Sound.RANDOM_LEVELUP, 1.0f, 0.8f, player);
            if (rarity.ordinal() >= Rarity.MYTHIC.ordinal()) {
                player.getLevel().addSound(player, Sound.RANDOM_TOTEM, 0.6f, 1.0f, player);
            }
        } else {
            player.getLevel().addSound(player, Sound.RANDOM_ORB, 1.0f, 1.2f, player);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof EntityFishingHook hook) {
            // Perbaikan: Ambil pelemparnya (Shooter)
            if (hook.shootingEntity instanceof Player player) {
                Item item = player.getInventory().getItemInHand();

                if (item instanceof ItemBlazingFishingRod) {
                    Block blockUnder = hook.getLevel().getBlock(hook);
                    // Cek apakah hook mendarat di lava
                    if (blockUnder.getId() == Block.LAVA || blockUnder.getId() == Block.STILL_LAVA) {
                        hook.fireTicks = 0; // Anti terbakar
                        hook.getLevel().addParticle(new LavaParticle(hook));
                    }
                }
            }
        }
    }

    // Hijack paket gelembung air agar jadi percikan lava secara visual
    @EventHandler
    public void onPacketSend(DataPacketSendEvent event) {
        if (event.getPacket() instanceof EntityEventPacket pk) {
            if (pk.event == EntityEventPacket.FISH_HOOK_BUBBLE || pk.event == EntityEventPacket.FISH_HOOK_HOOK) {
                Player player = event.getPlayer();
                if (player.fishing != null && player.getInventory().getItemInHand() instanceof ItemBlazingFishingRod) {
                    EntityFishingHook hook = player.fishing;
                    Block b = hook.getLevel().getBlock(hook);
                    if (b.getId() == Block.LAVA || b.getId() == Block.STILL_LAVA) {
                        // Batalkan gelembung air biru
                        event.setCancelled(true);
                        // Ganti dengan percikan lava manual
                        hook.getLevel().addParticle(new LavaParticle(hook.add(0, 0.2, 0)));
                    }
                }
            }
        }
    }
}