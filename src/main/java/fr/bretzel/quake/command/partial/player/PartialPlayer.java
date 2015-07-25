package fr.bretzel.quake.command.partial.player;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 20/07/15.
 */
public class PartialPlayer extends IPlayer {

    public PartialPlayer(CommandSender sender, Command command, Permission permission, String[] args, Player player) {
        super(sender, command, permission, args, player);
    }

    @Override
    public PartialCommand execute() {
        if(getArgs().length > 2) {
            PlayerInfo info = Quake.getPlayerInfo(getPlayer());
            if(getArgs()[2].equalsIgnoreCase("quit")) {
                if(info.isInGame()) {
                   return new PlayerQuit(getSender(), getCommand(), getPermission(), getArgs(), getPlayer()).execute();
                } else {
                    getSender().sendMessage(ChatColor.RED + "The player is not in a game !");
                    setValue(true);
                    return this;
                }
            } else if(getArgs()[2].equalsIgnoreCase("join")) {
                if(!info.isInGame()) {
                    if(getArgs().length > 3) {
                        if(Quake.gameManager.getGameByName(getArgs()[3]) != null) {
                            return new PlayerJoin(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), Quake.gameManager.getGameByName(getArgs()[3])).execute();
                        } else {
                            getSender().sendMessage(ChatColor.RED + "Not found the game !");
                            return this;
                        }
                    } else {
                        getSender().sendMessage(ChatColor.RED + "Usage: /quake players <player> join <game>");
                        return this;
                    }
                } else {
                    getSender().sendMessage(ChatColor.RED + "The player is already in a game !");
                    return this;
                }
            } else if(getArgs()[2].equalsIgnoreCase("setcoins")) {
                if(getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(ChatColor.RED + getArgs()[3] + " is not a valid number !");
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(ChatColor.RED + "The value must be greater than 0 !");
                        return this;
                    }
                    return new PlayerSetCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(ChatColor.RED + "Usage: /quake players <player> setcoins <coins>");
                    return this;
                }
            } else if(getArgs()[2].equalsIgnoreCase("addcoins")) {
                if(getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(ChatColor.RED + getArgs()[3] + " is not a valid number !");
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(ChatColor.RED + "The value must be greater than 0 !");
                        return this;
                    }
                    return new PlayerSetCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(ChatColor.RED + "Usage: /quake players <player> setcoins <coins>");
                    return this;
                }
            }
        } else {
            getSender().sendMessage(ChatColor.RED + "Usage: /quake players <player> <quit | join | setcoins | addcoins | removecoins | setkill | addkill | removekill | setkillsteak | setwon>");
            setValue(true);
            return this;
        }
        return this;
    }
}
