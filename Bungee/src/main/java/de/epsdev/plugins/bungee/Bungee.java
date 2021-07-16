package de.epsdev.plugins.bungee;

import de.epsdev.bungeeautoserver.api.EPS_API;
import de.epsdev.bungeeautoserver.api.PlayerManager;
import de.epsdev.bungeeautoserver.api.ServerManager;
import de.epsdev.bungeeautoserver.api.ban.Ban;
import de.epsdev.bungeeautoserver.api.config.Config;
import de.epsdev.bungeeautoserver.api.enums.OperationType;
import de.epsdev.bungeeautoserver.api.interfaces.PlayerStatusEmitter;
import de.epsdev.bungeeautoserver.api.interfaces.ServerStatusEmitter;
import de.epsdev.plugins.bungee.commands.c_Instance;
import de.epsdev.plugins.bungee.commands.c_tpToDefault;
import de.epsdev.plugins.bungee.events.PlayerDisconnectFromProxyEvent;
import de.epsdev.plugins.bungee.events.PlayerJoinEvent;
import de.epsdev.plugins.bungee.schedulers.TimeoutScheduler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Bungee extends Plugin {

    public static Plugin plugin;
<<<<<<< Updated upstream
=======
    public static EPS_API eps_api;
    public static Configuration configuration;
>>>>>>> Stashed changes

    @Override
    public void onEnable() {

        plugin = this;

        // Temp. disable updates while developing
        Config.CheckServerVersion = false;

        if(Config.isBungeeServerReady() && Config.checkUpdate("plugins/Bungee",
                "https://ci.eps-dev.de/job/BungeecordAutoConfig-Bungee/lastSuccessfulBuild/artifact/Bungee/target/sha512/",
                "https://ci.eps-dev.de/job/BungeecordAutoConfig-Bungee/lastSuccessfulBuild/artifact/Bungee/target/Bungee.jar")){
            removeAll();

            // Config

            String[] config_params = getConfigParams();

            EPS_API.key = config_params[0];
            EPS_API.DEFAULT_SERVER = config_params[1];

            //Register Events

            getProxy().getPluginManager().registerListener(this,new PlayerJoinEvent());
            getProxy().getPluginManager().registerListener(this,new PlayerDisconnectFromProxyEvent());

            // Register Commands

            ProxyServer.getInstance().getPluginManager().registerCommand(this, new c_Instance());
            ProxyServer.getInstance().getPluginManager().registerCommand(this, new c_tpToDefault(EPS_API.DEFAULT_SERVER));

            // API stuff

            EPS_API eps_api = new EPS_API(OperationType.SERVER);

            // Connection Management

            ServerManager.statusEmitter = new ServerStatusEmitter() {
                @Override
                public void onConnect(String s, InetSocketAddress inetSocketAddress) {
                    addServer(s,inetSocketAddress, "MOTD", false);
                }

                @Override
                public void onDisconnect(String s) {
                    removeServer(s);
                }
            };

            PlayerManager.playerStatusEmitter = new PlayerStatusEmitter() {
                @Override
                public void onPlayerServerChange(String playername, String servername) {
                    ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playername);

                    proxiedPlayer.connect(
                            ProxyServer.getInstance().getServerInfo(servername)
                    );

                    proxiedPlayer.sendMessage(new ComponentBuilder("Sending to " + servername + "!")
                            .color(ChatColor.YELLOW).create());
                }

                @Override
                public void onPlayerBanned(Ban ban) {
                    ArrayList<String> raws = new ArrayList<>();

                    for (Ban b : Ban.bans.values()) {
                        raws.add(b.uuid + ";" + b.until + ";" + b.reason);
                    }

                    configuration.set("bans", raws);
                    try {
                        saveConfig();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            eps_api.init();

            // Cleanup services

            TimeoutScheduler.run();
        }else {
            ProxyServer.getInstance().stop();
        }

    }

    @Override
    public void onDisable() {

    }

    public static void addServer(String name, InetSocketAddress address, String motd, boolean restricted) {
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, address, motd, restricted));
        System.out.println(EPS_API.PREFIX + "Connected " + name + address.getHostName());
    }
    public static void removeServer(String name) {

        ProxyServer.getInstance().getServers().remove(name);
        System.out.println(EPS_API.PREFIX + "Removed " + name);
    }

    public static void removeAll(){
        ArrayList<String> s = new ArrayList<>(ProxyServer.getInstance().getServers().keySet());
        for (String server : s) {
            removeServer(server);
        }
    }

    public String[] getConfigParams() {

        String key = "";
        String default_type = "Hub";

        try {
            if (!getDataFolder().exists())
                getDataFolder().mkdir();

            File file = new File(getDataFolder(), "config.yml");

            if (!file.exists()) {
                file.createNewFile();
            }

            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));

            if(!configuration.contains("bans")) configuration.set("bans", Arrays.asList("uuid;10;UwU"));

            if(configuration.contains("key") && configuration.contains("default_type")){
                key = configuration.getString("key");
                default_type = configuration.getString("default_type");

                List<String> raw_bans = configuration.getStringList("bans");

                for (String raw_ban : raw_bans){
                    String[] fields = raw_ban.split(";");

                    new Ban(fields[0], Integer.parseInt(fields[1]), fields[2]);
                }

            }else {
                configuration.set("key", key);
                configuration.set("default_type", default_type);
            }

            saveConfig();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String[]{key, default_type};
    }

    public static void saveConfig() throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(Bungee.plugin.getDataFolder(), "config.yml"));
    }

}
