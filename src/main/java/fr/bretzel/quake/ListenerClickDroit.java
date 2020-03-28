package fr.bretzel.quake;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import fr.bretzel.quake.game.Game;
import fr.bretzel.quake.game.State;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class ListenerClickDroit extends PacketAdapter
{
    public ListenerClickDroit(Plugin plugin, ListenerPriority listenerPriority, PacketType... types)
    {
        super(plugin, listenerPriority, types);
    }

    @Override
    public void onPacketReceiving(PacketEvent packetEvent)
    {
        if (packetEvent.getPacketType() == PacketType.Play.Client.LOOK || packetEvent.getPacketType() == PacketType.Play.Client.POSITION_LOOK)
        {
            Player player = packetEvent.getPlayer();
            PlayerInfo pi = Quake.getPlayerInfo(player);
            float pitch, yaw;

            yaw = packetEvent.getPacket().getFloat().read(0);
            pitch = packetEvent.getPacket().getFloat().read(1);

            Vector vector = new Vector();

            double rotX = yaw;
            double rotY = pitch;

            vector.setY(-Math.sin(Math.toRadians(rotY)));

            double xz = Math.cos(Math.toRadians(rotY));

            vector.setX(-xz * Math.sin(Math.toRadians(rotX)));
            vector.setZ(xz * Math.cos(Math.toRadians(rotX)));

            pi.setDirection(vector);
        }

        if (packetEvent.getPacketType() == PacketType.Play.Client.BLOCK_PLACE || packetEvent.getPacketType() == PacketType.Play.Client.USE_ITEM)
        {
            Player player = packetEvent.getPlayer();
            PlayerInfo pi = Quake.getPlayerInfo(player);
            Game game = Quake.gameManager.getGameByPlayer(player);
            if (player.hasPermission("quake.event.shoot"))
                if (game != null && game.getState() == State.STARTED)
                    Bukkit.getScheduler().runTask(Quake.quake, pi::shoot);
        }
    }
}
