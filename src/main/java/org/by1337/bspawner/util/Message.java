package org.by1337.bspawner.util;



import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;


import java.util.Objects;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.by1337.bspawner.BSpawner;

public class Message {

    private static final ConsoleCommandSender SENDER = Bukkit.getConsoleSender();
    private static final String AUTHOR = "&#a612cb&lB&#9a17d2&ly&#8d1bd9&l1&#8120e1&l3&#7424e8&l3&#6829ef&l7";
    private static final String prefixPlugin = "&7[&#a612cbA&#9c16d1i&#911ad7r&#871eddD&#7d21e3r&#7225e9o&#6829efp&7]";
    private static final String Prefix = "&7[Спавнер]";
    public static final Pattern RAW_HEX_REGEX = Pattern.compile("&(#[a-f0-9]{6})", Pattern.CASE_INSENSITIVE);


    public static void sendMsg(Player pl, String msg) {
        if(pl.isOnline())
            pl.sendMessage(messageBuilder(msg));
    }

    public static void logger(String msg) {
        SENDER.sendMessage(messageBuilder(msg));
    }

    public static void error(String msg) {
        BSpawner.instance.getLogger().log(Level.SEVERE, msg);
        sendAllOp(msg.replace("{PP}", prefixPlugin + " &#cb2d3e[&#d1313dE&#d7363dR&#dd3a3cR&#e33e3bO&#e9433bR&#ef473a]&c"));

    }

    public static void sendAllOp(String msg){
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.isOp()){
                sendMsg(pl, "&c" + messageBuilder(msg));
            }
        }
    }
    public static void warning(String msg) {
        BSpawner.instance.getLogger().warning(messageBuilder(msg));

    }

    public static void sendActionBar(Player pl, String msg){
        pl.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(messageBuilder(msg)));
    }
    public static void sendTitle(Player pl, String title,String subTitle, int fadeIn, int stay, int fadeOut){
        pl.sendTitle(messageBuilder(title), messageBuilder(subTitle), fadeIn, stay, fadeOut);
    }
    public static void sendTitle(Player pl, String title,String subTitle){
        pl.sendTitle(messageBuilder(title), messageBuilder(subTitle), 10, 20, 10);
    }
    public static void sendAllTitle(String title, String subTitle){
        for(Player pl : Bukkit.getOnlinePlayers())
            pl.sendTitle(messageBuilder(title), messageBuilder(subTitle), 20, 30, 20);
    }
    public static String messageBuilder(String msg) {
        if(msg == null)
            return "";
        String str = msg.replace("{PP}", prefixPlugin).replace("AU", AUTHOR).replace("{px}", Prefix);
        str = setPlaceholders(null, str);
        return  hex(str);
    }

    public static void sendAllMsg(String msg) {
        for (Player pl : Bukkit.getOnlinePlayers())
            pl.sendMessage(messageBuilder(msg));
    }
    public static void sendAllNear(String msg, Location loc){
        for(Entity entity : Objects.requireNonNull(loc.getWorld()).getNearbyEntities(loc, 10, 10, 10)){
            if(entity instanceof Player){
                entity.sendMessage(messageBuilder(msg));
            }
        }
    }

    public static String setPlaceholders(Player player, String string) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            try {
                return PlaceholderAPI.setPlaceholders(player, string.replace("&", "§")).replace("§", "&");
            } catch (Exception var3) {
            }
        }

        return string;
    }

    private static String hex(String message) {
        Matcher m = RAW_HEX_REGEX.matcher(message);
        while (m.find())
            message = message.replace(m.group(), ChatColor.of(m.group(1)).toString());
        return ChatColor.translateAlternateColorCodes('&', message);
    }

}