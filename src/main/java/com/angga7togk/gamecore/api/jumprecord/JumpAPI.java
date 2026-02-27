package com.angga7togk.gamecore.api.jumprecord;

import java.util.LinkedHashMap;
import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.types.Sort;

import cn.nukkit.utils.Config;

public class JumpAPI {

    private static Config config;

    static {
        Loader loader = Loader.get();
        loader.saveResource("jump_records.yml");
        config = new Config(loader.getDataFolder() + "/jump_records.yml", Config.YAML);

    }

    
    public static Long getJump(String playerName) {
        return config.getLong(playerName.toLowerCase(), 0L);
    }

    
    public static void addJump(String playerName, long amount) {
        config.set(playerName.toLowerCase(), getJump(playerName) + amount);
        config.save();
    }

    
    public static void setJump(String playerName, long amount) {
        config.set(playerName.toLowerCase(), amount);
        config.save();
    }

    
    public static Map<String, Long> getAll() {
        Map<String, Long> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : config.getAll().entrySet()) {
            Object value = entry.getValue();

            if (value instanceof Number) {
                result.put(entry.getKey(), ((Number) value).longValue());
            }
        }

        return result;
    }

    
    public static Map<String, Long> getAllSorted(Sort sort) {
        Map<String, Long> data = getAll();

        return data.entrySet()
                .stream()
                .sorted((a, b) -> {
                    if (sort == Sort.HIGH_TO_LOW) {
                        return Long.compare(b.getValue(), a.getValue());
                    } else {
                        return Long.compare(a.getValue(), b.getValue());
                    }
                })
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll);
    }

    
    public static void resetAll() {
        config.setAll(new LinkedHashMap<>());
        config.save();
    }

}
