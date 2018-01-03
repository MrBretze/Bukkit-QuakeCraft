package fr.bretzel.quake.command.partial.player;

import fr.bretzel.commands.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.coin.PlayerAddCoins;
import fr.bretzel.quake.command.partial.player.coin.PlayerRemoveCoins;
import fr.bretzel.quake.command.partial.player.coin.PlayerSetCoins;
import fr.bretzel.quake.command.partial.player.kill.PlayerAddKill;
import fr.bretzel.quake.command.partial.player.kill.PlayerRemoveKill;
import fr.bretzel.quake.command.partial.player.kill.PlayerSetKill;
import fr.bretzel.quake.command.partial.player.killstreak.PlayerAddKillStreak;
import fr.bretzel.quake.command.partial.player.killstreak.PlayerRemoveKillStreak;
import fr.bretzel.quake.command.partial.player.killstreak.PlayerSetKillStreak;
import fr.bretzel.quake.command.partial.player.won.PlayerAddWon;
import fr.bretzel.quake.command.partial.player.won.PlayerRemoveWon;
import fr.bretzel.quake.command.partial.player.won.PlayerSetWon;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 20/07/15.
 */
public class PartialPlayer extends IPlayer {

    public PartialPlayer(CommandSender sender, Command command, Permission permission, String[] args, Player player) {
        super(sender, command, permission, args, player);
    }

    @Override
    public PartialCommand execute() {
        if (getArgs().length > 2) {
            PlayerInfo info = Quake.getPlayerInfo(getPlayer());
            if (getArgs()[2].equalsIgnoreCase("quit")) {
                if (info.isInGame()) {
                    return new PlayerQuit(getSender(), getCommand(), getPermission(), getArgs(), getPlayer()).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.playerNotInAGame"));
                    setValue(true);
                    return this;
                }
            }

            if (!getSender().hasPermission("quake.players")) {
                getSender().sendMessage("You dont have the permission !");
                return this;
            }

            if (getArgs()[2].equalsIgnoreCase("join")) {
                if (!info.isInGame()) {
                    if (getArgs().length > 3) {
                        if (Quake.gameManager.getGameByName(getArgs()[3]) != null) {
                            return new PlayerJoin(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), Quake.gameManager.getGameByName(getArgs()[3])).execute();
                        } else {
                            getSender().sendMessage(getI18("command.players.gameNotFound"));
                            return this;
                        }
                    } else {
                        getSender().sendMessage(getI18("command.players.usageJoin"));
                        return this;
                    }
                } else {
                    getSender().sendMessage(getI18("command.players.alReadyInAGame"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setcoins")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerSetCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.setcoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addcoins")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerAddCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.addcoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removecoins")) {
                if (getArgs().length > 3) {
                    int i;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerRemoveCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setkill")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerSetKill(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.setkill.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addkill")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerAddKill(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removekill")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerRemoveKill(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setkillstreak")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerSetKillStreak(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.setkillstreak.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addkillstreak")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerAddKillStreak(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removekillstreak")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerRemoveKillStreak(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setwon")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerSetWon(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.setwon.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addwon")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerAddWon(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.addwon.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removewon")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18("command.players.notAValidNumber"));
                        return this;
                    }
                    return new PlayerRemoveWon(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18("command.players.removewon.usage"));
                    return this;
                }
            } else {
                getSender().sendMessage(getI18("command.players.usage"));
                setValue(true);
                return this;
            }
        } else {
            getSender().sendMessage(getI18("command.players.usage"));
            setValue(true);
            return this;
        }
    }
}
