package com.angga7togk.gamecore;

import java.util.List;

import com.angga7togk.gamecore.command.CoreCommand;
import com.angga7togk.gamecore.command.defaults.economy.*;
import com.angga7togk.gamecore.command.defaults.size.SizeCommand;
import com.angga7togk.gamecore.entity.EntityBlazingFishingHook;
import com.angga7togk.gamecore.item.FishRegistry;
import com.angga7togk.gamecore.item.fishingrod.ItemBlazingFishingRod;
import com.angga7togk.gamecore.listener.EventListener;
import com.angga7togk.gamecore.listener.FishingEventListener;
import com.angga7togk.gamecore.tasks.SaveTask;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
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
        FishRegistry.registerAll();
        Item.registerCustomItem(ItemBlazingFishingRod.class, true);
        Entity.registerEntity(EntityBlazingFishingHook.NAME_ID, EntityBlazingFishingHook.class);
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
        this.getServer().getPluginManager().registerEvents(new FishingEventListener(), this);
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
