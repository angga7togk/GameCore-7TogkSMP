package com.angga7togk.gamecore.api.economy;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.enums.Sort;
import com.angga7togk.gamecore.utils.OKE;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;

public class EconomyAPI {
    /*
     * =========================
     * DATA STORAGE (sementara)
     * =========================
     */

    // wallet player
    private static final Map<String, Long> wallets = new HashMap<>();

    private static final Config config;

    public final static Long DEFAULT_MONEY = 20000L;

    static {
        Loader loader = Loader.get();
        loader.saveResource("economy_data.yml");
        config = new Config(loader.getDataFolder() + "/economy_data.yml", Config.YAML);
        loadFromConfig();

    }

    public static void createWallet(Player player) {
        wallets.putIfAbsent(player.getName().toLowerCase(), DEFAULT_MONEY);
    }

    /*
     * =========================
     * PLAYER WALLET
     * =========================
     */

    public static boolean exists(Player player) {
        return exists(player.getName());
    }

    public static boolean exists(String playerName) {
        return wallets.containsKey(playerName.toLowerCase());
    }

    public static long getBalance(Player player) {
        return getBalance(player.getName());
    }

    public static long getBalance(String playerName) {
        return wallets.getOrDefault(playerName.toLowerCase(), DEFAULT_MONEY);
    }

    public static void setBalance(Player player, long amount) {
        setBalance(player.getName(), amount);
    }

    public static void setBalance(String playerName, long amount) {
        if (amount < 0)
            amount = 0;
        wallets.put(playerName.toLowerCase(), amount);
    }

    public static OKE<String> addBalance(Player player, long amount) {
        return addBalance(player.getName(), amount);
    }

    public static OKE<String> addBalance(String playerName, long amount) {
        if (amount <= 0) {
            return new OKE<>(false, "Tidak bisa menambahkan uang di bawah nol!");
        }

        String key = playerName.toLowerCase();
        wallets.put(key, getBalance(key) + amount);
        return new OKE<>(true);
    }

    public static OKE<String> reduceBalance(String playerName, long amount) {
        if (amount <= 0) {
            return new OKE<>(false, "Tidak bisa mengurangi uang di bawah nol!");
        }

        String key = playerName.toLowerCase();
        long balance = getBalance(key);

        if (balance < amount) {
            return new OKE<>(false, "Maaf uang kamu belum mencukupi!");
        }

        wallets.put(key, balance - amount);
        return new OKE<>(true);
    }

    public static boolean hasBalance(String playerName, long amount) {
        return getBalance(playerName) >= amount;
    }

    public static Map<String, Long> getAll() {
        return wallets;
    }

    public static Map<String, Long> getAllSorted(Sort sort) {
        return wallets.entrySet()
                .stream()
                .sorted((a, b) -> {
                    int cmp = Long.compare(a.getValue(), b.getValue());
                    return sort == Sort.HIGH_TO_LOW ? -cmp : cmp;
                })
                .collect(
                        java.util.stream.Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                java.util.LinkedHashMap::new));
    }

    /*
     * =========================
     * TRANSFER
     * =========================
     */

    public static OKE<String> transfer(String from, String to, long amount) {
        if (amount <= 0)
            return new OKE<>(false, "Tidak bisa transfer uang di bawah nol!");

        OKE<String> reduceBalance = reduceBalance(from, amount);
        if (!reduceBalance.ok()) {
            return reduceBalance;
        }

        addBalance(to, amount);
        return new OKE<>();
    }

    /*
     * =========================
     * GLOBAL ECONOMY
     * =========================
     */

    public static long getTotalMoney() {
        long total = 0;
        for (long balance : wallets.values()) {
            total += balance;
        }
        return total;
    }

    /*
     * =========================
     * ECONOMY UTILS
     * =========================
     */
    public static String format(long value, Locale locale) {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        format.setMaximumFractionDigits(0); // no .00
        return format.format(value);
    }

    public static String format(long value) {
        return format(value, new Locale("id", "ID"));
    }

    /*
     * =========================
     * MORE ECONOMY API
     * =========================
     */

    private static void loadFromConfig() {
        for (String key : config.getSection("players").getKeys()) {
            wallets.put(key, config.getLong("players." + key, DEFAULT_MONEY));
        }
    }

    public static void saveToConfig() {
        for (Map.Entry<String, Long> entry : wallets.entrySet()) {
            config.set("players." + entry.getKey(), entry.getValue());
        }
        config.save();
    }

}
