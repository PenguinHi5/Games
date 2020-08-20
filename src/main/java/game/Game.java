package game;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Represents a particular game.
 */
public abstract class Game extends Component {

    private GameManager _gameManager;

    public Game(JavaPlugin plugin, CommandManager commandManager, GameManager gameManager)
    {
        super("game.Game", plugin, commandManager);
        _gameManager = gameManager;
    }

    public void endGame(String winnerName, Player[] winners)
    {

    }
}
