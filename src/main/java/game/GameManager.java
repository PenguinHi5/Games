package game;

import core.minecraft.common.F;
import core.minecraft.cooldown.Cooldown;
import core.minecraft.cooldown.event.CooldownCompletedEvent;
import core.minecraft.damage.DamageManager;
import core.minecraft.hologram.HologramManager;
import core.minecraft.region.RegionManager;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.world.WorldManager;
import core.minecraft.world.config.MapConfig;
import game.common.GameList;
import game.gamelist.spleef.Spleef;
import game.gamelist.towerdefense.TowerDefense;
import game.gamestate.GameState;
import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import game.gamestate.event.ChooseNextGameEvent;
import game.gamestate.event.GameStateChangeEvent;
import game.lobby.LobbyManager;
import game.lobby.event.CancelCountdownEvent;
import game.lobby.event.StartCountdownEvent;
import game.settings.GameSettingsManager;
import game.sound.SoundManager;
import game.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;

public class GameManager extends Component implements Listener
{
    private TransactionManager _transactionManager;
    private WorldManager _worldManager;
    private ScoreManager _scoremanager;
    private LobbyManager _lobbyManager;
    private GameState _gameState;
    private Game _previousGame, _currentGame, _nextGame;
    // Stores all of the game instances
    private HashMap<GameList, Game> _games = new HashMap<>();
    private MapConfig _lobbyMap;
    private boolean _isCountingDown = false;
    // Ensures there's time between lobby and starting the game
    private boolean _canCountDown = true;
    private TeamManager _teamManager;
    private GameSettingsManager _gameSettingsManager;
    private SoundManager _soundManager;
    private DamageManager _damageManager;
    private HologramManager _hologramManager;
    private RegionManager _regionManager;

    // Checks for
    private int _countdownSecondsRemaining;

    public static final int DEFAULT_COUNTDOWN = 20;
    public static final String COUNTDOWN_COOLDOWN_NAME = "GameCountdownTimer";

