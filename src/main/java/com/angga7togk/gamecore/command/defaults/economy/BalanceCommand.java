package com.angga7togk.gamecore.command.defaults.economy;

import com.angga7togk.gamecore.api.economy.EconomyAPI;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class BalanceCommand extends Command {

    public BalanceCommand() {
        super("balance", "Check your balance", "/balance [player]",
                new String[] { "bal", "money", "checkbalance", "checkmoney", "mymoney" });
        this.setPermission("balance.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        String targetPlayer = sender.getName();
        if (args.length >= 1) {
            targetPlayer = args[0];
        }

        if (!EconomyAPI.exists(targetPlayer)) {
            sender.sendMessage("§cPlayer " + targetPlayer + " does not have a wallet.");
            return false;
        }

        long balance = EconomyAPI.getBalance(targetPlayer);

        // ===== PLAYER INFO =====
        sender.sendMessage("§6=== PLAYER ===");
        if (!targetPlayer.equals(sender.getName())) {
            sender.sendMessage("§ePlayer: §f" + targetPlayer);
        }
        sender.sendMessage("§eBalance: §a" + balance);

        return true;
    }

}
