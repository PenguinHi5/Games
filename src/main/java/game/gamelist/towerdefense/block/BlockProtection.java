package game.gamelist.towerdefense.block;

import core.minecraft.common.F;
import core.minecraft.region.RegionManager;
import game.GameManager;
import game.gamelist.towerdefense.TowerDefense;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import game.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class BlockProtection implements Listener
{

    private HashMap<Block, BlockData> _protectedBlocks;
    private TeamManager _teamManager;
    private GameManager _gameManager;
    private RegionManager _regionManager;
    private TowerDefense _towerDefense;

    public BlockProtection(TowerDefense towerDefense, GameManager gameManager, TeamManager teamManager, JavaPlugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _teamManager = teamManager;
        _gameManager = gameManager;
        _towerDefense = towerDefense;
        _protectedBlocks = new HashMap<>();
    }

    @EventHandler
    public void onPlaceFurnace(BlockPlaceEvent event)
    {
        // Ignore if we are not currently playing Tower Defense
        if (_gameManager.getCurrentGame() != _towerDefense || _gameManager.getGameState() != GameState.IN_GAME)
        {
            return;
        }

        if (event.getBlock().getType() == Material.FURNACE)
        {
            _protectedBlocks.put(event.getBlockPlaced(), new BlockData(event.getPlayer(), _teamManager.getPlayersTeam(event.getPlayer())));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // Ignore if we are not currently playing Tower Defense
        if (_gameManager.getCurrentGame() != _towerDefense || _gameManager.getGameState() != GameState.IN_GAME)
        {
            return;
        }

        // The player interacted with a furnace
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK &&
                event.getClickedBlock().getType() == Material.FURNACE && _protectedBlocks.containsKey(event.getClickedBlock()))
        {
            // If the furnace belongs to someone on their team but isn't theirs
            if (_teamManager.getPlayersTeam(event.getPlayer()) == _protectedBlocks.get(event.getClickedBlock()).team &&
                !_protectedBlocks.get(event.getClickedBlock()).owner.equals(event.getPlayer()))
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(F.errorMessage("Locked! This furnace belongs to " + _protectedBlocks.get(event.getClickedBlock()).owner.getName()));
            }
            else if (_teamManager.isDead(event.getPlayer()) || _teamManager.isSpectating(event.getPlayer()))
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        // Ignore if we are not currently playing Tower Defense
        if (_gameManager.getCurrentGame() != _towerDefense || _gameManager.getGameState() != GameState.IN_GAME)
        {
            return;
        }

        // Non-players can't break blocks
        if (_teamManager.isDead(event.getPlayer()) || _teamManager.isSpectating(event.getPlayer()))
        {
            event.setCancelled(true);
        }

        // If the furnace belongs to someone on their team but isn't theirs
        if (_protectedBlocks.containsKey(event.getBlock()) &&
                _teamManager.getPlayersTeam(event.getPlayer()) == _protectedBlocks.get(event.getBlock()).team &&
                !_protectedBlocks.get(event.getBlock()).owner.equals(event.getPlayer()))
        {
            event.setCancelled(true);
            event.getPlayer().sendMessage(F.errorMessage("Locked! This furnace belongs to " + _protectedBlocks.get(event.getBlock()).owner.getName()));
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        // Ignore if we are not currently playing Tower Defense
        if (!_gameManager.getCurrentGame().equals(_towerDefense) || _gameManager.getGameState() != GameState.IN_GAME)
        {
            return;
        }

        boolean isAboveVoid = true;
        Block block = event.getBlock();
        for (int i = block.getY(); i >= 0; i--)
        {
            Location loc = new Location(block.getWorld(), block.getX(), i, block.getZ());
            if (loc.getBlock().getType() != Material.AIR)
            {
                isAboveVoid = false;
                break;
            }
        }

        if (isAboveVoid)
            event.setCancelled(true);
    }

    @EventHandler
    public void clearData(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.POST_GAME && _gameManager.getCurrentGame().equals(_towerDefense))
        {
            _protectedBlocks.clear();
        }
    }



}
