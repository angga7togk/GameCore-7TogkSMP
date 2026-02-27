package com.angga7togk.gamecore.item.fishingrod;

import org.apache.commons.math3.util.FastMath;

import com.angga7togk.gamecore.entity.EntityBlazingFishingHook;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.customitem.CustomItemDefinition;
import cn.nukkit.item.customitem.ItemCustom;
import cn.nukkit.item.customitem.data.RenderOffsets;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.network.protocol.types.inventory.creative.CreativeItemCategory;

public class ItemBlazingFishingRod extends ItemCustom {

    public ItemBlazingFishingRod() {
        super("nukkit:blazing_fishing_rod", "Blazing Fishing Rod", "blazing_fishing_rod");
    }

    @Override
    public CustomItemDefinition getDefinition() {
        return CustomItemDefinition
                .simpleBuilder(this, CreativeItemCategory.EQUIPMENT)
                .renderOffsets(RenderOffsets.scaleOffset(16))
                .handEquipped(true)
                .build();
    }

    @Override
    public boolean onClickAir(Player player, Vector3 directionVector) {
        if (player.fishing != null) {
            if (!this.isUnbreakable()) {
                if (player.fishing.getTarget() > 0) {
                    this.meta = this.meta + 2;
                } else {
                    this.meta++;
                }
            }
            this.stopFishing(player, true);
        } else {
            this.startFishing(player);
        }
        return true;
    }

    /**
     * Logika Custom Start Fishing
     */
    public void startFishing(Player player) {
        Level level = player.getLevel();

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", player.x))
                        .add(new DoubleTag("", player.y + player.getEyeHeight()))
                        .add(new DoubleTag("", player.z)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("",
                                -Math.sin(player.yaw / 180 + Math.PI) * Math.cos(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag("", -Math.sin(player.pitch / 180 * Math.PI)))
                        .add(new DoubleTag("",
                                Math.cos(player.yaw / 180 * Math.PI) * Math.cos(player.pitch / 180 * Math.PI))))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) player.yaw))
                        .add(new FloatTag("", (float) player.pitch)));

        // Lemparan Blazing Rod dibuat sedikit lebih cepat (1.5) dibanding vanilla (1.1)
        double force = 1.5;

        EntityBlazingFishingHook fishingHook = (EntityBlazingFishingHook) Entity.createEntity(
                EntityBlazingFishingHook.NAME_ID, player.getChunk(), nbt,
                player);

        if (fishingHook == null)
            return;

        fishingHook.setMotion(new Vector3(
                -Math.sin(FastMath.toRadians(player.yaw)) * Math.cos(FastMath.toRadians(player.pitch)) * force,
                -Math.sin(FastMath.toRadians(player.pitch)) * force,
                Math.cos(FastMath.toRadians(player.yaw)) * Math.cos(FastMath.toRadians(player.pitch)) * force));

        ProjectileLaunchEvent ev = new ProjectileLaunchEvent(fishingHook);
        player.getServer().getPluginManager().callEvent(ev);

        if (ev.isCancelled()) {
            fishingHook.close();
        } else {
            player.fishing = fishingHook; // Set field fishing di objek Player
            fishingHook.rod = this; // Set joran yang dipake ke hook
            fishingHook.checkLure();
            fishingHook.spawnToAll();

            // Sound Throw
            level.addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_THROW, -1, "minecraft:player", false, false);
        }
    }

    /**
     * Logika Custom Stop Fishing
     */
    public void stopFishing(Player player, boolean click) {
        if (player.fishing != null) {
            if (click) {
                // Tambah damage ke joran pas narik
                if (!this.isUnbreakable()) {
                    this.setDamage(this.getDamage() + (player.fishing.caught ? 2 : 1));
                    player.getInventory().setItemInHand(this);
                }
                player.fishing.reelLine();
            } else {
                player.fishing.close();
            }
        }
        player.fishing = null;
    }

    @Override
    public int getMaxDurability() {
        return ItemTool.DURABILITY_FISHING_ROD;
    }

    @Override
    public boolean isTool() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx,
            double fy, double fz) {
        return this.onClickAir(player, player.getDirectionVector());
    }
}