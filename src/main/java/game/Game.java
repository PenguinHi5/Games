package game;

import core.minecraft.Component;
import core.minecraft.combat.DeathMessageType;
import core.minecraft.combat.events.CombatDeathEvent;
import core.minecraft.command.CommandManager;
import core.minecraft.common.F;
import core.minecraft.cooldown.Cooldown;
import core.minecraft.cooldown.event.CooldownCompletedEvent;
import core.minecraft.damage.DamageManager;
import core.minecraft.hologram.HologramManager;
import core.minecraft.region.RegionManager;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.world.MapType;
import core.minecraft.world.config.MapConfig;
import game.common.FireworkSpawner;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import game.lobby.event.StartCountdownEvent;
import game.settings.GameSettings;
import game.settings.GameSettingsManager;
import game.sound.SoundManager;
import game.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Represents a particular game.
 */
public abstract class Game extends Component implements Listener {

    protected boolean _isCountingDown;
    protected int _countdownSecondsRemaining = 3;
    protected GameManager _gameManager;
    protected TeamManager _teamManager;
    protected ScoreManager _scoreManager;
    protected SoundManager _soundManager;
    protected DamageManager _damageManager;
    protected GameSettingsManager _gameSettingsManager;
    protected GameSettings _gameSettings;
    protected HologramManager _hologramManager;
    protected RegionManager _regionManager;

    protected MapConfig _currentMap;
    protected DeathMessageType _deathMessageType = DeathMessageType.SIMPLE;
    protected HashMap<String, List<Player>> _playerTeleportLocations = new HashMap<>();

    // Requirements to start the game
    protected int requiredPlayerCount = 2;

    public static final String PREGAME_COOLDOWN_NAME = "PreGameCountdownTimer";

    public Game(JavaPlugin plugin, CommandManager commandManager, GameManager gameManager, GameSettingsManager gameSettingsManager,
                ScoreManager scoreManager, SoundManager soundManager, DamageManager damageManager, HologramManager hologramManager,
                RegionManager regionManager)
    {
        super("game.Game", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        _gameManager = gameManager;
        _teamManager = gameManager.getTeamManager();
        _gameSettingsManager = gameSettingsManager;
        _gameSettings = new GameSettings();
        _scoreManager = scoreManager;
        _soundManager = soundManager;
        _damageManager = damageManager;
        _hologramManager = hologramManager;
        _regionManager = regionManager;
    }

    public void endGame(String winnerName, Player[] winners)
    {
        _gameManager.endGame(winnerName, winners);
        clearGameData();
    }

    public abstract MapType getMapType();

    @EventHandler
    public void onStartCountdown(StartCountdownEvent event)
    {

    }

    /**
     * Retrieves the location of all the player spawn locations by team.
     */
    public abstract HashMap<String, List<Player>> getPlayerTeleportLocations();

    public boolean canStartGame()
    {
        boolean canStart =
                _teamManager.getLivingPlayerCount() >= requiredPlayerCount;

        return canStart;
    }

    /**
     * Return true if this game is either currently being played or if it is the next game to be played.
     */
    public boolean isGameActive()
    {
        // Because the games are cycled through when the PRE_GAME state is entered
        return (_gameManager.getGameState() == GameState.LOBBY && _gameManager.getNextGame() == this) ||
                (_gameManager.getGameState() != GameState.LOBBY && _gameManager.getCurrentGame() == this);
    }

    /**
     * Returns true if this game is currently in progress.
     */
    public boolean isInGame()
    {
        return _gameManager.getGameState() == GameState.IN_GAME && _gameManager.getCurrentGame() == this;
    }

    public abstract boolean isValidMap(MapConfig map);

    /**
     * Generates the teleport locations and prepares the map for the game. This method MUST be overriden.
     *
     * @param map the map being loaded
     */
    public void loadMapData(MapConfig map)
    {
        _currentMap = map;
    }

    public abstract void setGameSettings();

    public abstract SideBarScoreboard getPlayerScoreboard(Player player);

    public abstract TimerType getScoreboardRefreshRate();

    /**
     * This method runs when the game has ended. This should be used to clear any game data.
     */
    public abstract void clearGameData();

    public MapConfig getCurrentMap()
    {
        return _currentMap;
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.PRE_GAME)
        {
            // Change the gamestate to ingame after 15 seconds
            Bukkit.broadcastMessage(F.componentMessage("Game", "Starting game in 15 seconds..."));
            Cooldown.getInstance().createCooldown(PREGAME_COOLDOWN_NAME, 1000L * 15L);
            _isCountingDown = true;
            _countdownSecondsRemaining = 3;
            setGameSettings();
        }
    }

    @EventHandler
    public void gameCountdownSound(TimerEvent event)
    {
        if (event.getType() != TimerType.TICK)
            return;

        if (_isCountingDown)
        {
            // Sound effect and chat message
            long timeLeft = Cooldown.getInstance().getCooldownTime(PREGAME_COOLDOWN_NAME);
            if (_countdownSecondsRemaining > 0 && timeLeft < (long) _countdownSecondsRemaining * 1000L)
            {
                if (_countdownSecondsRemaining == 1)
                {
                    Bukkit.broadcastMessage(ChatColor.RED.toString() + _countdownSecondsRemaining--);
                    _soundManager.playTuneForEveryone(1f, 1f);                }
                else if (_countdownSecondsRemaining == 2)
                {
                    Bukkit.broadcastMessage(ChatColor.YELLOW.toString() + _countdownSecondsRemaining--);
                    _soundManager.playTuneForEveryone(1f, 0.75f);
                }
                else
                {
                    Bukkit.broadcastMessage(ChatColor.GREEN.toString() + _countdownSecondsRemaining--);
                    _soundManager.playTuneForEveryone(1f, 0.5f);
                }
            }

            // EXP bar
            float xpLevel = (timeLeft / 20000f);
            for (Player player : Bukkit.getOnlinePlayers())
            {
                player.setExp(xpLevel);
            }
        }
    }

    @EventHandler
    public void startGameCooldown(CooldownCompletedEvent event)
    {
        if (event.getName() == PREGAME_COOLDOWN_NAME)
        {
            _isCountingDown = false;
            _gameManager.setGameState(GameState.IN_GAME);
        }
    }

    @EventHandler
    public void updateDeathMessage(CombatDeathEvent event)
    {
        event.setDeathMessageType(_deathMessageType);
    }

    @EventHandler
    public void SetupCelebrationFireworkEffects(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.POST_GAME)
        {
            playCelebrationFireworkEffect();
            Cooldown.getInstance().createCooldown("game.fireworkwineffect", 3000L);
        }
    }

    @EventHandler
    public void celebrationFireworkEffectCooldown(CooldownCompletedEvent event)
    {
        if (event.getName() == "game.fireworkwineffect" && _gameManager.getGameState() == GameState.POST_GAME)
        {
            playCelebrationFireworkEffect();
            Cooldown.getInstance().createCooldown("game.fireworkwineffect", 3000L);
        }
    }

    public void playCelebrationFireworkEffect()
    {
        Random random = new Random();
        // Spawns 36 fireworks
        for (int x = -50; x < 50; x += 20)
        {
            for (int z = -50; z < 50; z += 20)
            {
                Location loc = new Location(_currentMap.getWorld(), x, _currentMap.getWorld().getHighestBlockYAt(x, z) + 1, z);
                FireworkSpawner.playFirework(_currentMap.getWorld(), loc, FireworkSpawner.generateRandomEffect(), 1);
            }
        }
    }

}
