package com.angga7togk.gamecore.tasks;

import java.util.HashMap;
import java.util.Map;

import com.angga7togk.gamecore.api.floatingtext.FloatingTextAPI;
import com.angga7togk.gamecore.api.floatingtext.FloatingTextFunction;
import com.angga7togk.gamecore.api.placeholder.PlaceholderAPI;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.scheduler.Task;
import lombok.Getter;

public class FloatingTextTask extends Task{
     @Getter
    private final String name;
    @Getter
    private final Location location;
    @Getter
    private final FloatingTextFunction text;

    // key = player name
    private final Map<String, FloatingTextParticle> particles = new HashMap<>();

    public FloatingTextTask(String floatingTextName, Location location) {
        this.name = floatingTextName;
        this.location = location;
        this.text = FloatingTextAPI.getText(floatingTextName);
    }

    @Override
    public void onRun(int currentTick) {
        Level level = location.getLevel();

        // 1️cleanup: player offline / pindah world
        particles.entrySet().removeIf(entry -> {
            Player player = Server.getInstance().getPlayer(entry.getKey());
            if (player == null || !player.getLevel().equals(level)) {
                FloatingTextParticle particle = entry.getValue();
                particle.setInvisible();
                if (player != null) {
                    player.getLevel().addParticle(particle, player);
                }
                return true;
            }
            return false;
        });

        // 2️spawn / update untuk player di level
        for (Player player : level.getPlayers().values()) {
            if (!player.spawned)
                continue;

            String playerName = player.getName();
            String translated = PlaceholderAPI.translate(player, text.process());

            FloatingTextParticle particle = particles.get(playerName);

            if (particle == null) {
                // spawn baru
                particle = new FloatingTextParticle(
                        location,
                        "",
                        translated);
                particles.put(playerName, particle);
            } else {
                // update text
                particle.setText(translated);
            }

            level.addParticle(particle, player);
        }
    }

    public String serialize() {
        return name + ";" +
                location.getLevel().getName() + ";" +
                location.getX() + ";" +
                location.getY() + ";" +
                location.getZ();
    }

    public static FloatingTextTask deserialize(String data) {
        String[] parts = data.split(";");
        String name = parts[0];
        String levelName = parts[1];
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double z = Double.parseDouble(parts[4]);

        Level level = Server.getInstance().getLevelByName(levelName);
        if (level == null) {
            return null;
        }

        Location location = new Location(x, y, z, level);
        return new FloatingTextTask(name, location);
    }

    /**
     * Optional: panggil kalau mau hapus permanent
     */
    public void remove() {
        Level level = location.getLevel();
        for (FloatingTextParticle particle : particles.values()) {
            particle.setInvisible();
            level.addParticle(particle);
        }
        particles.clear();
    }
}
