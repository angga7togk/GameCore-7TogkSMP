package com.angga7togk.gamecore.command.defaults.economy;


import com.angga7togk.gamecore.api.economy.EconomyAPI;

import cn.nukkit.command.Command;

public class AddBalanceCommand extends Command {
    public AddBalanceCommand() {
        super("addbalance", "Add balance to a player", "/addbalance <player> <amount>",
                new String[] { "addmoney", "givemoney", "givebalance" });
        this.setPermission("addbalance.command");
    }

    @Override
    public boolean execute(cn.nukkit.command.CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: " + this.getUsage());
            return false;
        }
        String targetPlayer = args[0];
        long amount;
        try {
            amount = Long.parseLong(args[1]);
            if (amount <= 0) {
                sender.sendMessage("Amount must be a positive number.");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount. Please enter a valid number.");
            return false;
        }

        if (!EconomyAPI.exists(targetPlayer)) {
            sender.sendMessage("Player " + targetPlayer + " does not have a wallet.");
            return false;
        }

        EconomyAPI.addBalance(targetPlayer, amount);
        sender.sendMessage("Added " + amount + " to " + targetPlayer + "'s balance.");

        return true;
    }
}
