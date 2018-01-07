package fr.bretzel.quake.command;

import fr.bretzel.quake.language.JsonBuilder;
import fr.bretzel.quake.language.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 19/07/15.
 */

public abstract class PartialCommand {

    private Command command;
    private CommandSender sender;
    private Permission permission;
    private String[] args;
    private Player player;
    private boolean value = true;
    private boolean isplayer = false;

    public PartialCommand(CommandSender sender, Command command, Permission permission, String[] args) {
        this.sender = sender;
        this.command = command;
        this.permission = permission;
        this.args = args;
        if (sender instanceof Player) {
            isplayer = true;
            this.player = (Player) sender;
        }
    }

    public PartialCommand execute() {
        return this;
    }

    public Command getCommand() {
        return command;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Permission getPermission() {
        return permission;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean value() {
        return this.value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getI18n(String key) {
        return Language.defaultLanguage.get(key);
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPlayer() {
        return isplayer;
    }

    public void sendMessage(String str) {
        if (isplayer) {
            JsonBuilder.sendJson(getPlayer(), str);
        }
    }
}
