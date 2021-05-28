package game.world;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.common.F;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.world.MapType;
import core.minecraft.world.WorldManager;
import core.minecraft.world.config.MapConfig;
import game.GameManager;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import game.lobby.event.CancelCountdownEvent;
import game.lobby.event.StartCountdownEvent;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GameWorldManager extends Component implements Listener
{

    private boolean _allowMovementInPregame = false;
    private HashMap<String, List<Player>> _spawnLocations;
    private WorldManager _worldManager;
    private GameManager _gameManager;
    private MapConfig _nextMap;
    private HashMap<Player, Location> _previousSpawnLocations;

    public GameWorldManager(JavaPlugin plugin, CommandManager commandManager, WorldManager worldManager, GameManager gameManager)
    {
        super("GameWorldManager", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());

        _worldManager = worldManager;
        _gameManager = gameManager;
    }

    @EventHandler
    public void onCountdownCancel(CancelCountdownEvent event)
    {

    }

    /**
     * Prepares a map for the game.
     */
    @EventHandler
    public void onCountdownStart(StartCountdownEvent event)
    {
        List<String> mapList = _worldManager.getMapsOfType(event.getGame().getMapType());
        Collections.shuffle(mapList);
        // If no maps were found
        if (mapList.size() == 0)
        {
            _gameManager.cancelTimer();
            Bukkit.broadcastMessage(F.errorMessage("No maps could be found for this game"));
            return;
        }

        boolean foundValidMap = false;
        int idx = 0;
        MapConfig mapConfig = null;
        do {
            String map = mapList.get(idx++);

            // If the world failed to load
            if (!_worldManager.loadWorld(map, WeatherType.CLEAR, 6000L))
            {
                continue;
            }

            // If the map isn't a valid map for this game
            mapConfig = _worldManager.getMapConfig(FilenameUtils.removeExtension(map));
            if (event.getGame().isValidMap(mapConfig))
            {
                foundValidMap = true;
            }
            else
            {
                _worldManager.unloadWorld(FilenameUtils.removeExtension(map));
                continue;
            }

        } while (foundValidMap == false && idx < mapList.size());

        if (foundValidMap)
        {
            _nextMap = mapConfig;
            // Prepares the mapdata for the game
            event.getGame().loadMapData(mapConfig);
        }
        else
        {
            _gameManager.cancelTimer();
        }
    }

    /**
     * Unload the map after the game ends
     * @param event
     */
    @EventHandler
    public void teleportPlayersToMap(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.LOBBY)
        {
            _previousSpawnLocations = null;
            _spawnLocations = null;
            _worldManager.setActiveMap(_gameManager.getLobbyMap().getWorld().getName());
            _worldManager.teleportAllPlayersToActiveMap(new ArrayList<Player>(Bukkit.getOnlinePlayers()), "ALL");
            _worldManager.unloadWorld(_nextMap.getWorld().getName());
        }
        else if (event.getNewGameState() == GameState.PRE_GAME)
        {
            // Teleport players to the game map
            _worldManager.setActiveMap(_nextMap.getWorld().getName());
            HashMap<String, List<Player>> playerLocs = _gameManager.getCurrentGame().getPlayerTeleportLocations();
            _spawnLocations = playerLocs;
            _previousSpawnLocations = new HashMap<>();
            for (String team : playerLocs.keySet())
            {
                _previousSpawnLocations.putAll(_worldManager.teleportAllPlayersToActiveMap(playerLocs.get(team), team));
            }
        }
    }

    /**
     * Teleport players to active map on join
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        _worldManager.teleportPlayerToActiveMap(event.getPlayer(), "ALL");
    }

    @EventHandler
    public void onPlayerMove(TimerEvent event)
    {
        if (event.getType() != TimerType.TEN_TICKS || _gameManager.getGameState() != GameState.PRE_GAME || _previousSpawnLocations == null)
        {
            return;
        }

        // Teleport players back to starting points
        for (Player player : _previousSpawnLocations.keySet())
        {
            Location loc = _previousSpawnLocations.get(player);
            loc.setDirection(player.getLocation().getDirection());
            player.teleport(loc);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _worldManager.teleportPlayerToActiveMap(event.getEntity(), "ALL");
            }
        }, 1L);
    }

}
