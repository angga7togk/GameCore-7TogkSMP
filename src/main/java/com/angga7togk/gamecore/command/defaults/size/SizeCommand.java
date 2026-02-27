package com.angga7togk.gamecore.command.defaults.size;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class SizeCommand extends Command {

    private String PREFIX = "§6[Size] §r";

    public SizeCommand() {
        super("size", "Change character size", "/size <num|reset> [player]", new String[] { "scale" });
        this.setPermission("size.command");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!testPermission(sender))
            return false;

        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        // ========== PLAYER ONLY SELF COMMAND ==========
        if (args.length == 1) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(PREFIX + "§cGunakan: /size <num|reset> <player>");
                return true;
            }

            if (!sender.hasPermission("size.command")) {
                sender.sendMessage(PREFIX + "§cKamu tidak punya permission!");
                return true;
            }

            if (args[0].equalsIgnoreCase("reset")) {
                player.setScale(1f);
                player.sendMessage("§aSize kamu berhasil di-reset ke normal!");
                return true;
            }

            Float scale = parseScale(sender, args[0]);
            if (scale == null)
                return true;

            player.setScale(scale);
            player.sendMessage("§aSize kamu diubah menjadi §e" + scale);
            return true;
        }

        // ========== OTHERS COMMAND ==========
        if (args.length == 2) {
            if (!sender.hasPermission("size.command.others")) {
                sender.sendMessage(PREFIX + "§cKamu tidak punya permission untuk mengubah size player lain!");
                return true;
            }

            Player target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(PREFIX + "§cPlayer tidak ditemukan!");
                return true;
            }

            if (args[1].equalsIgnoreCase("reset")) {
                target.setScale(1f);
                target.sendMessage("§aSize kamu telah di-reset oleh §e" + sender.getName());
                sender.sendMessage(PREFIX + "§aSize player §e" + target.getName() + " §aberhasil di-reset!");
                return true;
            }

            Float scale = parseScale(sender, args[1]);
            if (scale == null)
                return true;

            target.setScale(scale);
            target.sendMessage("§aSize kamu diubah menjadi §e" + scale + " §aoleh §e" + sender.getName());
            sender.sendMessage(PREFIX + "§aSize player §e" + target.getName() + " §aberhasil diubah ke §e" + scale);
            return true;
        }

        sendUsage(sender);
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(PREFIX + "§eUsage:");
        sender.sendMessage(PREFIX + "§7/size <angka>");
        sender.sendMessage(PREFIX + "§7/size reset");
        sender.sendMessage(PREFIX + "§7/size <player> <angka>");
        sender.sendMessage(PREFIX + "§7/size <player> reset");
    }

    private Float parseScale(CommandSender sender, String input) {
        float scale;
        try {
            scale = Float.parseFloat(input);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + "§cMasukkan angka yang valid!");
            return null;
        }

        if (scale < 0.1f || scale > 10f) {
            sender.sendMessage(PREFIX + "§cSize minimal 0.1 dan maksimal 10!");
            return null;
        }

        return scale;
    }
}