    public GameManager(JavaPlugin plugin, CommandManager commandManager, ScoreManager scoreManager, TransactionManager transactionManager,
                       WorldManager worldManager, GameSettingsManager gameSettingsManager, SoundManager soundManager, DamageManager damageManager,
                       HologramManager hologramManager, RegionManager regionManager)
    {
        super("game.Game Manager", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        _teamManager = new TeamManager(getPlugin(), this);

        _scoremanager = scoreManager;
        _transactionManager = transactionManager;
        _worldManager = worldManager;
        _gameSettingsManager = gameSettingsManager;
        _soundManager = soundManager;
        _damageManager = damageManager;
        _hologramManager = hologramManager;
        _regionManager = regionManager;
        _lobbyManager = new LobbyManager(plugin, commandManager, this, worldManager, scoreManager, transactionManager, gameSettingsManager);
        // The lobby should be the default world
        _lobbyMap = worldManager.getActiveMap();
        setGameState(GameState.LOBBY);

        initializeGames();

        // Set the initial game
        Game[] games = _games.values().toArray(new Game[0]);
        if (games.length <= 1) // Only one playable game
        {
            _previousGame = games[0];
            _currentGame = games[0];
        }
        else // multiple playable games
        {
            _previousGame = games[0];
            _nextGame = games[1];
        }
        setNextGame();
        setGameState(GameState.LOBBY);
    }

    /**
     * Creates a new instance for every game.
     */
    private void initializeGames()
    {
        _games.put(GameList.TOWER_DEFENSE, new TowerDefense(getPlugin(), _commandManager, this, _gameSettingsManager, _scoremanager, _soundManager,
                _damageManager, _hologramManager, _regionManager));
        //_games.put(GameList.SPLEEF, new Spleef(getPlugin(), _commandManager, this, _gameSettingsManager, _scoremanager, _soundManager, _damageManager, _hologramManager));
    }

    /**
     * Randomly picks the next game
     */
    public void setNextGame()
    {
        Game[] games = _games.values().toArray(new Game[0]);
        Game chosenGame;
        // For early on when there are less than 3 games to cycle through
        if (games.length > 2)
        {
            do {
                chosenGame = games[(new Random()).nextInt(games.length)];
            } while (chosenGame == _currentGame || chosenGame == _previousGame);
        }
        else
        {
            chosenGame = _previousGame;
        }

        _nextGame = chosenGame;
        Bukkit.getPluginManager().callEvent(new ChooseNextGameEvent(_nextGame));
        F.componentMessage("Game", "Next game: " + chosenGame.getMapType().name());
    }

    /**
     * Starts the timer that will countdown to the start of the game.
     *
     * @param seconds the number of seconds until the game starts
     */
    public void startTimer(int seconds)
    {
        // Ensures the game can even be started
        if (!_nextGame.canStartGame())
            return;

        _isCountingDown = true;
        // Countdown can't be less than 1
        if (seconds < 1)
        {
            seconds = DEFAULT_COUNTDOWN;
        }

        _countdownSecondsRemaining = 5;

        // Countdown stored as a cooldown
        Cooldown.getInstance().createCooldown(COUNTDOWN_COOLDOWN_NAME, seconds * 1000);
        Bukkit.getPluginManager().callEvent(new StartCountdownEvent(_nextGame, seconds));
    }

    /**
     * Cancels the game countdown timer.
     */
    public void cancelTimer()
    {
        _isCountingDown = false;
        Cooldown.getInstance().cancelCooldown(COUNTDOWN_COOLDOWN_NAME);
        Bukkit.getPluginManager().callEvent(new CancelCountdownEvent(_nextGame));
    }

    /**
     * Starts the next game.
     */
    public void startGame()
    {
        // Sets gamestate to pre-game
        setGameState(GameState.PRE_GAME);
    }

    public void setCurrentGame(Game game)
    {
        _currentGame = game;
    }

    public Game getCurrentGame()
    {
        return _currentGame;
    }

    public void setPreviousGame(Game game)
    {
        _previousGame = game;
    }

    public Game getPreviousGame()
    {
        return _previousGame;
    }

    public void setNextGame(Game game)
    {
        _nextGame = game;
    }

    public Game getNextGame()
    {
        return _nextGame;
    }

    public TeamManager getTeamManager()
    {
        return _teamManager;
    }

    public void endGame(String winnerName, Player[] winners)
    {
        // Update gamestate
        setGameState(GameState.POST_GAME);

        // Announce winner
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "WINNER: " + winnerName);

        // Countdown to change game state to lobby
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                 setGameState(GameState.LOBBY);
            }
        }, 200L);
    }

    public GameState getGameState()
    {
        return _gameState;
    }

    public MapConfig getLobbyMap()
    {
        return _lobbyMap;
    }

    public boolean isCountingDown()
    {
        return _isCountingDown;
    }

    public void setGameState(GameState gameState)
    {
        GameStateChangeEvent event = new GameStateChangeEvent(_gameState, gameState);
        Bukkit.getPluginManager().callEvent(event);
        _gameState = gameState;
    }

    @EventHandler
    public void onCountdownComplete(CooldownCompletedEvent event)
    {
        // Countdown completed
        if (event.getName() == COUNTDOWN_COOLDOWN_NAME && _gameState == GameState.LOBBY)
        {
            startGame();
        }
        else if (event.getName() == "LobbyCooldown" && _gameState == GameState.LOBBY)
        {
            _canCountDown = true;
        }
    }

    @EventHandler
    public void checkForGameRequirenments(TimerEvent event)
    {
        // Check every tick and confirm a next game has been picked
        if (event.getType() != TimerType.TICK || _nextGame == null || !_canCountDown || _gameState != GameState.LOBBY)
            return;

        if (_isCountingDown)
        {
            // Cancels the timer if requirements aren't met
            if (!_nextGame.canStartGame())
            {
                cancelTimer();
            }
        }
        else
        {
            // Starts the timer if requirements are met
            if (_nextGame.canStartGame())
            {
                startTimer(20);
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void cooldownBetweenGames(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.LOBBY)
        {
            Cooldown.getInstance().createCooldown("LobbyCooldown", 1000L * 5L);
            _canCountDown = false;
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void updateGameHistory(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.PRE_GAME)
        {
            // Game history is updated when we enter PRE_GAME state
            _previousGame = _currentGame;
            _currentGame = _nextGame;
            setNextGame();
        }
    }

    @EventHandler
    public void gameCountdownSound(TimerEvent event)
    {
        if (event.getType() != TimerType.TICK)
            return;

        if (_isCountingDown && _countdownSecondsRemaining > 0)
        {
            long timeLeft = Cooldown.getInstance().getCooldownTime(COUNTDOWN_COOLDOWN_NAME);
            if (timeLeft < (long) _countdownSecondsRemaining * 1000L)
            {
                Bukkit.broadcastMessage(ChatColor.AQUA + F.BOLD + "Starting in " + _countdownSecondsRemaining + " seconds");
                if (_countdownSecondsRemaining-- == 1)
                {
                    _soundManager.playTuneForEveryone(1f, 1f);
                }
                else
                {
                    _soundManager.playTuneForEveryone(1f, 0.75f);
                }
            }
        }
    }

}
