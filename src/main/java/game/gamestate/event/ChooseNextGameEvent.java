package game.gamestate.event;

import game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChooseNextGameEvent extends Event
{

    private Game _nextGame;
    private static final HandlerList _handlers = new HandlerList();

    public ChooseNextGameEvent(Game nextGame)
    {
        _nextGame = nextGame;
    }

    public Game getNextGame()
    {
        return _nextGame;
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
