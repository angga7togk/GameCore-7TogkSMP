package com.angga7togk.gamecore.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFishingHook;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.player.PlayerFishEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.randomitem.Fishing;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.LavaParticle;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.EntityEventPacket;
import cn.nukkit.utils.Utils;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class EntityBlazingFishingHook extends EntityFishingHook {
    public static final int NETWORK_ID = 77;
    public static final String NAME_ID = "BlazingFishingHook";
    private long target = 0;

    public EntityBlazingFishingHook(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, null);
    }

    public EntityBlazingFishingHook(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        super(chunk, nbt, shootingEntity);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.fireProof = true;
        this.fireTicks = 0; // Kebal api
    }

    public boolean isInsideOfLava() {
        int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ());
        return id == Block.LAVA || id == Block.STILL_LAVA;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.target != 0) {
            Entity ent = this.level.getEntity(this.target);
            if (ent == null || !ent.isAlive()) {
                this.caughtEntity = null;
                this.setTarget(0);
            } else {
                this.setPosition(new Vector3(ent.x, ent.y + (getHeight() * 0.75f), ent.z));
            }
            return false;
        }

        boolean hasUpdate = super.onUpdate(currentTick);

        // PERBAIKAN: Cek Lava, bukan Water
        boolean inLava = this.isInsideOfLava();
        if (inLava) {
            this.motionX = 0;
            this.motionY -= getGravity() * -0.04;
            this.motionZ = 0;
            hasUpdate = true;
            this.fireTicks = 0;
        } else if (this.isCollided && this.keepMovement) {
            this.motionX = 0;
            this.motionY = 0;
            this.motionZ = 0;
            this.keepMovement = false;
            hasUpdate = true;
        }

        if (inLava) {
            if (this.waitTimer == 240) {
                this.waitTimer = this.waitChance << 1;
            } else if (this.waitTimer == 360) {
                this.waitTimer = this.waitChance * 3;
            }
            if (!this.attracted) {
                if (this.waitTimer > 0) {
                    --this.waitTimer;
                }
                if (this.waitTimer == 0) {
                    if (Utils.random.nextInt(100) < 90) {
                        this.attractTimer = (Utils.random.nextInt(40) + 20);
                        this.spawnFish();
                        this.caught = false;
                        this.attracted = true;
                    } else {
                        this.waitTimer = this.waitChance;
                    }
                }
            } else if (!this.caught) {
                if (this.attractFish()) {
                    this.caughtTimer = (Utils.random.nextInt(20) + 30);
                    this.fishBites();
                    this.caught = true;
                }
            } else {
                if (this.caughtTimer > 0) {
                    --this.caughtTimer;
                }
                if (this.caughtTimer == 0) {
                    this.attracted = false;
                    this.caught = false;
                    this.waitTimer = this.waitChance * 3;
                }
            }
        }

        return hasUpdate;
    }

    @Override
    protected void updateMotion() {
        if (this.isInsideOfLava() && this.getY() < this.getLavaHeight() - 1) {
            this.motionX = 0;
            this.motionY += getGravity();
            this.motionZ = 0;
        } else if (this.isInsideOfLava() && this.getY() >= this.getLavaHeight() - 1) {
            this.motionX = 0;
            this.motionZ = 0;
            this.motionY = 0;
        } else {
            super.updateMotion();
        }
    }

    public int getLavaHeight() {
        for (int y = this.getFloorY(); y < 256; y++) {
            int id = this.level.getBlockIdAt(this.getFloorX(), y, this.getFloorZ());
            if (id == Block.AIR) {
                return y;
            }
        }
        return this.getFloorY();
    }

    @Override
    public void fishBites() {
        Collection<Player> viewers = this.getViewers().values();

        EntityEventPacket pk = new EntityEventPacket();
        pk.eid = this.getId();
        pk.event = EntityEventPacket.FISH_HOOK_HOOK;
        Server.broadcastPacket(viewers, pk);

        // PERBAIKAN: Gunakan partikel Lava & Smoke daripada Bubble
        Vector3 pos = new Vector3(
                this.x + Utils.random.nextDouble() * 0.5 - 0.25,
                this.getLavaHeight(),
                this.z + Utils.random.nextDouble() * 0.5 - 0.25);

        this.level.addParticle(new LavaParticle(pos));
        this.level.addParticle(new SmokeParticle(pos));
    }

    @Override
    public void spawnFish() {
        this.fish = new Vector3(
                this.x + (Utils.random.nextDouble() * 1.2 + 1) * (Utils.random.nextBoolean() ? -1 : 1),
                this.getLavaHeight(),
                this.z + (Utils.random.nextDouble() * 1.2 + 1) * (Utils.random.nextBoolean() ? -1 : 1));
    }

    @Override
    public boolean attractFish() {
        double multiply = 0.1;
        this.fish.setComponents(
                this.fish.x + (this.x - this.fish.x) * multiply,
                this.fish.y,
                this.fish.z + (this.z - this.fish.z) * multiply);

        if (Utils.random.nextInt(100) < 85) {
            // PERBAIKAN: Partikel lava saat ikan mendekat
            this.level.addParticle(new LavaParticle(this.fish));
        }

        double dist = Math.abs(Math.sqrt(this.x * this.x + this.z * this.z)
                - Math.sqrt(this.fish.x * this.fish.x + this.fish.z * this.fish.z));
        return dist < 0.15;
    }

    @Override
    public void reelLine() {
        if (this.shootingEntity instanceof Player) {
            Player player = (Player) this.shootingEntity;
            if (this.caught) {
                // Di sini nanti bisa panggil FishingService kamu
                Item item = Fishing.getFishingResult(this.rod);
                int experience = Utils.random.nextInt(3) + 1;
                Vector3 pos = new Vector3(this.x, this.getLavaHeight(), this.z);
                Vector3 motion = player.subtract(pos).multiply(0.2);
                motion.y += Math.sqrt(player.add(0, player.getEyeHeight(), 0).distance(pos)) * 0.08;

                PlayerFishEvent event = new PlayerFishEvent(player, this, item, experience, motion);
                this.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    EntityItem itemEntity = new EntityItem(
                            this.level.getChunk((int) this.x >> 4, (int) this.z >> 4, true),
                            Entity.getDefaultNBT(pos, event.getMotion(), ThreadLocalRandom.current().nextFloat() * 360,
                                    0).putShort("Health", 5).putCompound("Item", NBTIO.putItemHelper(event.getLoot()))
                                    .putShort("PickupDelay", 1));

                    itemEntity.setOwner(player.getName());
                    itemEntity.spawnToAll();

                    player.getLevel().dropExpOrb(player, event.getExperience());
                }
            } else if (this.caughtEntity != null) {
                Vector3 motion = this.shootingEntity.subtract(this).multiply(0.1);
                motion.y += Math.sqrt(this.shootingEntity.distance(this)) * 0.08;
                this.caughtEntity.setMotion(motion);
            }
        }
        this.close();
    }
    
    

    @Override
    public void onCollideWithEntity(Entity entity) {
        
    }
}