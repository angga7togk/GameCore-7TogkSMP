package com.angga7togk.gamecore;

import java.util.List;

import com.angga7togk.gamecore.command.CoreCommand;
import com.angga7togk.gamecore.command.defaults.economy.*;
import com.angga7togk.gamecore.command.defaults.size.SizeCommand;
import com.angga7togk.gamecore.listener.EventListener;
import com.angga7togk.gamecore.tasks.SaveTask;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;

public class Loader extends PluginBase {

    private CoreCommand coreCommand;

    private static Loader instance;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.coreCommand = new CoreCommand(this);

        this.getServer().getCommandMap().register("size", new SizeCommand());
        this.getServer().getCommandMap().registerAll("economy", List.of(
                new BalanceCommand(),
                new AddBalanceCommand(),
                new ReduceBalanceCommand(),
                new SetBalanceCommand(),
                new TopBalanceCommand(),
                new PayBalanceCommand()));

        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        SaveTask.saveAll();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return coreCommand.onCommand(sender, command, label, args);
    }

    public static Loader get() {
        return instance;
    }
}
