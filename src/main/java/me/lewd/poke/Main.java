package me.lewd.poke;

import me.lewd.poke.commands.Info;
import me.lewd.poke.commands.List;
import me.lewd.poke.commands.Poke;
import me.lewd.poke.database.Database;
import me.lewd.poke.listeners.PlayerJoinListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public final class Main extends JavaPlugin {
    public static Main instance;
    public Database database;

    private FileConfiguration config = new YamlConfiguration();
    private FileConfiguration conf = new YamlConfiguration();

    @Override
    public void onEnable() {
        instance = this;

        initializeFiles();
        database = new Database();

        registerCommands();
        registerEvents();

        loadExpansions();
    }

    private void initializeFiles() {
        File dataDir = new File(getDataFolder(), "data");
        InputStreamReader confFile = new InputStreamReader(getClassLoader().getResourceAsStream("conf.yml"));
        File configFile = new File(getDataFolder(), "config.yml");

        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        if (!dataDir.exists()) dataDir.mkdirs();
        if (!configFile.exists()) saveResource("config.yml", false);

        try {
            conf.load(confFile);
            config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private void registerCommands() {
        getCommand("poke").setExecutor(new Poke());
        getCommand("pokes").setExecutor(new List());
        getCommand("info").setExecutor(new Info());
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
    }

    private void loadExpansions() {
        // bStats
        int pluginId = 16691;
        Metrics metrics = new Metrics(this, pluginId);
    }

    public FileConfiguration getDevConf() { return conf; }
    public Database getDatabase() { return database; }
    public String getAuthor() { return getDescription().getAuthors().get(0); }
    public String getVersion() { return getDescription().getVersion(); }

    @Override
    public void onDisable() {
        instance = null;
        database.closeDatabase();
    }
}

// https://www.reddit.com/r/admincraft/comments/y2bkka/comment/is39dy4/?utm_source=share&utm_medium=web2x&context=3