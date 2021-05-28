package game;

import core.minecraft.command.CommandManager;
import core.minecraft.damage.DamageManager;
import core.minecraft.hologram.HologramManager;
import core.minecraft.region.RegionManager;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.world.MapType;
import core.minecraft.world.config.MapConfig;
import game.settings.GameSettingsManager;
import game.sound.SoundManager;
import game.world.GameWorldManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public abstract class SoloGame extends Game
{

    public SoloGame(JavaPlugin plugin, CommandManager commandManager, GameManager gameManager, GameSettingsManager gameSettingsManager,
                    ScoreManager scoreManager, SoundManager soundManager, DamageManager damageManager, HologramManager hologramManager,
                    RegionManager regionManager)
    {
        super(plugin, commandManager, gameManager, gameSettingsManager, scoreManager, soundManager, damageManager, hologramManager, regionManager);
    }

    public abstract MapType getMapType();

    public abstract boolean canStartGame();

    public abstract boolean isValidMap(MapConfig map);

    public abstract HashMap<String, List<Player>> getPlayerTeleportLocations();
}
