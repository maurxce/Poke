package me.lewd.poke.commands;

import me.lewd.poke.Main;
import me.lewd.poke.utils.ChatUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.dizitart.no2.*;
import org.dizitart.no2.filters.Filters;

public class Poke implements CommandExecutor {
    private ChatUtils chatUtils = new ChatUtils();
    private FileConfiguration config = Main.instance.getConfig();
    private FileConfiguration conf = Main.instance.getDevConf();
    private NitriteCollection pokesCollection = Main.instance.getDatabase().getPokesCollection();
    private NitriteCollection pokeAmountCollection = Main.instance.getDatabase().getUserPokeAmountCollection();

    @Override
    public boolean onCommand(CommandSender cmdSender, Command command, String label, String[] args) {
        if (!(cmdSender instanceof Player)) return true;
        if (args.length == 0) return true;

        String senderName = cmdSender.getName();
        Player sender = (Player) cmdSender;
        Audience senderAudience = (Audience) sender;

        if (!sender.hasPermission("poke.poke")) return true;

        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        Audience targetAudience = (Audience) target;

        if (target == sender) return true;

        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");

        String onPokeSent = config.getString("messages.onPoke.sent")
                .replace("{player}", String.format("<%s>%s</%s>", primary, targetName, primary));

        Component senderMessage = chatUtils.deserialize(String.format("<%s>%s", secondary, onPokeSent));
        Component senderResponse = Component.text()
                .append(chatUtils.getPrefixComponent())
                .append(senderMessage).build();
        senderAudience.sendMessage(senderResponse);


        if (target != null && target.isOnline()) {
            String onPokeReceived = config.getString("messages.onPoke.received")
                    .replace("{player}", String.format("<%s>%s</%s>", primary, senderName, primary));

            Component targetMessage = chatUtils.deserialize(String.format("<%s>%s", secondary, onPokeReceived));
            Component targetResponse = Component.text()
                    .append(chatUtils.getPrefixComponent())
                    .append(targetMessage).build();
            targetAudience.sendMessage(targetResponse);
            target.playSound(target.getLocation(), getSound(), 1f, 1f);

            updatePokeAmount(senderName, targetName);
            return true;
        }

        updateCollection(senderName, targetName);
        updatePokeAmount(senderName, targetName);
        return true;
    }

    private String formatPokeMessage(String senderName) {
        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");

        String message = config.getString("messages.onPoke.received")
                .replace("{player}", String.format("<%s>%s</%s>", primary, senderName, primary));

        return String.format("<%s>%s", secondary, message);
    }

    private Sound getSound() {
        String sound = config.getString("pokeSound");

        if (sound.isEmpty()) sound = "ENTITY_EXPERIENCE_ORB_PICKUP";
        return Sound.valueOf(sound);
    }

    private void updateCollection(String sender, String target) {
        Filter filter = Filters.and(
                Filters.eq("sender", sender),
                Filters.eq("target", target)
        );
        Document doc = Document.createDocument("sender", sender)
                .put("target", target);
        UpdateOptions options = UpdateOptions.updateOptions(true);

        pokesCollection.update(filter, doc, options);
    }

    private void updatePokeAmount(String sender, String target) {
        updateSender(sender);
        updateTarget(target);
    }

    private void updateSender(String name) {
        int sentPokes = 0;
        int receivedPokes = 0;

        Filter filter = Filters.eq("username", name);
        Cursor find = pokeAmountCollection.find(filter);
        for (Document doc : find) {
            sentPokes = (int) doc.get("sent");
            receivedPokes = (int) doc.get("received");
        }

        Document doc = Document.createDocument("username", name)
                .put("sent", sentPokes + 1)
                .put("received", receivedPokes);
        UpdateOptions options = UpdateOptions.updateOptions(true);

        pokeAmountCollection.update(filter, doc, options);
    }

    private void updateTarget(String name) {
        int sentPokes = 0;
        int receivedPokes = 0;

        Filter filter = Filters.eq("username", name);
        Cursor find = pokeAmountCollection.find(filter);
        for (Document doc : find) {
            sentPokes = (int) doc.get("sent");
            receivedPokes = (int) doc.get("received");
        }

        Document doc = Document.createDocument("username", name)
                .put("sent", sentPokes)
                .put("received", receivedPokes + 1);
        UpdateOptions options = UpdateOptions.updateOptions(true);

        pokeAmountCollection.update(filter, doc, options);
    }
}
