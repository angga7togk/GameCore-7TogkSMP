package com.angga7togk.gamecore.api.floatingtext;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.tasks.FloatingTextTask;

import cn.nukkit.Server;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;

public class FloatingTextAPI {

    private static final Map<String, FloatingTextFunction> texts = new HashMap<>();
    private static final Map<String, FloatingTextTask> floatingTexts = new HashMap<>();

    private static final Config config;

    static {
        Loader loader = Loader.get();
        loader.saveResource("floating_texts.yml");
        config = new Config(loader.getDataFolder() + "/floating_texts.yml");
    }

    public static void register(String name, FloatingTextFunction ftf) {
        texts.put(name, ftf);

        if (config.exists(name)) {
            String serialized = config.getString(name);
            FloatingTextTask ft = FloatingTextTask.deserialize(serialized);

            if (ft != null) {
                Random random = new Random();
                Server.getInstance().getScheduler().scheduleRepeatingTask(Loader.get(), ft, 20 * random.nextInt(5, 15));
                floatingTexts.put(name, ft);
            }
        }
    }

    public static FloatingTextFunction getText(String name) {
        return texts.get(name);
    }

    public static boolean hasText(String name) {
        return texts.containsKey(name);
    }

    public static void setPosition(String name, Location location) {
        unsetPosition(name);

        FloatingTextTask floatingText = new FloatingTextTask(name, location);
        Random random = new Random();
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(Loader.get(), floatingText, 20 * 3,
                20 * random.nextInt(5, 15));
        floatingTexts.put(name, floatingText);
    }

    public static void unsetPosition(String name) {

        FloatingTextTask ft = floatingTexts.remove(name);
        if (ft != null) {
            ft.remove();
            ft.cancel();
        }
    }

    public static void saveToConfig() {
        config.setAll(new LinkedHashMap<>());
        for (FloatingTextTask ft : floatingTexts.values()) {
            String serialized = ft.serialize();
            config.set(ft.getName(), serialized);
        }
        config.save();
    }

}
