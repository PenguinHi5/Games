package game.respawn;

import core.minecraft.cooldown.Cooldown;
import core.minecraft.cooldown.event.CooldownCompletedEvent;
import core.minecraft.damage.events.CustomDamageEvent;
import core.minecraft.world.config.MapConfig;
import game.respawn.event.OnDeathPlayerRespawnEvent;
import game.respawn.event.OnRespawnPlayerRespawnEvent;
import game.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * This is a basic respawn controller that can be used by any game.
 * Players will not be marked as dead in the TeamManager while in limbo waiting to respawn.
 */
public class RespawnController implements Listener
{

    private HashMap<String, Player> _activeRespawns = new HashMap<>();
    private HashMap<Player, Location> _lastLocation = new HashMap<>();
    private long _defaultRespawnTime;
    private MapConfig _map;
    private JavaPlugin _plugin;
    private TeamManager _teamManager;

    public RespawnController(JavaPlugin plugin, long defaultRespawnTime, TeamManager teamManager, MapConfig map)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _defaultRespawnTime = defaultRespawnTime;
        _plugin = plugin;
        _teamManager = teamManager;
        _map = map;
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDamage(CustomDamageEvent event)
    {
        Location loc = event.getPlayerDamagee().getLocation();
        Location locVoid = _map.getTeamSpawnLocations("ALL").get(0);

        if (event.getDamageCause() == EntityDamageEvent.DamageCause.VOID)
        {
            _lastLocation.put(event.getPlayerDamagee(), locVoid);
        }
        else
        {
            _lastLocation.put(event.getPlayerDamagee(), loc);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        if (event.getEntity() == null)
            return;

        // If the player died
        if (event.getEntity() != null)
        {
            _teamManager.setDead(event.getEntity(), true);

            Bukkit.getScheduler().runTaskLater(_plugin, new Runnable() {
                @Override
                public void run()
                {
                    _teamManager.setDead(event.getEntity(), true);

                    if (_lastLocation.containsKey(event.getEntity()))
                    {
                        event.getEntity().teleport(_lastLocation.get(event.getEntity()));
                    }
                    OnDeathPlayerRespawnEvent respawnEvent = new OnDeathPlayerRespawnEvent(_defaultRespawnTime, event.getEntity());
                    Bukkit.getPluginManager().callEvent(respawnEvent);
                    String respawnID = "respawn." + event.getEntity().getName();
                    _activeRespawns.put(respawnID, event.getEntity());
                    Cooldown.getInstance().createCooldown(respawnID, respawnEvent.getRespawnRate());
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onRespawn(CooldownCompletedEvent event)
    {
        if (_activeRespawns.containsKey(event.getName()))
        {
            Player player = _activeRespawns.get(event.getName());
            _activeRespawns.remove(event.getName());
            _teamManager.setDead(player, false);
            player.setFireTicks(0);
            Bukkit.getPluginManager().callEvent(new OnRespawnPlayerRespawnEvent(player));
        }
    }

}
