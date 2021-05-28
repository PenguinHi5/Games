package game.gamelist.towerdefense.ore;

import core.minecraft.world.config.MapConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class OreSpawner
{

    private ArrayList<Block> _team1Iron;
    private ArrayList<Block> _team1Coal;
    private ArrayList<Block> _team1Gravel;
    private ArrayList<Block> _team2Iron;
    private ArrayList<Block> _team2Coal;
    private ArrayList<Block> _team2Gravel;
    private ArrayList<Block> _centerIron;

    private HashSet<Block> _team1IronSel;
    private HashSet<Block> _team1CoalSel;
    private HashSet<Block> _team1GravelSel;
    private HashSet<Block> _team2IronSel;
    private HashSet<Block> _team2CoalSel;
    private HashSet<Block> _team2GravelSel;
    private HashSet<Block> _centerIronSel;

    private ArrayList<Block> _diamond;
    private JavaPlugin _plugin;
    private MapConfig _map;

    public final static double CENTRAL_IRON_SPAWN_RATE = 0.35f;
    public final static double IRON_SPAWN_RATE = 0.075f;
    public final static double COAL_SPAWN_RATE = 0.25f;
    public final static double GRAVEL_SPAWN_RATE = 0.35f;

    public OreSpawner(JavaPlugin plugin, MapConfig map)
    {
        _plugin = plugin;
        _map = map;

        findAllBlocks();

        stonifyCoal();
        stonifyGravel();
        stonifyIron();

        selectBlocksForEachType();

        spawnIron();
        spawnGravel();
        spawnDiamond();
        spawnCoal();
    }

    public void spawnIron()
    {
        for (Block block : _team1IronSel)
        {
            block.setType(Material.IRON_ORE);
        }
        for (Block block : _team2IronSel)
        {
            block.setType(Material.IRON_ORE);
        }
        for (Block block : _centerIronSel)
        {
            block.setType(Material.IRON_ORE);
        }
    }

    public void spawnCoal()
    {
        for (Block block : _team1CoalSel)
        {
            block.setType(Material.COAL_ORE);
        }
        for (Block block : _team2CoalSel)
        {
            block.setType(Material.COAL_ORE);
        }
    }

    public void spawnGravel()
    {
        for (Block block : _team1GravelSel)
        {
            block.setType(Material.GRAVEL);
        }
        for (Block block : _team2GravelSel)
        {
            block.setType(Material.GRAVEL);
        }
    }

    public void spawnDiamond()
    {
        for (Block block : _diamond)
        {
            block.setType(Material.DIAMOND_ORE);
        }
    }

    public void stonifyIron()
    {
        for (Block block : _team1Iron)
        {
            block.setType(Material.STONE);
        }
        for (Block block : _team2Iron)
        {
            block.setType(Material.STONE);
        }
        for (Block block : _centerIron)
        {
            block.setType(Material.STONE);
        }
    }

    public void stonifyCoal()
    {
        for (Block block : _team1Coal)
        {
            block.setType(Material.STONE);
        }
        for (Block block : _team2Coal)
        {
            block.setType(Material.STONE);
        }
    }

    public void stonifyGravel()
    {
        for (Block block : _team1Gravel)
        {
            block.setType(Material.STONE);
        }
        for (Block block : _team2Gravel)
        {
            block.setType(Material.STONE);
        }
    }

    public void selectBlocksForEachType()
    {
        // Shuffle
        Collections.shuffle(_team1Iron);
        Collections.shuffle(_team1Coal);
        Collections.shuffle(_team1Gravel);
        Collections.shuffle(_team2Iron);
        Collections.shuffle(_team2Coal);
        Collections.shuffle(_team2Gravel);
        Collections.shuffle(_centerIron);

        // Iron
        int ironCount = (int) (IRON_SPAWN_RATE * (double) _team1Iron.size()) + 1;
        int offset = 0;
        for (int i = 0; i < Math.min(ironCount + offset, _team1Iron.size()); i++)
        {
            if (canPlaceIronOre(_team1Iron.get(i)))
            {
                _team1IronSel.add(_team1Iron.get(i));
            }
        }
        ironCount = (int) (IRON_SPAWN_RATE * (double) _team2Iron.size()) + 1;
        offset = 0;
        for (int i = 0; i < Math.min(ironCount + offset, _team2Iron.size()); i++)
        {
            if (canPlaceIronOre(_team2Iron.get(i)))
            {
                _team2IronSel.add(_team2Iron.get(i));
            }
        }
        ironCount = (int) (CENTRAL_IRON_SPAWN_RATE * (double) _centerIron.size()) + 1;
        for (int i = 0; i < ironCount; i++)
        {
            _centerIronSel.add(_centerIron.get(i));
        }

        // Coal
        int coalCount = (int) (COAL_SPAWN_RATE * (double) _team1Coal.size()) + 1;
        for (int i = 0; i < coalCount; i++)
        {
            _team1CoalSel.add(_team1Coal.get(i));
        }
        coalCount = (int) (COAL_SPAWN_RATE * (double) _team2Coal.size()) + 1;
        for (int i = 0; i < coalCount; i++)
        {
            _team2CoalSel.add(_team2Coal.get(i));
        }

        // Gravel
        int gravelCount = (int) (GRAVEL_SPAWN_RATE * (double) _team1Gravel.size()) + 1;
        for (int i = 0; i < gravelCount; i++)
        {
            _team1GravelSel.add(_team1Gravel.get(i));
        }
        gravelCount = (int) (GRAVEL_SPAWN_RATE * (double) _team2Gravel.size()) + 1;
        for (int i = 0; i < gravelCount; i++)
        {
            _team2GravelSel.add(_team2Gravel.get(i));
        }
    }

    public boolean canPlaceIronOre(Block block)
    {
        int ironCount = 0;
        for (int x = -1; x <= 1; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                for (int z = -1; z <= 1; z++)
                {
                    if (_team1IronSel.contains(block.getRelative(x, y, z)) || _team2IronSel.contains(block.getRelative(x, y, z)))
                    {
                        ironCount++;
                        if (ironCount > 1)
                        {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void findAllBlocks()
    {
        _team1Iron = new ArrayList<>();
        _team1Coal = new ArrayList<>();
        _team1Gravel = new ArrayList<>();
        _team2Iron = new ArrayList<>();
        _team2Coal = new ArrayList<>();
        _team2Gravel = new ArrayList<>();
        _centerIron = new ArrayList<>();
        _diamond = new ArrayList<>();

        _team1IronSel = new HashSet<>();
        _team1CoalSel = new HashSet<>();
        _team1GravelSel = new HashSet<>();
        _team2IronSel = new HashSet<>();
        _team2CoalSel = new HashSet<>();
        _team2GravelSel = new HashSet<>();
        _centerIronSel = new HashSet<>();

        World world = _map.getWorld();
        for (int x = _map.getMinX(); x < _map.getMaxX() + 1; x++)
        {
            for (int y = _map.getMinY(); y < _map.getMaxY() + 1; y++)
            {
                for (int z = _map.getMinZ(); z < _map.getMaxZ() + 1; z++)
                {
                    Block block = world.getBlockAt(x, y, z);
                    switch (block.getType())
                    {
                        case IRON_ORE:
                            if (x < -5)
                            {
                                _team1Iron.add(block);
                            }
                            else if (x > 3)
                            {
                                _team2Iron.add(block);
                            }
                            else
                            {
                                _centerIron.add(block);
                            }
                            break;

                        case COAL_ORE:
                            if (x < 0)
                            {
                                _team1Coal.add(block);
                            }
                            else
                            {
                                _team2Coal.add(block);
                            }
                            break;

                        case GRAVEL:
                            if (x < 0)
                            {
                                _team1Gravel.add(block);
                            }
                            else
                            {
                                _team2Gravel.add(block);
                            }
                            break;

                        case DIAMOND_ORE:
                            _diamond.add(block);
                            break;
                    }
                }
            }
        }
    }

}
