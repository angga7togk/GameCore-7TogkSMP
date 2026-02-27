package com.angga7togk.gamecore.api.placeholder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.angga7togk.gamecore.api.clover.CloverAPI;
import com.angga7togk.gamecore.api.economy.EconomyAPI;
import com.angga7togk.gamecore.api.jumprecord.JumpAPI;
import com.angga7togk.gamecore.api.level.LevelAPI;
import com.angga7togk.gamecore.api.placeholder.holder.Placeholder;
import com.angga7togk.gamecore.api.playtime.PlaytimeAPI;
import com.angga7togk.gamecore.api.vote.VoteAPI;
import com.angga7togk.gamecore.utils.TimeUtils;
import com.angga7togk.gamecore.utils.Utils;

import cn.nukkit.IPlayer;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.network.protocol.ProtocolInfo;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

public class PlaceholderAPI {
    private  static final Map<String, Placeholder> placeholders = new HashMap<>();

    private  static final String PARAM_SEPARATOR = ";";

    private  static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
            "%([a-zA-Z0-9_]+)(?:\\" + PARAM_SEPARATOR + "([^%]*))?%");


    static {
        registerDefaults();
    }

   
    public static void register(String identifier, Placeholder placeholder) {
        placeholders.put(identifier.toLowerCase(), placeholder);
    }

   
    public static String translate(Player player, String text) {

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String identifier = matcher.group(1).toLowerCase();
            String paramsRaw = matcher.group(2);

            Placeholder placeholder = placeholders.get(identifier);
            if (placeholder != null) {
                String[] params = paramsRaw != null ? paramsRaw.split(PARAM_SEPARATOR) : new String[0];
                String replacement = placeholder.process(player, params);
                matcher.appendReplacement(
                        result,
                        Matcher.quoteReplacement(replacement != null ? replacement : ""));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }

    private static void registerDefaults() {
        register("player", (player, params) -> player.getName());
        register("player_displayname", (player, params) -> player.getDisplayName());
        register("player_nametag", (player, params) -> player.getNameTag());
        register("player_uuid", (player, params) -> player.getUniqueId().toString());
        register("player_ping", (player, params) -> String.valueOf(player.getPing()));
        register("player_ping_v2", (player, params) -> Utils.getPing(player));
        register("player_level", (player, params) -> player.getLevel().getName());
        register("player_health", (player, params) -> String.valueOf(player.getHealth()));
        register("player_max_health", (player, params) -> String.valueOf(player.getMaxHealth()));
        register("player_saturation",
                (player, params) -> String.valueOf(player.getFoodData().getFoodSaturationLevel()));
        register("player_food", (player, params) -> String.valueOf(player.getFoodData().getLevel()));
        register("player_max_food", (player, params) -> String.valueOf(player.getFoodData().getMaxLevel()));
        register("player_gamemode", (player, params) -> String.valueOf(player.getGamemode()));
        register("player_item", (player, params) -> player.getInventory().getItemInHand().getName());
        register("player_offhand", (player, params) -> player.getOffhandInventory().getItem(0).getName());
        register("player_exp", (player, params) -> String.valueOf(player.getExperience()));
        register("player_exp_level", (player, params) -> String.valueOf(player.getExperienceLevel()));
        register("player_version", (player, params) -> String.valueOf(player.getGameVersion().toString()));
        register("player_protocol", (player, params) -> String.valueOf(player.getGameVersion().getProtocol()));

        register("server_online",
                (player, params) -> String.valueOf(Server.getInstance().getOnlinePlayers().size()));
        register("server_max_players", (player, params) -> String.valueOf(Server.getInstance().getMaxPlayers()));
        register("server_motd", (player, params) -> Server.getInstance().getMotd());
        register("server_tps", (player, params) -> String.valueOf(Server.getInstance().getTicksPerSecond()));
        register("server_tps_v2", (player, params) -> Utils.getTPS());
        register("server_tick", (player, params) -> String.valueOf(Server.getInstance().getTick()));
        register("server_difficulty", (player, params) -> String.valueOf(Server.getInstance().getDifficulty()));
        register("server_version", (player, params) -> ProtocolInfo.MINECRAFT_VERSION_NETWORK);
        register("server_protocol", (player, params) -> String.valueOf(ProtocolInfo.CURRENT_PROTOCOL));
        register("player_pos", (p, arg) -> {
            if (arg.length == 0)
                return p.getFloorX() + " " + p.getFloorY() + " " + p.getFloorZ();
            return switch (arg[0]) {
                case "x" -> String.valueOf(p.getFloorX());
                case "y" -> String.valueOf(p.getFloorY());
                case "z" -> String.valueOf(p.getFloorZ());
                default -> "unknown";
            };
        });
        register("time", (player, params) -> {
            Date now = new Date();

            // default
            String format = "yyyy-MM-dd HH:mm:ss";
            TimeZone timeZone = TimeZone.getDefault();

            if (params.length > 0 && !params[0].isEmpty()) {
                format = params[0];
            }

            if (params.length > 1 && !params[1].isEmpty()) {
                timeZone = TimeZone.getTimeZone(params[1]);
            }

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setTimeZone(timeZone);
            return sdf.format(now);
        });

        /** Luckperms Placeholders */
        if (Server.getInstance().getPluginManager().getPlugin("LuckPerms") != null) {

            LuckPerms luckPerms = LuckPermsProvider.get();

            register("luckperms_prefix", (player, params) -> {
                UUID uuid = null;
                if (params.length < 1) {
                    uuid = player.getUniqueId();
                } else {
                    IPlayer p = Server.getInstance().getOfflinePlayer(params[0]);
                    if (p != null) {
                        uuid = p.getUniqueId();
                    }
                }
                if (uuid == null)
                    return "";

                var user = luckPerms.getUserManager().getUser(uuid);
                if (user == null)
                    return "";

                String prefix = user.getCachedData()
                        .getMetaData(luckPerms.getContextManager().getQueryOptions(player))
                        .getPrefix();

                return prefix != null ? prefix : "";
            });

            register("luckperms_suffix", (player, params) -> {
                UUID uuid = null;
                if (params.length < 1) {
                    uuid = player.getUniqueId();
                } else {
                    IPlayer p = Server.getInstance().getOfflinePlayer(params[0]);
                    if (p != null) {
                        uuid = p.getUniqueId();
                    }
                }
                if (uuid == null)
                    return "";

                var user = luckPerms.getUserManager().getUser(uuid);
                if (user == null)
                    return "";

                String suffix = user.getCachedData()
                        .getMetaData(luckPerms.getContextManager().getQueryOptions(player))
                        .getSuffix();

                return suffix != null ? suffix : "";
            });

            register("luckperms_primary_group", (player, params) -> {
                UUID uuid = null;
                if (params.length < 1) {
                    uuid = player.getUniqueId();
                } else {
                    IPlayer p = Server.getInstance().getOfflinePlayer(params[0]);
                    if (p != null) {
                        uuid = p.getUniqueId();
                    }
                }
                if (uuid == null)
                    return "";

                var user = luckPerms.getUserManager().getUser(uuid);
                return user != null ? user.getPrimaryGroup() : "";
            });
        }

        // Default economy
        register("economy_balance", (player, params) -> {

            long balance = EconomyAPI.getBalance(player.getName());
            if (params.length > 1) {
                try {
                    String formatted = EconomyAPI.format(balance, new Locale(params[0], params[1]));
                    return formatted;
                } catch (Exception e) {
                    return String.valueOf(balance);
                }
            } else {
                return String.valueOf(balance);
            }
        });

        register("level_color",
                (player, params) -> LevelAPI.getLeveL(player.getName()).getLevelColor());
        register("level",
                (player, params) -> String.valueOf(LevelAPI.getLeveL(player.getName()).getLevel()));
        register("level_xp",
                (player, params) -> String.valueOf(LevelAPI.getLeveL(player.getName()).getXp()));
        register("level_xp_goal",
                (player, params) -> String.valueOf(LevelAPI.getLeveL(player.getName()).getGoalXp()));
        register("level_xp_percentage",
                (player, params) -> String.valueOf(LevelAPI.getLeveL(player.getName()).getXpPercentage()));
        register("level_xp_multiplier",
                (player, params) -> String.valueOf(LevelAPI.getLeveL(player.getName()).getXpMultiplier()));

        register("jumprecord",
                (player, params) -> String.valueOf(JumpAPI.getJump(player.getName())));

        register("votes", (player, params) -> String.valueOf(VoteAPI.getVoteCount(player.getName())));

        register("clover", (player, params) -> String.valueOf(CloverAPI.getClover(player.getName())));

        register("playtime", (player, params) -> {

            long playtime = PlaytimeAPI.getTime(player.getName());

            if (params.length < 1) {
                return String.valueOf(TimeUtils.getTotalHours(playtime));
            }

            return switch (params[0].toLowerCase()) {
                case "seconds" -> String.valueOf(TimeUtils.getSeconds(playtime));
                case "minutes" -> String.valueOf(TimeUtils.getMinutes(playtime));
                case "hours" -> String.valueOf(TimeUtils.getHours(playtime));
                case "days" -> String.valueOf(TimeUtils.getDays(playtime));

                case "total_seconds" -> String.valueOf(TimeUtils.getTotalSeconds(playtime));
                case "total_minutes" -> String.valueOf(TimeUtils.getTotalMinutes(playtime));
                case "total_hours" -> String.valueOf(TimeUtils.getTotalHours(playtime));
                case "total_days" -> String.valueOf(TimeUtils.getTotalDays(playtime));

                default -> "0";
            };
        });

    }
}
