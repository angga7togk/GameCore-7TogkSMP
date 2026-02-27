package com.angga7togk.gamecore.command.defaults.economy;


import com.angga7togk.gamecore.api.economy.EconomyAPI;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class PayBalanceCommand extends Command {

    public PayBalanceCommand() {
        super("paybalance", "Transfer money to player", "/paybalance <player> <amount>",
                new String[] { "pay", "paybal", "transfer" });
        this.setPermission("paybalance.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("please use command in game!");
            return false;
        }
        if (args.length < 2) {
            sender.sendMessage("Usage: " + this.getUsage());
            return false;
        }

        String target = args[0];
        long amount;
        try {
            amount = Long.parseLong(args[1]);
            if (amount <= 0) {
                sender.sendMessage("Amount must be a positive number.");
                return false;
            }
        } catch (Exception e) {
            sender.sendMessage("amount not valid!");
            return false;
        }
        if (!EconomyAPI.exists(target)) {
            sender.sendMessage("player not found!");
            return false;
        }

        if (!EconomyAPI.hasBalance(player.getName(), amount)) {
            sender.sendMessage("Silakan cari uang lagi, uang kamu kurang!");
            sender.sendMessage("Yang perlu dibayar: " + EconomyAPI.format(amount));
            return false;
        }

        if (EconomyAPI.transfer(player.getName(), target, amount).ok()) {
            sender.sendMessage("Berhasil transfer uang ke " + target);
            sender.sendMessage("Telah membayar: " + EconomyAPI.format(amount));
        } else {
            sender.sendMessage("Silakan cari uang lagi, uang kamu kurang!");
            sender.sendMessage("Yang perlu dibayar: " + EconomyAPI.format(amount));
        }
        return true;
    }
}
