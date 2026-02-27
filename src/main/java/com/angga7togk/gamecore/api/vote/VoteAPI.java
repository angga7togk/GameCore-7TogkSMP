package com.angga7togk.gamecore.api.vote;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jose4j.json.internal.json_simple.parser.JSONParser;

import com.angga7togk.gamecore.Loader;
import com.angga7togk.gamecore.domain.types.Sort;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;

import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.utils.Config;

public class VoteAPI {

    public final static String USER_AGENT = "Mozilla/5.0";
    public static HashMap<String, Integer> cooldown = new HashMap<>();

    private static final Config config, configData;

    static {
        Loader loader = Loader.get();
        loader.saveResource("vote_config.yml");
        loader.saveResource("vote_data.yml");
        config = new Config(loader.getDataFolder() + "/vote_config.yml", Config.YAML);
        configData = new Config(loader.getDataFolder() + "/vote_data.yml", Config.YAML);
    }

    
    public static HashMap<String, Integer> getCooldown() {
        return cooldown;
    }

    
    public static HashMap<String, Integer> getVoteMap() {
        HashMap<String, Integer> voteMap = new HashMap<>();
        try {
            String url = "https://minecraftpocket-servers.com/api/?object=servers&element=voters&key="
                    + config.getString("secret-key") + "&month=current&format=json";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String jsonString = response.toString();
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
            JSONArray voteArray = (JSONArray) jsonObject.get("voters");
            for (Object voter : voteArray) {
                JSONObject playerObject = (JSONObject) parser.parse(voter.toString());
                voteMap.put(playerObject.get("nickname").toString(),
                        Integer.parseInt(playerObject.get("votes").toString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voteMap;
    }

    
    public static String checkVoteStatus(String playername) {
        playername = playername.replace(" ", "");
        String number = null;
        try {
            String url = "https://minecraftpocket-servers.com/api/?object=votes&element=claim&key="
                    + config.getString("secret-key") + "&username=" + playername;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (response.toString().equals("0")) {
                number = "0";
            } else if (response.toString().equals("1")) {
                number = "1";
            } else
                number = "2";
            // System.out.println("LOL: " + number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    
    public static String setVote(String playername) {
        playername = playername.replace(" ", "");
        String number = null;
        try {
            URL url = new URL("https://minecraftpocket-servers.com/api/?action=post&object=votes&element=claim&key="
                    + config.getString("secret-key") + "&username=" + playername);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                if (response.toString().equals("0")) {
                    number = "0";
                } else
                    number = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return number;
    }

    
    public static Integer getVoteCount(String playername) {
        return configData.getInt("all." + playername.toLowerCase(), 0);
    }

    
    public static void setVoteCount(String playername, int amount) {
        configData.set("all." + playername.toLowerCase(), amount);
        configData.save();
    }

    
    public static void addVoteCount(String playername, int amount) {
        configData.set("all." + playername.toLowerCase(), getVoteCount(playername) + amount);
        configData.save();
    }

    
    public static Map<String, Integer> getAll() {
        Map<String, Integer> result = new LinkedHashMap<>();

        if (!configData.exists("all"))
            return result;

        for (String key : configData.getSection("all").getKeys(false)) {
            Integer value = configData.getInt("all." + key, 0);
            result.put(key, value);
        }

        return result;
    }

    
    public static Map<String, Integer> getAllSorted(Sort sort) {
        Map<String, Integer> data = getAll();

        return data.entrySet()
                .stream()
                .sorted((a, b) -> {
                    if (sort == Sort.HIGH_TO_LOW) {
                        return Integer.compare(b.getValue(), a.getValue());
                    } else {
                        return Integer.compare(a.getValue(), b.getValue());
                    }
                })
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        LinkedHashMap::putAll);
    }

    
    public static void sendCommands(String name) {
        for (String commands : config.getStringList("commands")) {
            Server.getInstance().dispatchCommand(new ConsoleCommandSender(), commands.replace("{player}", name));
        }
    }

}
