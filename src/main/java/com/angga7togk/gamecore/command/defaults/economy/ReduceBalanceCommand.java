package com.angga7togk.gamecore.command.defaults.economy;

import com.angga7togk.gamecore.api.economy.EconomyAPI;

import cn.nukkit.command.Command;

public class ReduceBalanceCommand extends Command {

    public ReduceBalanceCommand() {
        super("reducebalance", "Reduce balance of a player", "/reducebalance <player> <amount>",
                new String[] { "reducemoney" });
        this.setPermission("reducebalance.command");
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
        
        boolean success = EconomyAPI.reduceBalance(targetPlayer, amount).ok();
        if (!success) {
            sender.sendMessage("Failed to reduce balance. Player may have insufficient funds.");
            return false;
        }
        
        sender.sendMessage("Reduced " + amount + " from " + targetPlayer + "'s balance.");
        return true;
    }
}
