package me.lewd.poke.listeners;

import me.lewd.poke.Main;
import me.lewd.poke.utils.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.dizitart.no2.Cursor;
import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;

public class PlayerJoinListener implements Listener {
    private ChatUtils chatUtils = new ChatUtils();
    private FileConfiguration config = Main.instance.getConfig();
    private FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection pokesCollection = Main.instance.getDatabase().getPokesCollection();
    private NitriteCollection pokeAmountCollection = Main.instance.getDatabase().getUserPokeAmountCollection();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        checkPokeCounter(player);
        checkPokes(player);
    }

    private void checkPokeCounter(Player player) {
        Filter filter = Filters.eq("username", player.getName());
        Cursor find = pokeAmountCollection.find(filter);

        if (find.size() <= 0) {
            Document doc = Document.createDocument("username", player.getName())
                    .put("sent", 0)
                    .put("received", 0);

            pokeAmountCollection.insert(doc);
        }
    }

    private void checkPokes(Player player) {
        Audience audience = (Audience) player;

        Filter filter = Filters.eq("target", player.getName());
        int pokeAmount = pokesCollection.find(filter).size();
        if (pokeAmount <= 0 ) return;

        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");

        String onJoin = config.getString("messages.onJoin")
                .replace("{amount}", String.format("<%s>%d</%s>", primary, pokeAmount, primary));
        Component message = chatUtils.deserialize(String.format("<%s>%s", secondary, onJoin));

        Component response = Component.text()
                .append(chatUtils.getPrefixComponent())
                .append(message)
                .clickEvent(ClickEvent.runCommand("/pokes"))
                .build();

        audience.sendMessage(response);
    }
}
