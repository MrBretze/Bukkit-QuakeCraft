package fr.bretzel.quake.command.partial.game.respawn;

import fr.bretzel.quake.Quake;
import fr.bretzel.quake.util.PartialCommand;
import fr.bretzel.quake.command.partial.game.ICommandGame;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.hologram.Hologram;
import fr.bretzel.quake.language.JsonBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;

public class AddRespawn extends ICommandGame {

    public AddRespawn(CommandSender sender, Command command, Permission permission, String[] args, Game game) {
        super(sender, command, permission, args, game);
    }

    @Override
    public PartialCommand execute() {
        if (getGame().hasRespawn(getPlayer().getLocation())) {
            JsonBuilder.sendJson(getPlayer(), getI18n("command.game.addrespawn.error"));
            return this;
        }
        getGame().addRespawn(getPlayer().getLocation());
        int s = getGame().getRespawns().size();

        if (getGame().isView()) {
            Hologram hologram = new Hologram(getPlayer().getLocation(), "Respawn: " + s, Quake.holoManager);
            hologram.display(true);
        }

        JsonBuilder.sendJson(getPlayer(), getI18n("command.game.addrespawn.valid").replace("%value%", "" + s));
        return this;
    }
}
