package game.gamelist.spleef;

import core.minecraft.combat.DeathMessageType;
import core.minecraft.command.CommandManager;
import core.minecraft.damage.DamageManager;
import core.minecraft.damage.events.CustomDamageEvent;
import core.minecraft.hologram.HologramManager;
import core.minecraft.region.RegionManager;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.world.MapType;
import core.minecraft.world.config.MapConfig;
import game.GameManager;
import game.SoloGame;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import game.settings.GameSettings;
import game.settings.GameSettingsManager;
import game.sound.SoundManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Spleef extends SoloGame implements Listener
{

    private FallingBlockController _fallingBlockController;

    public Spleef(JavaPlugin plugin, CommandManager commandManager, GameManager gameManager, GameSettingsManager gameSettingsManager,
                  ScoreManager scoreManager, SoundManager soundManager, DamageManager damageManager, HologramManager hologramManager,
                  RegionManager regionManager)
    {
        super(plugin, commandManager, gameManager, gameSettingsManager, scoreManager, soundManager, damageManager, hologramManager, regionManager);

        _fallingBlockController = new FallingBlockController(getPlugin(), _teamManager);
        _deathMessageType = DeathMessageType.SIMPLE;
    }

    @Override
    public MapType getMapType()
    {
        return MapType.SPLEEF;
    }

    @Override
    public boolean canStartGame()
    {
        boolean canStart = _teamManager.getLivingPlayerCount() >= requiredPlayerCount;
        return canStart;
    }

    @Override
    public boolean isValidMap(MapConfig map)
    {
        if (map == null)
        {
            return false;
        }
        boolean isValid = !map.getTeamSpawnLocations("ALL").isEmpty();
        return isValid;
    }

    @Override
    public HashMap<String, List<Player>> getPlayerTeleportLocations()
    {
        HashMap<String, List<Player>> spawnMap = new HashMap<>();
        spawnMap.put("ALL", new ArrayList<Player>(Bukkit.getOnlinePlayers()));
        return spawnMap;
    }

    @Override
    public void setGameSettings()
    {
        _gameSettings = new GameSettings();
        _gameSettings.enableExplosionMapDamage = false;
        _gameSettings.allowExplosions = false;
        _gameSettings.enableExplosionPlayerDamage = false;
        _gameSettings.loseDurability = false;
        _gameSettings.allowCustomGameModes = true;
        _gameSettings.defaultGameMode = GameMode.SURVIVAL;
        _gameSettings.pvp = false;
        _gameSettings.evp = false;
        _gameSettings.pve = false;
        _gameSettings.allowBlockPlace = false;
        _gameSettings.allowBlockBreak = false;
        _gameSettings.enableFallDamage = false;
        _gameSettings.enableLavaDamage = false;
        _gameSettings.enableHungerLoss = false;
        _gameSettingsManager.applyGameSettings(_gameSettings);
    }

    @Override
    public SideBarScoreboard getPlayerScoreboard(Player player)
    {
        SideBarScoreboard scoreboard = new SideBarScoreboard(_scoreManager, 3);
        scoreboard.updateLine(2, "");
        scoreboard.updateLine(1, "" + ChatColor.YELLOW + ChatColor.BOLD + "Players Left");
        scoreboard.updateLine(0, "" + _teamManager.getLivingPlayers().size());
        return scoreboard;
    }

    @Override
    public TimerType getScoreboardRefreshRate()
    {
        return TimerType.SECOND;
    }

    @Override
    public void clearGameData()
    {

    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void checkForWinner(CustomDamageEvent event)
    {
        Player damagee = event.getPlayerDamagee();
        if (_gameManager.getGameState() == GameState.IN_GAME && _gameManager.getCurrentGame() == this &&
                (event.getDamageCause() == EntityDamageEvent.DamageCause.LAVA ||
                 event.getDamageCause() == EntityDamageEvent.DamageCause.VOID))
        {
            _teamManager.setDead(damagee, true);
            if (_teamManager.getLivingPlayerCount() == 1)
            {
                Player winner = _teamManager.getLivingPlayers().iterator().next();
                endGame(winner.getDisplayName(), new Player[] {winner});
            }
            else if (_teamManager.getLivingPlayerCount() < 1)
            {
                endGame("Nobody", new Player[0]);
            }
        }
    }

    @EventHandler
    public void spawnFallingBlocks(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.IN_GAME && _gameManager.getCurrentGame() == this)
        {
            _fallingBlockController.detectFallingBlocks();
        }
        else
        {
            _fallingBlockController.stopDetectingFallingBlocks();
        }
    }

}
