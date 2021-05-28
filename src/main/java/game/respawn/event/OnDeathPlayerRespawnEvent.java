package game.respawn.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnDeathPlayerRespawnEvent extends Event
{

    private long _respawnRate;
    private Player _player;

    private static final HandlerList _handlers = new HandlerList();

    public OnDeathPlayerRespawnEvent(long defaultRespawnRate, Player player)
    {
        _respawnRate = defaultRespawnRate;
        _player = player;
    }

    public Player getPlayer()
    {
        return _player;
    }

    public long getRespawnRate()
    {
        return _respawnRate;
    }

    public void setRespawnRate(long respawnRate)
    {
        _respawnRate = respawnRate;
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
