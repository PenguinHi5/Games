package game.lobby.event;

import game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when the countdown to start the game is canceled before it reaches 0.
 */
public class CancelCountdownEvent extends Event
{

    private Game _game;

    private static final HandlerList _handlers = new HandlerList();

    public CancelCountdownEvent(Game game)
    {
        _game = game;
    }

    /**
     * Retrieves the game that would have been played if the countdown would have completed.
     */
    public Game getGame()
    {
        return _game;
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
