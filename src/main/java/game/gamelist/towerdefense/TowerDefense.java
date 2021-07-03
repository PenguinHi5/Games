package game.gamelist.towerdefense;

import core.minecraft.combat.DeathMessageType;
import core.minecraft.command.CommandManager;
import core.minecraft.common.utils.SystemUtil;
import core.minecraft.cooldown.event.CooldownCompletedEvent;
import core.minecraft.damage.DamageManager;
import core.minecraft.hologram.HologramManager;
import core.minecraft.region.RegionManager;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.world.MapType;
import core.minecraft.world.config.MapConfig;
import game.GameManager;
import game.TeamGame;
import game.common.ColorTeamNames;
import game.common.ElapsedTimeTracker;
import game.gamelist.towerdefense.block.BlockProtection;
import game.gamelist.towerdefense.ore.OreSpawner;
import game.gamelist.towerdefense.region.TowerDefenseRegionManager;
import game.gamelist.towerdefense.tower.TowerController;
import game.gamelist.towerdefense.wither.WitherController;
import game.gamestate.GameState;
import game.gamestate.event.ChooseNextGameEvent;
import game.gamestate.event.GameStateChangeEvent;
import game.lobby.event.StartCountdownEvent;
import game.settings.GameSettings;
import game.settings.GameSettingsManager;
import game.sound.SoundManager;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TowerDefense extends TeamGame
{

    public OreSpawner _oreSpawner;
    public TowerController _towerController;
    public BlockProtection _blockProtection;
    public TowerDefenseRegionManager _towerDefenseRegionManager;
    public TowerDefenseRespawnManager _towerDefenseRespawnManager;
    public ElapsedTimeTracker elapsedTimeTracker;

    public TowerDefense(JavaPlugin plugin, CommandManager commandManager, GameManager gameManager, GameSettingsManager gameSettingsManager,
                        ScoreManager scoreManager, SoundManager soundManager, DamageManager damageManager, HologramManager hologramManager,
                        RegionManager regionManager)
    {
        super(plugin, commandManager, gameManager, gameSettingsManager, scoreManager, soundManager, damageManager, hologramManager, regionManager);

        _deathMessageType = DeathMessageType.DETAILED;
        _blockProtection = new BlockProtection(this, _gameManager, _teamManager, plugin);
    }

    private void setupGame()
    {
        _towerController = null;
        _towerController = new TowerController(getPlugin(), this, _gameManager, _hologramManager);

        _oreSpawner = null;
        _oreSpawner = new OreSpawner(getPlugin(), _currentMap);

        _towerDefenseRegionManager = null;
        _towerDefenseRegionManager = new TowerDefenseRegionManager(_regionManager, _currentMap, getPlugin());

        _towerDefenseRespawnManager = null;
        _towerDefenseRespawnManager = new TowerDefenseRespawnManager(getPlugin(), this, _teamManager, _currentMap);
    }

    public void team1Wins()
    {
        ColorTeamNames winner = ColorTeamNames.values()[0];
        endGame(winner.textColor + winner.name + " Team", _teamManager.getAllPlayerTeams().get(winner).toArray(new Player[0]));
    }

    public void team2Wins()
    {
        ColorTeamNames winner = ColorTeamNames.values()[1];
        endGame(winner.textColor + winner.name + " Team", _teamManager.getAllPlayerTeams().get(winner).toArray(new Player[0]));
    }

    public ItemStack[] getDefaultArmor(Player player)
    {
        // TODO -- Check for upgraded armor
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.LEATHER_HELMET, 1);
        armor[1] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        armor[2] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        armor[3] = new ItemStack(Material.LEATHER_BOOTS, 1);
        for (int i = 0; i < 4; i++)
        {
            LeatherArmorMeta meta = (LeatherArmorMeta)armor[i].getItemMeta();
            meta.setColor(_teamManager.getPlayersTeam(player).color);
            armor[i].setItemMeta(meta);
        }
        return armor;
    }

    @Override
    public MapType getMapType()
    {
        return MapType.TOWER_DEFENSE;
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

        boolean isValid = !map.getTeamSpawnLocations("ALL").isEmpty() &&
                !map.getTeamSpawnLocations("TEAM1").isEmpty() &&
                !map.getTeamSpawnLocations("TEAM2").isEmpty();
        return isValid;
    }

    @Override
    public void setGameSettings()
    {
        _gameSettings = new GameSettings();
        _gameSettings.enableExplosionMapDamage = true;
        _gameSettings.allowExplosions = true;
        _gameSettings.enableExplosionPlayerDamage = false;
        _gameSettings.loseDurability = false;
        _gameSettings.allowCustomGameModes = true;
        _gameSettings.defaultGameMode = GameMode.SURVIVAL;
        _gameSettings.pvp = true;
        _gameSettings.evp = true;
        _gameSettings.pve = true;
        _gameSettings.allowBlockPlace = true;
        _gameSettings.allowBlockBreak = true;
        _gameSettings.enableFallDamage = true;
        _gameSettings.enableLavaDamage = true;
        _gameSettings.enableHungerLoss = false;
        _gameSettingsManager.applyGameSettings(_gameSettings);
    }

    @Override
    public SideBarScoreboard getPlayerScoreboard(Player player)
    {
        SideBarScoreboard scoreboard = new SideBarScoreboard(_scoreManager, 11);
        scoreboard.updateLine(10, "");
        scoreboard.updateLine(9, ColorTeamNames.values()[0].textColor.toString() + ChatColor.BOLD + ColorTeamNames.values()[0].name.toUpperCase() + " TEAM");

        // Tower health bars
        scoreboard.updateLine(8, _towerController.getTeam1TowerHealthBar());
        scoreboard.updateLine(7, "☠☠☠");
        scoreboard.updateLine(6, "");
        scoreboard.updateLine(5, ColorTeamNames.values()[1].textColor.toString() + ChatColor.BOLD + ColorTeamNames.values()[1].name.toUpperCase() + " TEAM");
        scoreboard.updateLine(4, _towerController.getTeam2TowerHealthBar());
        scoreboard.updateLine(3, "☠☠☠");
        scoreboard.updateLine(2, "");

        // Time Elapsed
        scoreboard.updateLine(1, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Time Elapsed");
        if (elapsedTimeTracker == null)
        {
            scoreboard.updateLine(0, "00:00");
        }
        else
        {
            long elapsedTime = elapsedTimeTracker.getTime();
            scoreboard.updateLine(0, String.format("%02d", elapsedTime / 60000) + ":" + String.format("%02d", elapsedTime / 1000 % 60));
        }

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
        _towerController = null;
        _oreSpawner = null;
        _towerDefenseRegionManager = null;
        _towerDefenseRespawnManager = null;
    }

    @Override
    public HashMap<String, List<Player>> getPlayerTeleportLocations()
    {
        HashMap<String, List<Player>> playerLocs = new HashMap<>();
        HashMap<ColorTeamNames, HashSet<Player>> teams = _teamManager.getAllPlayerTeams();
        for (int i = 0; i < teams.keySet().size(); i++)
        {
            ArrayList<Player> team = new ArrayList<>(teams.get(ColorTeamNames.values()[i]));
            playerLocs.put("TEAM" + (i + 1), team);
        }
        return playerLocs;
    }

    @EventHandler
    public void setupPlayerTeams(GameStateChangeEvent event)
    {
        if (_gameManager.getNextGame() == this && event.getNewGameState() == GameState.LOBBY)
        {
            _teamManager.setTeamCount(2);
        }
    }

    @EventHandler
    public void setupPlayerTeams(ChooseNextGameEvent event)
    {
        if (event.getNextGame() == this && _gameManager.getGameState() == GameState.LOBBY)
        {
            _teamManager.setTeamCount(2);
        }
    }

    @EventHandler
    public void forceAssignPlayersToTeams(CooldownCompletedEvent event)
    {
        if (event.getName().equals(GameManager.COUNTDOWN_COOLDOWN_NAME) && _gameManager.getNextGame() == this)
        {
            _teamManager.assignAllPlayersToTeams();
        }
    }

    @EventHandler
    public void giveStarterItems(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.PRE_GAME && _gameManager.getCurrentGame() == this)
        {
            for (Player player : _teamManager.getPlayers())
            {
                player.getInventory().clear();
                ItemStack[] armor = getDefaultArmor(player);
                player.getInventory().setHelmet(armor[0]);
                player.getInventory().setChestplate(armor[1]);
                player.getInventory().setLeggings(armor[2]);
                player.getInventory().setBoots(armor[3]);
                player.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1), new ItemStack(Material.STONE_PICKAXE, 1));
            }
        }
    }

    @EventHandler
    public void startElapsedTimeTracker(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.IN_GAME && _gameManager.getCurrentGame() == this)
        {
            elapsedTimeTracker = new ElapsedTimeTracker(getPlugin());
            elapsedTimeTracker.start();

            // TODO !!!FOR TESTING ONLY!!!
            WitherController controller = new WitherController(this);
            controller.spawnTeam1Wither();
            controller.spawnTeam2Wither();
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void resetComponents(StartCountdownEvent event)
    {
        if (event.getGame() == this)
        {
            setupGame();
        }
    }
}
