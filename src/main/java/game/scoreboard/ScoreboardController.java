package game.scoreboard;

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

public class ScoreboardController extends Component implements Listener {

    private GameManager _gameManager;
    private ScoreManager _scoreManager;
    private TransactionManager _transactionManager;

    public ScoreboardController(JavaPlugin plugin, CommandManager commandManager, ScoreManager scoreManager, TransactionManager transactionManager,
                                GameManager gameManager)
    {
        super("Scoreboard Controller", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _gameManager = gameManager;
        _scoreManager = scoreManager;
        _scoreManager.updateScoreboardRefreshRate(TimerType.SECOND);
        _transactionManager = transactionManager;
    }

    /**
     * Generates a scoreboard for the given player that should be shown in the lobby.
     *
     * @param player the player who the scoreboard is being generated for
     */
    private void generatePlayerLobbySideBarScoreboard(Player player)
    {
        SideBarScoreboard scoreboard = new SideBarScoreboard(_scoreManager, 5);
        int crystals = _transactionManager.getPlayerBalance(CurrencyType.CRYSTAL, player.getName());
        scoreboard.updateLine(4, "  ");
        scoreboard.updateLine(3, "" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "CRYSTALS");
        scoreboard.updateLine(2, "" + ChatColor.WHITE + crystals);
        scoreboard.updateLine(1, " ");
        if (_gameManager.isCountingDown())
        {
            scoreboard.updateLine(0, "" + ChatColor.GREEN + ChatColor.BOLD + "Starting in " +
                    ChatColor.WHITE + ChatColor.BOLD + SystemUtil.getWrittenTimeRemaining(Cooldown.getInstance().getCooldownTime(_gameManager.COUNTDOWN_COOLDOWN_NAME)));
        }
        else
        {
            scoreboard.updateLine(0, "" + ChatColor.GREEN + ChatColor.BOLD + "Waiting for players...");
        }
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
            for (Player player : Bukkit.getOnlinePlayers())
            {
                generatePlayerLobbySideBarScoreboard(player);
            }
        }
    }

    /**
     * Updates the information shown in the scoreboard for every player.
     */
    @EventHandler
    public void updateScoreboard(TimerEvent event)
    {
        if (event.getType() == TimerType.TEN_TICKS)
        {
            if (_gameManager.getGameState() == GameState.LOBBY)
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    generatePlayerLobbySideBarScoreboard(player);
                }
            }
        }

        if (event.getType() == _gameManager.getCurrentGame().getScoreboardRefreshRate() &&
            _gameManager.getGameState() != GameState.LOBBY)
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                _scoreManager.setPlayerData(player.getName(), _gameManager.getCurrentGame().getPlayerScoreboard(player));
            }
            _scoreManager.updateScoreboards();
        }
    }
}
