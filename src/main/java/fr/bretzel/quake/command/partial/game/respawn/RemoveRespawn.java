package fr.bretzel.quake.command.partial.game.respawn;

import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.Quake;
import fr.bretzel.quake.command.partial.game.ICommandGame;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class RemoveRespawn extends ICommandGame {

    public RemoveRespawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (!getGame().hasRespawn(getPlayer().getEyeLocation())) {
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.removerespawn.error"));
            return this;
        }

        if (getGame().isView())
            Quake.holoManager.removeHologram(getGame().getRespawn(getPlayer().getEyeLocation()));

        getGame().removeRespawn(getPlayer().getEyeLocation());
        JsonBuilder.sendJson(getPlayer(), getI18n("command.game.removerespawn.valid"));
        return this;
    }
}
