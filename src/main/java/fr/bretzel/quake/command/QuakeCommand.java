package fr.bretzel.quake.command;

import fr.bretzel.quake.command.partial.game.PartialCommandGame;
import fr.bretzel.quake.command.partial.player.PartialCommandPlayer;
import fr.bretzel.quake.language.JsonBuilder;
import fr.bretzel.quake.util.CommandExe;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class QuakeCommand extends CommandExe {

    @Override
    public boolean onCommand(CommandSender s, org.bukkit.command.Command command, String label, String[] args) {
        if(s instanceof Player) {
            Player sender = (Player) s;
            if(args.length > 0) {
                if (args[0].equalsIgnoreCase("game")) {
                    if(args.length > 1) {
                            return new PartialCommandGame(sender, command, "", args).execute().value();
                    } else {
                        JsonBuilder.sendJson(sender, getI18n("command.game.usage").replace("%game%", "<game>"));
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("players")) {
                    if(args.length > 1) {
                        if (Bukkit.getPlayer(args[1]) != null) {
                            return new PartialCommandPlayer(sender, command, "", args, Bukkit.getPlayer(args[1])).execute().value();
                        } else {
                            sender.sendMessage(ChatColor.RED + "Player can not bee found !");
                            return true;
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Usage: /quake players <player> <quit | join | setcoins | addcoins | removecoins | setkill | setkillsteak | setwon>");
                        return true;
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    for(String str : getHelps()) {
                        sender.sendMessage(str);
                    }
                    return true;
                } else {
                    for (String str : getHelps()) {
                        JsonBuilder.sendJson(sender, str);
                    }
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "/quake help");
                return true;
            }
        } else {
            s.sendMessage(ChatColor.RED + "It must be a player !");
            return true;
        }
    }

    public List<String> getHelps() {
        List list = new ArrayList();
        for(int i = 1; i <= 100; i++) {
            String value = getI18n("command.help." + i);
            if (StringUtils.isNotEmpty(value) && !value.equalsIgnoreCase("command.help." + i))
                list.add(value);
            else
                return list;

        }
        return list;
    }
}