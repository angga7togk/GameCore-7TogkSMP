package com.angga7togk.gamecore.api.level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.enums.Sort;

import cn.nukkit.utils.Config;

public class LevelAPI {
    private static final Map<String, LevelPlayer> levels = new HashMap<>();

    private static final Config config;

    static {
        Loader loader = Loader.get();
        loader.saveResource("levels.yml");
        config = new Config(loader.getDataFolder() + "/levels.yml", Config.YAML);
        
        loadFromConfig();
    }

    
    public static LevelPlayer getLeveL(String playerName) {
        return levels.putIfAbsent(playerName.toLowerCase(), new LevelPlayer(playerName.toLowerCase()));
    }

    
    public static List<LevelPlayer> getAll() {
        return levels.values().stream().toList();
    }

    
    public static List<LevelPlayer> getAllSorted(Sort sort) {
        return levels.values()
                .stream()
                .sorted((a, b) -> {
                    int levelCmp = Integer.compare(a.getLevel(), b.getLevel());
                    if (levelCmp == 0) {
                        levelCmp = Long.compare(a.getXp(), b.getXp());
                    }
                    return sort == Sort.HIGH_TO_LOW ? -levelCmp : levelCmp;
                })
                .toList();
    }

    private static void loadFromConfig() {
        for (String key : config.getKeys(false)) {
            String data = config.getString(key);
            LevelPlayer levelPlayer = LevelPlayer.deserialize(data);
            if (levelPlayer != null) {
                levels.put(key, levelPlayer);
            }
        }
    }

    public static void saveToConfig() {
        for (LevelPlayer levelPlayer : levels.values()) {
            String data = levelPlayer.serialize();
            config.set(levelPlayer.getPlayerName(), data);
        }
        config.save();
    }

}
