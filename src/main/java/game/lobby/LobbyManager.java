package game.lobby;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.common.CurrencyType;
import core.minecraft.common.utils.SystemUtil;
import core.minecraft.cooldown.Cooldown;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.world.WorldManager;
import core.minecraft.world.config.MapConfig;
import game.GameManager;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import game.settings.GameSettings;
import game.settings.GameSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;

public class LobbyManager extends Component implements Listener {

    private GameManager _gameManager;
    private WorldManager _worldManager;
    private ScoreManager _scoreManager;
    private TransactionManager _transactionManager;
    private GameSettingsManager _gameSettingsManager;
    private GameSettings _lobbyGameSettings;

    public LobbyManager(JavaPlugin plugin, CommandManager commandManager, GameManager gamemanager, WorldManager worldManager,
                        ScoreManager scoreManager, TransactionManager transactionManager, GameSettingsManager gameSettingsManager)
    {
        super("Lobby", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _gameManager = gamemanager;
        _worldManager = worldManager;
        _scoreManager = scoreManager;
        _transactionManager = transactionManager;
        _gameSettingsManager = gameSettingsManager;

        // Game settings
        _lobbyGameSettings = new GameSettings();
        _lobbyGameSettings.enableExplosionMapDamage = false;
        _lobbyGameSettings.allowExplosions = true;
        _lobbyGameSettings.enableExplosionPlayerDamage = false;
        _lobbyGameSettings.loseDurability = false;
        _lobbyGameSettings.allowCustomGameModes = false;
        _lobbyGameSettings.defaultGameMode = GameMode.SURVIVAL;
        _lobbyGameSettings.pvp = false;
        _lobbyGameSettings.evp = false;
        _lobbyGameSettings.pve = false;
        _lobbyGameSettings.allowBlockPlace = false;
        _lobbyGameSettings.allowBlockBreak = false;
        _lobbyGameSettings.enableFallDamage = false;
        _lobbyGameSettings.enableFireDamage = false;
        _lobbyGameSettings.enableLavaDamage = false;
        _lobbyGameSettings.enableHungerLoss = false;
    }

    public void enterLobby()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.setExp(0f);
            player.setSaturation(10f);
        }
    }

    /**
     * Teleports all players on the server to the lobby.
     */
    public void teleportPlayersToLobby()
    {
        MapConfig lobbyMap = _gameManager.getLobbyMap();
        _worldManager.teleportAllPlayersToMap(new LinkedList<>(Bukkit.getOnlinePlayers()), "all", lobbyMap);

    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.LOBBY)
        {
            _gameSettingsManager.applyGameSettings(_lobbyGameSettings);
            // Sets lobby to active map and teleports players to active map
            _worldManager.setActiveMap(_gameManager.getLobbyMap().getWorld().getName());
            teleportPlayersToLobby();
            enterLobby();
        }
    }

}
