package game.respawn.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnRespawnPlayerRespawnEvent extends Event
{

    private Player _player;

    private static final HandlerList _handlers = new HandlerList();

    public OnRespawnPlayerRespawnEvent(Player player)
    {
        _player = player;
    }

    public Player getPlayer()
    {
        return _player;
    }

    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList()
    {
        return _handlers;
    }

}
