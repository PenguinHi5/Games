package game.gamelist.spleef;

import game.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.HashSet;

public class FallingBlockController implements Listener
{

    private TeamManager _teamManager;
    private JavaPlugin _plugin;
    private int _task;
    private HashSet<Block> _fallingBlocks = new HashSet<>();

    public FallingBlockController(JavaPlugin plugin, TeamManager teamManager)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _plugin = plugin;
        _teamManager = teamManager;
    }

    public void stopDetectingFallingBlocks()
    {
        Bukkit.getScheduler().cancelTask(_task);
        _fallingBlocks.clear();
    }

    public void detectFallingBlocks()
    {
        _task = Bukkit.getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable() {
            @Override
            public void run()
            {
                for (Player player : _teamManager.getLivingPlayers())
                {
                    HashSet<Block> blocks = new HashSet<>();
                    Location playerLoc = player.getLocation();
                    Block block1 = playerLoc.add(new Vector(0.25, -0.25, 0.25)).getBlock();
                    if (block1 != null)
                        blocks.add(block1);
                    Block block2 = playerLoc.add(new Vector(-0.5, 0, 0)).getBlock();
                    if (block2 != null)
                        blocks.add(block2);
                    Block block3 = playerLoc.add(new Vector(0, 0, -0.5)).getBlock();
                    if (block3 != null)
                        blocks.add(block3);
                    Block block4 = playerLoc.add(new Vector(0.5, 0, 0)).getBlock();
                    if (block4 != null)
                        blocks.add(block4);
                    for (Block block : blocks)
                    {
                        if (block.getType() == Material.STAINED_CLAY && !_fallingBlocks.contains(block))
                        {
                            block.setData((byte) 14);
                            spawnFallingBlock(block);
                        }
                    }
                }
            }
        }, 0L, 2L);
    }

    public void spawnFallingBlock(Block block)
    {
        _fallingBlocks.add(block);
        Bukkit.getScheduler().runTaskLater(_plugin, new Runnable() {
            @Override
            public void run()
            {
                Location blockLoc = block.getLocation();
                block.setType(Material.AIR);
                blockLoc.getWorld().spawnFallingBlock(blockLoc, Material.STAINED_CLAY, (byte) 14);
            }
        }, 20L);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event)
    {
        if (event.getEntity() instanceof FallingBlock)
        {
            Bukkit.getScheduler().runTaskLater(_plugin, new Runnable() {
                @Override
                public void run()
                {
                    event.getBlock().setType(Material.AIR);
                }
            }, 1L);
        }
    }

}
