package game.lobby.event;

import game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when the game countdown timer is started.
 */
public class StartCountdownEvent extends Event
{

    private int _seconds;
    private Game _game;
    private static final HandlerList _handlers = new HandlerList();

    /**
     * @param seconds the number of seconds until the countdown ends
     */
    public StartCountdownEvent(Game game, int seconds)
    {
        _seconds = seconds;
        _game = game;
    }

    /**
     * Retrieves the game that will be played once the countdown is completed.
     */
    public Game getGame()
    {
        return _game;
    }

    public int getSeconds(int seconds)
    {
        return _seconds;
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
