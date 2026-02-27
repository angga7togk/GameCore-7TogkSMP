package com.angga7togk.gamecore.command.defaults.economy;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import java.util.*;

import com.angga7togk.gamecore.api.economy.EconomyAPI;

public class TopBalanceCommand extends Command {

    private static final int ITEMS_PER_PAGE = 5;

    public TopBalanceCommand() {
        super("topbalance", "Show top richest players", "/topbalance [page]",
                new String[] { "rich", "baltop", "moneytop", "topmoney" });
        this.setPermission("topbalance.command");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!this.testPermission(sender)) {
            return false;
        }

        int page = 1;
        if (args.length >= 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
            }
        }

        Map<String, Long> balances = EconomyAPI.getAll();

        if (balances.isEmpty()) {
            sender.sendMessage("§cNo economy data found.");
            return true;
        }

        // ===== SORT DESC =====
        List<Map.Entry<String, Long>> sorted = new ArrayList<>(balances.entrySet());
        sorted.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        int totalPages = (int) Math.ceil(sorted.size() / (double) ITEMS_PER_PAGE);
        page = Math.max(1, Math.min(page, totalPages));

        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, sorted.size());

        sender.sendMessage("§6=== TOP MONEY (Page " + page + "/" + totalPages + ") ===");

        for (int i = start; i < end; i++) {
            Map.Entry<String, Long> entry = sorted.get(i);
            sender.sendMessage(
                    "§e" + (i + 1) + ". §f" + entry.getKey() + " §7- §a" + EconomyAPI.format(entry.getValue()));
        }

        return true;
    }
}
