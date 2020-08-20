package game.lobby;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.common.CurrencyType;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.world.WorldManager;
import core.minecraft.world.config.MapConfig;
import game.GameManager;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

    public LobbyManager(JavaPlugin plugin, CommandManager commandManager, GameManager gamemanager, WorldManager worldManager,
                        ScoreManager scoreManager, TransactionManager transactionManager)
    {
        super("Lobby", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _gameManager = gamemanager;
        _worldManager = worldManager;
        _scoreManager = scoreManager;
        _transactionManager = transactionManager;
    }

    /**
     * Teleports all players on the server to the lobby.
     */
    public void teleportPlayersToLobby()
    {
        MapConfig lobbyMap = _gameManager.getLobbyMap();
        _worldManager.teleportAllPlayersToMap(new LinkedList<>(Bukkit.getOnlinePlayers()), "all", lobbyMap);

    }

    private void generatePlayerLobbySideBarScoreboard(Player player)
    {
        SideBarScoreboard scoreboard = new SideBarScoreboard(_scoreManager, 3);
        int crystals = _transactionManager.getPlayerBalance(CurrencyType.CRYSTAL, player.getName());
        scoreboard.updateLine(1, "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "CRYSTALS");
        scoreboard.updateLine(0, "" + ChatColor.WHITE + crystals);
        _scoreManager.setPlayerData(player.getName(), scoreboard);
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        generatePlayerLobbySideBarScoreboard(event.getPlayer());
        if (_gameManager.getGameState() == GameState.LOBBY)
        {
            _scoreManager.setPlayerData(event.getPlayer().getName(), _scoreManager.getPlayerData(event.getPlayer()));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        _scoreManager.removePlayerData(event.getPlayer().getName());
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.LOBBY)
        {
            _scoreManager.updateScoreboardRefreshRate(TimerType.SECOND);
            for (Player player : Bukkit.getOnlinePlayers())
            {
                generatePlayerLobbySideBarScoreboard(player);
            }
            // Sets lobby to active map and teleports players to active map
            _worldManager.setActiveMap(_gameManager.getLobbyMap().getWorld().getName());
            teleportPlayersToLobby();
        }
    }
}
