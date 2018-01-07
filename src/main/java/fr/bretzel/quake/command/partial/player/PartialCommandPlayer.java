package fr.bretzel.quake.command.partial.player;

import fr.bretzel.quake.command.PartialCommand;
import fr.bretzel.quake.PlayerInfo;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.player.coin.CommandPlayerAddCoins;
import fr.bretzel.quake.command.partial.player.coin.CommandPlayerRemoveCoins;
import fr.bretzel.quake.command.partial.player.coin.CommandPlayerSetCoins;
import fr.bretzel.quake.command.partial.player.kill.CommandPlayerAddKill;
import fr.bretzel.quake.command.partial.player.kill.CommandPlayerRemoveKill;
import fr.bretzel.quake.command.partial.player.kill.CommandPlayerSetKill;
import fr.bretzel.quake.command.partial.player.killstreak.CommandPlayerAddKillStreak;
import fr.bretzel.quake.command.partial.player.killstreak.CommandPlayerRemoveKillStreak;
import fr.bretzel.quake.command.partial.player.killstreak.CommandPlayerSetKillStreak;
import fr.bretzel.quake.command.partial.player.win.CommandPlayerAddWin;
import fr.bretzel.quake.command.partial.player.win.CommandPlayerRemoveWin;
import fr.bretzel.quake.command.partial.player.win.CommandPlayerSetWin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by MrBretzel on 20/07/15.
 */
public class PartialCommandPlayer extends ICommandPlayer {

    public PartialCommandPlayer(CommandSender sender, Command command, Permission permission, String[] args, Player player) {
        super(sender, command, permission, args, player);
    }

    @Override
    public PartialCommand execute() {
        if (getArgs().length > 2) {
            PlayerInfo info = Quake.getPlayerInfo(getPlayer());
            if (getArgs()[2].equalsIgnoreCase("quit")) {
                if (info.isInGame()) {
                    return new CommandPlayerQuit(getSender(), getCommand(), getPermission(), getArgs(), getPlayer()).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.playerNotInAGame"));
                    setValue(true);
                    return this;
                }
            }
            if (getArgs()[2].equalsIgnoreCase("join")) {
                if (!info.isInGame()) {
                    if (getArgs().length > 3) {
                        if (Quake.gameManager.getGameByName(getArgs()[3]) != null) {
                            return new CommandPlayerJoin(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), Quake.gameManager.getGameByName(getArgs()[3])).execute();
                        } else {
                            getSender().sendMessage(getI18n("command.players.gameNotFound"));
                            return this;
                        }
                    } else {
                        getSender().sendMessage(getI18n("command.players.usageJoin"));
                        return this;
                    }
                } else {
                    getSender().sendMessage(getI18n("command.players.alReadyInAGame"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setcoins")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (i < 0) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerSetCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.setcoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addcoins")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerAddCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.addcoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removecoins")) {
                if (getArgs().length > 3) {
                    int i;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerRemoveCoins(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setkill")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerSetKill(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.setkill.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addkill")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerAddKill(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removekill")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerRemoveKill(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setkillstreak")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerSetKillStreak(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.setkillstreak.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addkillstreak")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerAddKillStreak(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removekillstreak")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerRemoveKillStreak(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.removecoins.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("setwon")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerSetWin(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.setwon.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("addwon")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerAddWin(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.addwon.usage"));
                    return this;
                }
            } else if (getArgs()[2].equalsIgnoreCase("removewon")) {
                if (getArgs().length > 3) {
                    int i = 0;
                    try {
                        i = Integer.valueOf(getArgs()[3]);
                    } catch (Exception e) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber").replace("%number%", getArgs()[3]));
                        return this;
                    }
                    if (!(i > 0)) {
                        getSender().sendMessage(getI18n("command.players.notAValidNumber"));
                        return this;
                    }
                    return new CommandPlayerRemoveWin(getSender(), getCommand(), getPermission(), getArgs(), getPlayer(), i).execute();
                } else {
                    getSender().sendMessage(getI18n("command.players.removewon.usage"));
                    return this;
                }
            } else {
                getSender().sendMessage(getI18n("command.players.usage"));
                setValue(true);
                return this;
            }
        } else {
            getSender().sendMessage(getI18n("command.players.usage"));
            setValue(true);
            return this;
        }
    }
}
