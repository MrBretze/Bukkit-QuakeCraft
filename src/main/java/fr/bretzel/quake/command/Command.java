package fr.bretzel.quake.command;

import com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Created by MrBretzel on 14/06/2015.
 */
public class Command implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {

        } else {
            commandSender.sendMessage(ChatColor.RED + "Player ?");
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        return ImmutableList.of();
    }
}
