package com.angga7togk.gamecore.api.clover;

import java.util.HashMap;
import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.types.Sort;

import cn.nukkit.utils.Config;

public class CloverAPI {

    private static final Map<String, Long> clovers = new HashMap<>();

    private static final Config config;

    static {
        Loader loader = Loader.get();
        loader.saveResource("clover_data.yml");
        config = new Config(loader.getDataFolder() + "/clover_data.yml", Config.YAML);
        loadFromConfig();
    }

    public static Long getClover(String player) {
        return clovers.getOrDefault(player.toLowerCase(), 0L);
    }

    public static void addClover(String player, long clover) {
        if (clover <= 0)
            return;

        String key = player.toLowerCase();
        clovers.put(key, getClover(key) + clover);
    }

    public static boolean reduceClover(String player, long clover) {
        if (clover <= 0)
            return false;

        String key = player.toLowerCase();
        long myClover = getClover(key);

        if (myClover < clover)
            return false;

        clovers.put(key, myClover - clover);
        return true;
    }

    public static boolean hasClover(String player, long clover) {
        return getClover(player) >= clover;
    }

    public static Map<String, Long> getAll() {
        return clovers;
    }

    public static Map<String, Long> getAllSorted(Sort sort) {
        return clovers.entrySet()
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
        for (String key : config.getSection("players").getKeys()) {
            clovers.put(key, config.getLong("players." + key, 0L));
        }
    }

    public static void saveToConfig() {
        for (Map.Entry<String, Long> entry : clovers.entrySet()) {
            config.set("players." + entry.getKey(), entry.getValue());
        }
        config.save();
    }

}
