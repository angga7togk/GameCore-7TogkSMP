package com.angga7togk.gamecore.tasks;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.api.clover.CloverAPI;
import com.angga7togk.gamecore.api.economy.EconomyAPI;
import com.angga7togk.gamecore.api.floatingtext.FloatingTextAPI;
import com.angga7togk.gamecore.api.level.LevelAPI;
import com.angga7togk.gamecore.api.playtime.PlaytimeAPI;

import cn.nukkit.scheduler.PluginTask;

public class SaveTask extends PluginTask<Loader> {

    public SaveTask(Loader plugin) {
        super(plugin);
    }

    @Override
    public void onRun(int currentTick) {
        saveAll();
    }

    public static void saveAll() {
        EconomyAPI.saveToConfig();
        FloatingTextAPI.saveToConfig();
        LevelAPI.saveToConfig();
        PlaytimeAPI.saveToConfig();
        CloverAPI.saveToConfig();
    }

}
