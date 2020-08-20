package game;

import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.world.WorldManager;
import core.minecraft.world.config.MapConfig;
import game.gamestate.GameState;
import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import game.gamestate.event.GameStateChangeEvent;
import game.lobby.LobbyManager;
import game.scoreboard.ScoreboardController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GameManager extends Component
{
    private TransactionManager _transactionManager;
    private WorldManager _worldManager;
    private ScoreManager _scoremanager;
    private LobbyManager _lobbyManager;
    private GameState _gameState;
    private Game _currentGame;
    private MapConfig _lobbyMap;

    public GameManager(JavaPlugin plugin, CommandManager commandManager, ScoreManager scoreManager, TransactionManager transactionManager, WorldManager worldManager)
    {
        super("game.Game Manager", plugin, commandManager);

        _scoremanager = scoreManager;
        _transactionManager = transactionManager;
        _worldManager = worldManager;
        _lobbyManager = new LobbyManager(plugin, commandManager, this, worldManager, scoreManager, transactionManager);
        // The lobby should be the default world
        _lobbyMap = worldManager.getActiveMap();
        setGameState(GameState.LOBBY);
    }

    public void startTimer(int seconds)
    {

    }

    public void setCurrentGame(Game game)
    {
        _currentGame = game;
    }

    public Game getCurrentGame()
    {
        return _currentGame;
    }

    public void endGame(String winnerName, Player[] winners)
    {
        _gameState = GameState.POST_GAME;
    }

    public GameState getGameState()
    {
        return _gameState;
    }

    public MapConfig getLobbyMap()
    {
        return _lobbyMap;
    }

    public void setGameState(GameState gameState)
    {
        GameStateChangeEvent event = new GameStateChangeEvent(_gameState, gameState);
        Bukkit.getPluginManager().callEvent(event);
        _gameState = gameState;
    }
}
