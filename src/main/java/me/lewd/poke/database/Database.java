package me.lewd.poke.database;

import me.lewd.poke.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;

public class Database {
    private Nitrite database;
    private NitriteCollection pokes;
    private NitriteCollection userPokeAmount;
    private FileConfiguration config = Main.instance.getConfig();

    public Database() {
        String username = config.getString("database.username");
        String password = config.getString("database.password");

        database = Nitrite.builder()
                .filePath(Main.instance.getDataFolder().getAbsolutePath() + "/data/poke.db")
                .openOrCreate(username, password);

        pokes = database.getCollection("pokes");
        userPokeAmount = database.getCollection("pokeAmount");
    }

    public NitriteCollection getPokesCollection() { return pokes; }
    public NitriteCollection getUserPokeAmountCollection() { return userPokeAmount; }

    public void closeDatabase() {
        if (database.hasUnsavedChanges()) database.commit();
        if (!database.isClosed()) database.close();
    }
}
