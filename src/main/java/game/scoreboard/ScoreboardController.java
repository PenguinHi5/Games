package game.scoreboard;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.common.CurrencyType;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.transaction.TransactionManager;
import game.GameManager;
import game.gamestate.GameState;
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
}
