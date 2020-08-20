package game.gamestate.event;

import game.gamestate.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStateChangeEvent extends Event {

    private static final HandlerList _handlers = new HandlerList();
    private GameState _previousState;
    private GameState _newState;

    public GameStateChangeEvent(GameState previousState, GameState newState)
    {
        _previousState = previousState;
        _newState = newState;
    }

    /**
     * Returns the new GameState.
     */
    public GameState getNewGameState()
    {
        return _newState;
    }

    /**
     * Returns the previous GameState.
     */
    public GameState getPreviousGameState()
    {
        return _previousState;
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
