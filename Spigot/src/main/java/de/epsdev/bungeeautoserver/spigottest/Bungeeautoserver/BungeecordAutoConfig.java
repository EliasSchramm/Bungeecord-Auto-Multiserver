package de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver;

import de.epsdev.bungeeautoserver.api.EPS_API;
import de.epsdev.bungeeautoserver.api.ServerManager;
import de.epsdev.bungeeautoserver.api.config.Config;
import de.epsdev.bungeeautoserver.api.enums.OperationType;
import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.commands.*;

import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.config.GUI_Config;
import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.events.e_InventoryChangeEvent;
import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.events.e_OnBlockInteract;
import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.events.e_OnSignChange;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BungeecordAutoConfig extends JavaPlugin {
    public static FileConfiguration config;
    public static EPS_API eps_api;
    public static JavaPlugin plugin = null;

    @Override
    public void onEnable() {
        config = getConfig();
        plugin = this;

        // Temp. disable updates while developing

        Config.CheckServerVersion = false;

        init_config();

        SignManager.loadAllSigns();
        SignManager.startSignUpdateScheduler();

        // Commands

        getCommand("changeserver").setExecutor(new c_ChangeServer());
        getCommand("changeinstance").setExecutor(new c_ChangeInstance());
        getCommand("getServerInfo").setExecutor(new c_getServerInfo());
        getCommand("closeserver").setExecutor(new c_closeserver());
        getCommand("openserver").setExecutor(new c_openserver());
        getCommand("menu").setExecutor(new c_menu());

        // Events

        getServer().getPluginManager().registerEvents(new e_OnSignChange(), this);
        getServer().getPluginManager().registerEvents(new e_OnBlockInteract(), this);
        getServer().getPluginManager().registerEvents(new e_InventoryChangeEvent(), this);

        // Updatess

        if(Config.isBungeeReady() && Config.checkUpdate("plugins/BungeecordAutoConfig",
                "https://ci.eps-dev.de/job/BungeecordAutoConfig-Spigot/lastSuccessfulBuild/artifact/Spigot/target/sha512/",
                "https://ci.eps-dev.de/job/BungeecordAutoConfig-Spigot/lastSuccessfulBuild/artifact/Spigot/target/BungeecordAutoConfig.jar")){

            eps_api = new EPS_API(OperationType.CLIENT);
            eps_api.setRemoteAddress(config.getString("bungee_address"));
            eps_api.setPort(Bukkit.getPort());
            eps_api.setMax_players(Bukkit.getMaxPlayers());
            eps_api.setType(config.getString("server_type"));

            EPS_API.key = config.getString("bungee_password");

            eps_api.init();

            GUI_Config.init();

        }else {
            Bukkit.shutdown();
        }
    }

    @Override
    public void onDisable() {
        if(eps_api != null) eps_api.disable();
    }

    public void init_config(){
        config.addDefault("bungee_address", "0.0.0.0");
        config.addDefault("bungee_password", "");
        config.addDefault("server_type", "Hub");
        config.addDefault("signs", new String[0]);

        config.options().copyDefaults(true);

        saveConfig();
    }
}
