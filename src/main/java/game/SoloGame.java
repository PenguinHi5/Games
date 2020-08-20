package game;

import core.minecraft.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SoloGame extends Game {

    public SoloGame(JavaPlugin plugin, CommandManager commandManager, GameManager gameManager)
    {
        super(plugin, commandManager, gameManager);
    }
}
