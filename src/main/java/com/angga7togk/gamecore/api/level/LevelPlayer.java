package com.angga7togk.gamecore.api.level;

import java.util.Random;

import com.angga7togk.gamecore.api.economy.EconomyAPI;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Sound;
import lombok.Data;

@Data
public class LevelPlayer {

    public static final int BASE_LEVEL = 1;
    public static final long BASE_GOAL_XP = 508L;

    private String playerName;
    private int level = 1;
    private long xp = 0;
    private int xpMultiplier = 1;

    public LevelPlayer(String playerName) {
        this.playerName = playerName;
    }

    public String getLevelColor() {
        if (this.level >= 180) {
            return "§4";
        }
        if (this.level >= 150) {
            return "§c";
        }
        if (this.level >= 100) {
            return "§d";
        }
        if (this.level >= 75) {
            return "§e";
        }
        if (this.level >= 50) {
            return "§b";
        }
        if (this.level >= 25) {
            return "§a";
        }
        return "§f";
    }

    public long getGoalXp() {
        return BASE_GOAL_XP * (((int) this.level / 10) + 1);
    }

    public void addXp(long amount) {
        long xpObtain = amount * this.xpMultiplier;
        this.xp += xpObtain; // Update XP terlebih dahulu
        long xpGoal = this.getGoalXp();

        while (this.xp >= xpGoal) { // Cek apakah XP cukup untuk naik level
            Player player = Server.getInstance().getPlayer(playerName);
            if (player != null) {
                player.getLevel().addSound(player, Sound.RANDOM_LEVELUP);
                player.sendTitle("§l§eNaik Level", "§aSelamat Kamu Telah Naik Level");
                Random random = new Random();
                long reward = random.nextLong(1, 501) * this.level;

                EconomyAPI.addBalance(playerName, reward);
                player.sendMessage("§7Selamat " + player.getName() + " mendapatkan hadiah kenaiakn level sejumlah "
                        + EconomyAPI.format(reward) + ".");
            }
            this.level++;
            xpGoal = this.getGoalXp(); // Update goal XP untuk level baru
        }
    }

    /**
     * get Xp Percentage 1-100
     * 
     * @return
     */
    public int getXpPercentage() {
        long goalXP = getGoalXp();
        int progress = (int) ((float) this.xp / goalXP * 100);

        progress = Math.max(0, progress);
        progress = Math.min(100, progress);
        if (progress == 0) {
            return 1;
        }
        return progress;
    }

    public String serialize() {
        return playerName + ";"
                + level + ";"
                + xp + ";"
                + xpMultiplier;
    }

    public static LevelPlayer deserialize(String data) {
        if (data == null || data.isEmpty()) {
            return null;
        }

        String[] split = data.split(";");
        if (split.length < 4) {
            return null;
        }

        LevelPlayer lp = new LevelPlayer(split[0]);

        try {
            lp.setLevel(Integer.parseInt(split[1]));
            lp.setXp(Long.parseLong(split[2]));
            lp.setXpMultiplier(Integer.parseInt(split[3]));
        } catch (NumberFormatException e) {
            return null;
        }

        return lp;
    }

}
