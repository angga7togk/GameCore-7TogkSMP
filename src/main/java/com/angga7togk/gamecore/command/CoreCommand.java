package com.angga7togk.gamecore.command;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.api.floatingtext.FloatingTextAPI;
import com.angga7togk.gamecore.api.jumprecord.JumpAPI;
import com.angga7togk.gamecore.api.vote.VoteAPI;
import com.angga7togk.gamecore.utils.Prefix;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;

public class CoreCommand {

    @Getter
    private final Loader loader;

    public CoreCommand(Loader loader) {
        this.loader = loader;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "floatingtext" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Prefix.ERROR + "This command can only be used in-game.");
                    return false;
                }

                if (args.length < 2) {
                    sender.sendMessage(Prefix.WARNING + "Usage: " + command.getUsage());
                    return false;
                }

                String action = args[0];
                String name = args[1];

                if (action.equalsIgnoreCase("set")) {
                    if (!FloatingTextAPI.hasText(name)) {
                        sender.sendMessage(Prefix.ERROR + "Floating text with name '" + name + "' does not exist.");
                        return false;
                    }
                    FloatingTextAPI.setPosition(name, player.getLocation());
                    sender.sendMessage(Prefix.SUCCESS + "Floating text '" + name + "' has been set at your location.");
                } else if (action.equalsIgnoreCase("remove")) {
                    FloatingTextAPI.unsetPosition(name);
                    sender.sendMessage(Prefix.DANGER + "Floating text '" + name + "' has been removed.");
                } else {
                    sender.sendMessage(Prefix.WARNING + "Unknown action: " + action);
                    sender.sendMessage(Prefix.WARNING + "Usage: " + command.getUsage());
                    return false;
                }
            }

            case "resetjump" -> {
                if (sender instanceof Player) {
                    sender.sendMessage(Prefix.WARNING + "Pakai command lewat console!");
                    return false;
                }
                JumpAPI.resetAll();
                sender.sendMessage(Prefix.SUCCESS + "Berhasil reset all jump!");
            }

            case "mtp" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(Prefix.INFO + "§cCommand ini hanya bisa digunakan oleh player!");
                    return true;
                }

                if (args.length != 1) {
                    sender.sendMessage(Prefix.INFO + "§eUsage: /mtp <player>");
                    return true;
                }

                Player target = sender.getServer().getPlayer(args[0]);
                if (target == null) {
                    sender.sendMessage(Prefix.INFO + "§cPlayer tidak ditemukan atau sedang offline!");
                    return true;
                }

                if (target.equals(player)) {
                    sender.sendMessage(Prefix.INFO + "§cKamu tidak bisa teleport ke diri sendiri!");
                    return true;
                }
                if (target.isOp() || target.hasPermission("admin")) {
                    sender.sendMessage(Prefix.INFO + "§cTidak bisa teleport ke operator!");
                    return true;
                }

                player.teleport(target.getLocation());
                player.sendMessage(Prefix.SUCCESS + "§aBerhasil teleport ke §e" + target.getName());
            }

            case "vote" -> {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Prefix.ERROR + "This command can only be used in-game.");
                    return false;
                }

                try {
                    if (VoteAPI.getCooldown().get(sender.getName()) > 0) {
                        sender.sendMessage(Prefix.INFO + "Please wait " + VoteAPI.getCooldown().get(sender.getName()).toString()
                                + " second(s) until you enter the command again.");
                        return true;
                    } else {
                        VoteAPI.getCooldown().put(sender.getName(), 60);
                    }
                    sender.sendMessage(Prefix.INFO + "Data is retrieved...");
                    if (VoteAPI.checkVoteStatus(sender.getName()).equals("0")) {
                        sender.sendMessage(Prefix.WARNING + "You haven't voted for us today.");
                    } else if (VoteAPI.checkVoteStatus(sender.getName()).equals("1")) {
                        if (VoteAPI.setVote(sender.getName()).equals("1")) {
                            Server.getInstance().broadcastMessage(
                                    Prefix.SUCCESS + TextFormat.GOLD + sender.getName()
                                            + " voted for us and got great rewards!");
                            sender.sendMessage(Prefix.INFO + "Thank you so much for voting for us!");
                            VoteAPI.addVoteCount(sender.getName(), 1);
                            VoteAPI.sendCommands(sender.getName());
                        }
                    } else if (VoteAPI.checkVoteStatus(sender.getName()).equals("2")) {
                        sender.sendMessage(Prefix.ERROR + "You already voted for us today.");
                    }
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }
        }

        return true;
    }
}
