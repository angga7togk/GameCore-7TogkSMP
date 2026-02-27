package com.angga7togk.gamecore.command.defaults.economy;


import com.angga7togk.gamecore.api.economy.EconomyAPI;

import cn.nukkit.command.Command;

public class SetBalanceCommand extends Command{
    
    public SetBalanceCommand(){
        super("setbalance", "Set balance of a player", "/setbalance <player> <amount>",
                new String[] { "setmoney", });
        this.setPermission("setbalance.command");
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
            if (amount < 0) {
                sender.sendMessage("Amount must be a non-negative number.");
                return false;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid amount. Please enter a valid number.");
            return false;
        }
        // Assuming there's an EconomyService to handle setting balance
        if (!EconomyAPI.exists(targetPlayer)) {
            sender.sendMessage("Player " + targetPlayer + " does not have a wallet.");
            return false;
        }
        EconomyAPI.setBalance(targetPlayer, amount);
        sender.sendMessage("Set " + targetPlayer + "'s balance to " + amount);
        return true;
    }
    
}
