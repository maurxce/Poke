package me.lewd.poke.utils;

import me.lewd.poke.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

public class ChatUtils {
    private FileConfiguration conf = Main.instance.getDevConf();
    private String primary = conf.getString("colors.primary");
    private String secondary = conf.getString("colors.secondary");
    private MiniMessage mm = MiniMessage.miniMessage();

   public Component deserialize(String text) {
        return mm.deserialize(text);
   }

   public Component getPrefixComponent() {
       return mm.deserialize(
               String.format("<%s>[<%s>Poke<%s>] ", secondary, primary, secondary)
       );
   }
}
