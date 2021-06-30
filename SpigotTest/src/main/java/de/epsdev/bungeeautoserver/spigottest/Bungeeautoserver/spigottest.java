package de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver;

import de.epsdev.bungeeautoserver.api.EPS_API;
import de.epsdev.bungeeautoserver.api.enums.OperationType;
import de.epsdev.bungeeautoserver.spigottest.Bungeeautoserver.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class spigottest extends JavaPlugin {

    @Override
    public void onEnable() {
        if(Config.isBungeeReady()){
            EPS_API eps_api = new EPS_API(OperationType.CLIENT);
            eps_api.setRemoteAddress("raspberrypi");
            eps_api.setPort(Bukkit.getPort());

            eps_api.init();
        }else {
            Bukkit.shutdown();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
