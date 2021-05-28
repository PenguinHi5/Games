package game.gamelist.towerdefense.tower;

import core.minecraft.cooldown.Cooldown;
import core.minecraft.hologram.Hologram;
import core.minecraft.hologram.HologramManager;
import game.GameManager;
import game.common.ColorTeamNames;
import game.gamelist.towerdefense.TowerDefense;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public class TowerController implements Listener
{

    private TowerDefense _towerDefense;
    private Location[] _team1TowerLocs;
    private Location[] _team2TowerLocs;
    private Tower[] _team1Towers = new Tower[3];
    private Tower[] _team2Towers = new Tower[3];
    private Hologram[] _team1Holograms = new Hologram[3];
    private Hologram[] _team2Holograms = new Hologram[3];
    private int _team1ActiveTower = 0;
    private int _team2ActiveTower = 0;
    private GameManager _gameManager;
    private JavaPlugin _plugin;
    private HologramManager _hologramManager;

    public static final int SMALL_TOWER_HEALTH = 500;
    public static final int MEDIUM_TOWER_HEALTH = 1000;
    public static final int LARGE_TOWER_HEALTH = 1500;

    public TowerController(JavaPlugin plugin, TowerDefense towerDefense, GameManager gameManager, HologramManager hologramManager)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _towerDefense = towerDefense;
        _gameManager = gameManager;
        _hologramManager = hologramManager;
        _plugin = plugin;

        _team1Towers[0] = new Tower(SMALL_TOWER_HEALTH);
        _team1Towers[1] = new Tower(MEDIUM_TOWER_HEALTH);
        _team1Towers[2] = new Tower(LARGE_TOWER_HEALTH);
        _team2Towers[0] = new Tower(SMALL_TOWER_HEALTH);
        _team2Towers[1] = new Tower(MEDIUM_TOWER_HEALTH);
        _team2Towers[2] = new Tower(LARGE_TOWER_HEALTH);
    }

    @EventHandler
    public void spawnCrystals(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.PRE_GAME && _gameManager.getCurrentGame() == _towerDefense)
        {
            // Gets spawn locations from map config
            String team1S = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM1_SMALL");
            String team1M = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM1_MEDIUM");
            String team1L = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM1_LARGE");
            String team2S = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM2_SMALL");
            String team2M = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM2_MEDIUM");
            String team2L = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM2_LARGE");
            _team1TowerLocs = new Location[3];
            _team2TowerLocs = new Location[3];
            World world = _towerDefense.getCurrentMap().getWorld();
            try
            {
                // Parses strings into locations
                String[] split = team1S.split(",");
                _team1TowerLocs[0] = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                split = team1M.split(",");
                _team1TowerLocs[1] = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                split = team1L.split(",");
                _team1TowerLocs[2] = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                split = team2S.split(",");
                _team2TowerLocs[0] = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                split = team2M.split(",");
                _team2TowerLocs[1] = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));
                split = team2L.split(",");
                _team2TowerLocs[2] = new Location(world, Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]));

                // Spawns tower crystals
                _team1Towers[0].setTowerCrystal(world.spawnEntity(_team1TowerLocs[0], EntityType.ENDER_CRYSTAL));
                _team1Towers[1].setTowerCrystal(world.spawnEntity(_team1TowerLocs[1], EntityType.ENDER_CRYSTAL));
                _team1Towers[2].setTowerCrystal(world.spawnEntity(_team1TowerLocs[2], EntityType.ENDER_CRYSTAL));
                _team2Towers[0].setTowerCrystal(world.spawnEntity(_team2TowerLocs[0], EntityType.ENDER_CRYSTAL));
                _team2Towers[1].setTowerCrystal(world.spawnEntity(_team2TowerLocs[1], EntityType.ENDER_CRYSTAL));
                _team2Towers[2].setTowerCrystal(world.spawnEntity(_team2TowerLocs[2], EntityType.ENDER_CRYSTAL));

                // Spawn tower holograms
                _team1Holograms[0] = new Hologram(_plugin, _team1TowerLocs[0].clone().add(0, 1, 0));
                _team1Holograms[0].addLine(ChatColor.GREEN.toString() + SMALL_TOWER_HEALTH);
                _team1Holograms[0].addLine(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "HEALTH");
                _team1Holograms[1] = new Hologram(_plugin, _team1TowerLocs[1].clone().add(0, 1, 0));
                _team1Holograms[1].addLine(ChatColor.GREEN.toString() + MEDIUM_TOWER_HEALTH);
                _team1Holograms[1].addLine(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "HEALTH");
                _team1Holograms[2] = new Hologram(_plugin, _team1TowerLocs[2].clone().add(0, 1, 0));
                _team1Holograms[2].addLine(ChatColor.GREEN.toString() + LARGE_TOWER_HEALTH);
                _team1Holograms[2].addLine(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "HEALTH");
                _team2Holograms[0] = new Hologram(_plugin, _team2TowerLocs[0].clone().add(0, 1, 0));
                _team2Holograms[0].addLine(ChatColor.GREEN.toString() + SMALL_TOWER_HEALTH);
                _team2Holograms[0].addLine(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "HEALTH");
                _team2Holograms[1] = new Hologram(_plugin, _team2TowerLocs[1].clone().add(0, 1, 0));
                _team2Holograms[1].addLine(ChatColor.GREEN.toString() + MEDIUM_TOWER_HEALTH);
                _team2Holograms[1].addLine(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "HEALTH");
                _team2Holograms[2] = new Hologram(_plugin, _team2TowerLocs[2].clone().add(0, 1, 0));
                _team2Holograms[2].addLine(ChatColor.GREEN.toString() + LARGE_TOWER_HEALTH);
                _team2Holograms[2].addLine(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "HEALTH");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerHitCrystal(EntityDamageByEntityEvent event)
    {
        // Hit a crystal
        if (_gameManager.getCurrentGame() == _towerDefense && _gameManager.getGameState() == GameState.IN_GAME &&
                event.getEntity().getType() == EntityType.ENDER_CRYSTAL)
        {
            if (!(event.getDamager() instanceof Player))
            {
                event.setCancelled(true);
                return;
            }
            Player damager = (Player) event.getDamager();

            // Players can only hit twice a second
            boolean canDamage = !Cooldown.getInstance().hasCooldown(getHitCooldownName(damager));
            int damage = (int) event.getDamage();
            // Find the tower damaged and ensures the enemy damaged the tower
            if (_team1TowerLocs[_team1ActiveTower].equals(event.getEntity().getLocation()) && canDamage &&
                    _gameManager.getTeamManager().getAllPlayerTeams().get(ColorTeamNames.values()[1]).contains(damager))
            {
                // Damage the tower
                _team1Towers[_team1ActiveTower].damageTower(damage);
                // Players can only hit twice a second
                Cooldown.getInstance().createCooldown(getHitCooldownName(damager), 500L);
                _team1TowerLocs[_team1ActiveTower].getWorld().playSound(_team1TowerLocs[_team1ActiveTower], Sound.ITEM_BREAK, 1f, (new Random().nextFloat() / 3) + 0.5f);

                // Check if tower is dead
                if (_team1Towers[_team1ActiveTower].getHealth() <= 0)
                {
                    blowUpTower(_team1TowerLocs[_team1ActiveTower], _team1Towers[_team1ActiveTower].getCrystal());
                    _team1Holograms[_team1ActiveTower].deleteHologram();
                    _team1ActiveTower++;
                    if (_team1ActiveTower > 2)
                    {
                        _towerDefense.team1Wins();
                    }
                    event.setCancelled(true);
                }
                else
                {
                    event.setCancelled(true);
                }
            }
            else if (_team2TowerLocs[_team2ActiveTower].equals(event.getEntity().getLocation()) && canDamage &&
                    _gameManager.getTeamManager().getAllPlayerTeams().get(ColorTeamNames.values()[0]).contains(damager))
            {
                _team2Towers[_team2ActiveTower].damageTower(damage);
                Cooldown.getInstance().createCooldown(getHitCooldownName(damager), 500L);
                _team2TowerLocs[_team2ActiveTower].getWorld().playSound(_team2TowerLocs[_team2ActiveTower], Sound.ITEM_BREAK, 1f, (new Random().nextFloat() / 3) + 0.5f);
                // Check if tower is dead
                if (_team2Towers[_team2ActiveTower].getHealth() <= 0)
                {
                    blowUpTower(_team2TowerLocs[_team2ActiveTower], _team2Towers[_team2ActiveTower].getCrystal());
                    _team2Holograms[_team2ActiveTower].deleteHologram();
                    _team2ActiveTower++;
                    if (_team2ActiveTower > 2)
                    {
                        _towerDefense.team2Wins();
                    }
                    event.setCancelled(true);
                }
                else
                {
                    event.setCancelled(true);
                }
            }
            else
            {
                event.setCancelled(true);
            }

            // Update holograms
            _team1Holograms[0].updateLine(0,
                    getTowerHealthColor(_team1Towers[0].getHealth(), SMALL_TOWER_HEALTH).toString() + _team1Towers[0].getHealth());
            _team1Holograms[1].updateLine(0,
                    getTowerHealthColor(_team1Towers[1].getHealth(), MEDIUM_TOWER_HEALTH).toString() + _team1Towers[1].getHealth());
            _team1Holograms[2].updateLine(0,
                    getTowerHealthColor(_team1Towers[2].getHealth(), LARGE_TOWER_HEALTH).toString() + _team1Towers[2].getHealth());
            _team2Holograms[0].updateLine(0,
                    getTowerHealthColor(_team2Towers[0].getHealth(), SMALL_TOWER_HEALTH).toString() + _team2Towers[0].getHealth());
            _team2Holograms[1].updateLine(0,
                    getTowerHealthColor(_team2Towers[1].getHealth(), MEDIUM_TOWER_HEALTH).toString() + _team2Towers[1].getHealth());
            _team2Holograms[2].updateLine(0,
                    getTowerHealthColor(_team2Towers[2].getHealth(), LARGE_TOWER_HEALTH).toString() + _team2Towers[2].getHealth());
        }
    }

    /**
     * Creates an explosion that blows up the tower.
     *
     * @param towerLoc the location of the tower
     */
    public void blowUpTower(Location towerLoc, Entity crystal)
    {
        crystal.remove();
        Bukkit.getScheduler().runTaskLater(_plugin, new Runnable() {
            @Override
            public void run()
            {
                towerLoc.getWorld().createExplosion(towerLoc.clone().add(0, 1, 0), 4f, false);
            }
        }, 1L);
    }

    public String getTeam1TowerHealthBar()
    {
        return  getTowerHealthColor(_team1Towers[0].getHealth(), TowerController.SMALL_TOWER_HEALTH) + "♜" +
                getTowerHealthColor(_team1Towers[1].getHealth(), TowerController.MEDIUM_TOWER_HEALTH) + "♜" +
                getTowerHealthColor(_team1Towers[2].getHealth(), TowerController.LARGE_TOWER_HEALTH) + "♜";
    }

    public String getTeam2TowerHealthBar()
    {
        return  getTowerHealthColor(_team2Towers[0].getHealth(), TowerController.SMALL_TOWER_HEALTH) + "♜" +
                getTowerHealthColor(_team2Towers[1].getHealth(), TowerController.MEDIUM_TOWER_HEALTH) + "♜" +
                getTowerHealthColor(_team2Towers[2].getHealth(), TowerController.LARGE_TOWER_HEALTH) + "♜";
    }

    private String getHitCooldownName(Player player)
    {
        return "towerdefense.crystalcooldown." + player.getName();
    }

    public ChatColor getTowerHealthColor(int health, double startingHealth)
    {
        if ((double) health / (double) startingHealth > 0.66D)
        {
            return ChatColor.GREEN;
        }
        else if ((double) health / (double) startingHealth > 0.33D)
        {
            return ChatColor.YELLOW;
        }
        else if ((double) health / (double) startingHealth > 0.001D)
        {
            return ChatColor.RED;
        }
        else
        {
            return ChatColor.DARK_GRAY;
        }
    }

}
