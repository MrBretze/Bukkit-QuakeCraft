package fr.bretzel.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

/**
 * Created by mrbretzel on 19/07/15.
 */

public abstract class PartialCommand {

    private Command command;
    private CommandSender sender;
    private Permission permission;
    private String[] args;
    private boolean value = true;

    public PartialCommand(CommandSender sender, Command command, Permission permission, String[] args) {
        this.sender = sender;
        this.command = command;
        this.permission = permission;
        this.args = args;
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
}
