package game.gamelist.towerdefense;

import core.minecraft.world.config.MapConfig;
import game.common.ColorTeamNames;
import game.respawn.RespawnController;
import game.respawn.event.OnDeathPlayerRespawnEvent;
import game.respawn.event.OnRespawnPlayerRespawnEvent;
import game.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class TowerDefenseRespawnManager implements Listener
{

    private HashMap<Player, ArrayList<ItemStack>> _respawnInventories = new HashMap<>();
    private HashMap<Player, ItemStack[]> _respawnArmor = new HashMap<>();
    private MapConfig _map;
    private TowerDefense _towerDefense;
    private TeamManager _teamManager;
    private RespawnController _respawnController;

    public TowerDefenseRespawnManager(JavaPlugin plugin, TowerDefense towerDefense, TeamManager teamManager, MapConfig map)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _respawnController = new RespawnController(plugin, 10L * 1000L, teamManager, map);
        _towerDefense = towerDefense;
        _map = map;
        _teamManager = teamManager;
    }

    @EventHandler
    public void onPlayerRespawn(OnRespawnPlayerRespawnEvent event)
    {
        // Return player's items
        for (ItemStack item : _respawnInventories.get(event.getPlayer()))
        {
            event.getPlayer().getInventory().addItem(item);
        }
        event.getPlayer().getInventory().setArmorContents(_respawnArmor.get(event.getPlayer()));
        // Ensure player has all armor pieces
        ItemStack[] armor = _towerDefense.getDefaultArmor(event.getPlayer());
        event.getPlayer().getInventory().setHelmet(armor[0]);
        event.getPlayer().getInventory().setChestplate(armor[1]);
        event.getPlayer().getInventory().setLeggings(armor[2]);
        event.getPlayer().getInventory().setBoots(armor[3]);
    }

    @EventHandler
    public void onPlayerDamage(OnDeathPlayerRespawnEvent event)
    {
        _respawnArmor.put(event.getPlayer(), event.getPlayer().getInventory().getArmorContents().clone());
        ItemStack strongestMelee = new ItemStack(Material.STONE_SWORD, 1);
        ItemStack strongestPic = new ItemStack(Material.STONE_PICKAXE, 1);
        ArrayList<ItemStack> savedMaterials = new ArrayList<>();
        for (ItemStack item : event.getPlayer().getInventory().getContents())
        {
            if (item == null)
                continue;

            if (item.getType().name().contains("SWORD"))
            {
                strongestMelee = compareTools(strongestMelee, item, true);
            }
            else if (item.getType().name().contains("PICKAXE"))
            {
                strongestPic = compareTools(strongestPic, item, false);
            }
            else if (item.getType() == Material.BOW)
            {
                savedMaterials.add(0, item);
            }
            else if (item.getType() == Material.FISHING_ROD)
            {
                savedMaterials.add(0, item);
            }
            else if (item.getType() == Material.ARROW && item.getAmount() > 1)
            {
                ItemStack newItem = item.clone();
                newItem.setAmount(item.getAmount() / 2);
                savedMaterials.add(newItem);
            }
            else if (item.getType() == Material.DIAMOND && item.getAmount() > 1)
            {
                ItemStack newItem = item.clone();
                newItem.setAmount(item.getAmount() / 2);
                savedMaterials.add(newItem);
            }
            else if (item.getType() == Material.IRON_ORE && item.getAmount() > 1)
            {
                ItemStack newItem = item.clone();
                newItem.setAmount(item.getAmount() / 2);
                savedMaterials.add(newItem);
            }
            else if (item.getType() == Material.IRON_INGOT && item.getAmount() > 1)
            {
                ItemStack newItem = item.clone();
                newItem.setAmount(item.getAmount() / 2);
                savedMaterials.add(newItem);
            }
        }
        ArrayList<ItemStack> itemList = new ArrayList<>();
        itemList.add(strongestMelee);
        itemList.add(strongestPic);
        itemList.addAll(savedMaterials);
        _respawnInventories.put(event.getPlayer(), itemList);

        // Clear player's inventory on death
        event.getPlayer().getInventory().clear();
    }

    @EventHandler
    public void manageDropsOnDeath(PlayerDeathEvent event)
    {
        ArrayList<ItemStack> remove = new ArrayList<>();
        for (ItemStack item : event.getDrops())
        {
            if (item.getType() == Material.DIAMOND_SWORD ||
                    item.getType().name().contains("PICKAXE") ||
                    item.getType() == Material.BOW ||
                    item.getType() == Material.FISHING_ROD ||
                    item.getType().name().contains("HELMET") ||
                    item.getType().name().contains("CHESTPLATE") ||
                    item.getType().name().contains("LEGGINGS") ||
                    item.getType().name().contains("BOOTS"))
            {
                remove.add(item);
            }
            else if (item.getType() == Material.ARROW ||
                    item.getType() == Material.DIAMOND ||
                    item.getType() == Material.IRON_ORE ||
                    item.getType() == Material.IRON_INGOT)
            {
                item.setAmount((item.getAmount() + 1) / 2);
            }
        }
        event.getDrops().removeAll(remove);
    }

    private ItemStack compareTools(ItemStack tool1, ItemStack tool2, boolean ignoreDiamond)
    {
        if (tool1.getType().name().contains("DIAMOND") && !ignoreDiamond)
        {
            return tool1;
        }
        else if (tool2.getType().name().contains("DIAMOND") && !ignoreDiamond)
        {
            return tool2;
        }
        else if (tool1.getType().name().contains("IRON"))
        {
            return tool1;
        }
        else if (tool2.getType().name().contains("IRON"))
        {
            return tool2;
        }
        else
        {
            if (tool1.getType().name().contains("SWORD"))
            {
                return new ItemStack(Material.STONE_SWORD, 1);
            }
            else if (tool1.getType().name().contains("PICKAXE"))
            {
                return new ItemStack(Material.STONE_PICKAXE, 1);
            }
            else if (tool1.getType().name().contains("AXE"))
            {
                return new ItemStack(Material.STONE_AXE, 1);
            }
        }
        return tool1;
    }

    @EventHandler
    public void teleportPlayerOnRespawn(OnRespawnPlayerRespawnEvent event)
    {
        // Teleport player to spawn
        List<Location> teleportLocations;
        if (_teamManager.getPlayersTeam(event.getPlayer()) == ColorTeamNames.values()[0]) // Player is on team 1
        {
            teleportLocations = _map.getTeamSpawnLocations("TEAM1");
        }
        else // Player is on team 2
        {
            teleportLocations = _map.getTeamSpawnLocations("TEAM2");
        }
        event.getPlayer().teleport(teleportLocations.get(new Random().nextInt(teleportLocations.size())));
    }

}
