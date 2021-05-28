package game.gamestate.event;

import game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event
{

    private static final HandlerList _handlers = new HandlerList();
    private Game _game;

    public GameEndEvent(Game game)
    {
        _game = game;
    }

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
