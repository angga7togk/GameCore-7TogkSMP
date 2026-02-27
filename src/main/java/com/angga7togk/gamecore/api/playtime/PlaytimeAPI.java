package com.angga7togk.gamecore.api.playtime;

import java.util.HashMap;
import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.types.Sort;

import cn.nukkit.utils.Config;
import lombok.Getter;

public class PlaytimeAPI {

    @Getter
    private static final Map<String, Long> playtimes = new HashMap<>();

    private static final Config cfg;

    static {
        Loader loader = Loader.get();
        loader.saveResource("playtime_data.yml");
        cfg = new Config(loader.getDataFolder() + "/playtime_data.yml", Config.YAML);
        loadFromConfig();
    }

    
    public static Long getTime(String playername) {
        return playtimes.getOrDefault(playername.toLowerCase(), 0L);
    }

    
    public static Map<String, Long> getAll() {
        return playtimes;
    }

    
    public static Map<String, Long> getAllSorted(Sort sort) {
        return playtimes.entrySet()
                .stream()
                .sorted((a, b) -> {
                    int cmp = Long.compare(a.getValue(), b.getValue());
                    return sort == Sort.HIGH_TO_LOW ? -cmp : cmp;
                })
                .collect(
                        java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                java.util.LinkedHashMap::new));
    }

    private static void loadFromConfig() {
        playtimes.clear();
        for (String key : cfg.getKeys(false)) {
            Long value = cfg.getLong(key, 0L);
            playtimes.put(key, value);
        }
    }

    public static void saveToConfig() {
        for (Map.Entry<String, Long> entry : playtimes.entrySet()) {
            cfg.set(entry.getKey(), entry.getValue());
        }
        cfg.save();
    }

}
