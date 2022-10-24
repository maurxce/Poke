package me.lewd.poke.commands;

import me.lewd.poke.Main;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Info implements CommandExecutor {
    private FileConfiguration conf = Main.instance.getDevConf();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return true;
        Audience audience = (Audience) sender;

        if (!sender.hasPermission("poke.info")) return true;

        String primary = conf.getString("colors.primary");
        String secondary = conf.getString("colors.secondary");
        String author = Main.instance.getAuthor();
        String version = Main.instance.getVersion();
        String discord = conf.getString("socials.discord");
        String github = conf.getString("socials.github");

        MiniMessage mm = MiniMessage.miniMessage();
        Component staticText = mm.deserialize(
                String.format("\n<%s>〰〰 [<%s>Poke<%s>] 〰〰\n", secondary, primary, secondary) +
                        String.format("<%s><bold>Author:</bold> <%s>%s\n", secondary, primary, author) +
                        String.format("<%s><bold>Version:</bold> <%s>%s\n", secondary, primary, version)
        );
        Component discordText = mm.deserialize(
                String.format("<%s><bold>Discord:</bold> <%s>[Click]\n", secondary, primary)
        ).clickEvent(ClickEvent.openUrl(discord));
        Component githubText = mm.deserialize(
                String.format("<%s><bold>GitHub:</bold> <%s>[Click]\n", secondary, primary)
        ).clickEvent(ClickEvent.openUrl(github));

        Component message = Component.text()
                .append(staticText)
                .append(discordText)
                .append(githubText)
                .build();

        audience.sendMessage(message);
        return true;
    }
}
