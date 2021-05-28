package game.gamelist.towerdefense.region;

import core.minecraft.region.RegionManager;
import core.minecraft.region.flags.BlockBreakInRegionEvent;
import core.minecraft.region.flags.BlockBreakInRegionFlag;
import core.minecraft.region.flags.BlockPlaceInRegionEvent;
import core.minecraft.region.flags.BlockPlaceInRegionFlag;
import core.minecraft.region.type.CuboidRegion;
import core.minecraft.world.config.MapConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class TowerDefenseRegionManager implements Listener
{

    private RegionManager _regionManager;
    private MapConfig _map;

    public static final String TEAM1_SMALL_TOWER_REGION = "towerdefense.team1smalltowerregion";
    public static final String TEAM1_MEDIUM_TOWER_REGION = "towerdefense.team1mediumtowerregion";
    public static final String TEAM1_LARGE_TOWER_REGION = "towerdefense.team1largetowerregion";
    public static final String TEAM1_WITHER_ALTER_REGION = "towerdefense.team1witheralterregion";
    public static final String TEAM1_SPAWN_REGION = "towerdefense.team1spawnregion";

    public static final String TEAM2_SMALL_TOWER_REGION = "towerdefense.team2smalltowerregion";
    public static final String TEAM2_MEDIUM_TOWER_REGION = "towerdefense.team2mediumtowerregion";
    public static final String TEAM2_LARGE_TOWER_REGION = "towerdefense.team2largetowerregion";
    public static final String TEAM2_WITHER_ALTER_REGION = "towerdefense.team2witheralterregion";
    public static final String TEAM2_SPAWN_REGION = "towerdefense.team2spawnregion";

    public static final String CENTER_SIDE1_REGION = "towerdefense.centerside1region";
    public static final String CENTER_SIDE2_REGION = "towerdefense.centerside2region";

    public TowerDefenseRegionManager(RegionManager regionManager, MapConfig map, JavaPlugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _regionManager = regionManager;
        _map = map;

        setupRegions();
    }

    private void setupRegions()
    {
        ArrayList<String> flags = new ArrayList<>();
        flags.add(BlockBreakInRegionFlag.FLAG_ID);
        flags.add(BlockPlaceInRegionFlag.FLAG_ID);

        // Team1 small tower region
        String[] team1S = ((String) _map.getConfigurationSetting("TEAM1_SMALL_TOWER_REGION1")).split(",");
        Location team1SLoc1 = new Location(_map.getWorld(), Double.parseDouble(team1S[0]), 0, Double.parseDouble(team1S[1]));
        team1S = ((String) _map.getConfigurationSetting("TEAM1_SMALL_TOWER_REGION2")).split(",");
        Location team1SLoc2 = new Location(_map.getWorld(), Double.parseDouble(team1S[0]), 256, Double.parseDouble(team1S[1]));
        CuboidRegion team1SRegion = new CuboidRegion(flags, "Tower", TEAM1_SMALL_TOWER_REGION, _map.getWorld(), team1SLoc1, team1SLoc2);
        _regionManager.createRegion(team1SRegion);

        // Team1 medium tower region
        String[] team1M = ((String) _map.getConfigurationSetting("TEAM1_MEDIUM_TOWER_REGION1")).split(",");
        Location team1MLoc1 = new Location(_map.getWorld(), Double.parseDouble(team1M[0]), 0, Double.parseDouble(team1M[1]));
        team1M = ((String) _map.getConfigurationSetting("TEAM1_MEDIUM_TOWER_REGION2")).split(",");
        Location team1MLoc2 = new Location(_map.getWorld(), Double.parseDouble(team1M[0]), 256, Double.parseDouble(team1M[1]));
        CuboidRegion team1MRegion = new CuboidRegion(flags, "Tower", TEAM1_MEDIUM_TOWER_REGION, _map.getWorld(), team1MLoc1, team1MLoc2);
        _regionManager.createRegion(team1MRegion);

        // Team1 large tower region
        String[] team1L = ((String) _map.getConfigurationSetting("TEAM1_LARGE_TOWER_REGION1")).split(",");
        Location team1LLoc1 = new Location(_map.getWorld(), Double.parseDouble(team1L[0]), 0, Double.parseDouble(team1L[1]));
        team1L = ((String) _map.getConfigurationSetting("TEAM1_LARGE_TOWER_REGION2")).split(",");
        Location team1LLoc2 = new Location(_map.getWorld(), Double.parseDouble(team1L[0]), 256, Double.parseDouble(team1L[1]));
        CuboidRegion team1LRegion = new CuboidRegion(flags, "Tower", TEAM1_LARGE_TOWER_REGION, _map.getWorld(), team1LLoc1, team1LLoc2);
        _regionManager.createRegion(team1LRegion);

        // Team1 wither alter region
        String[] team1W = ((String) _map.getConfigurationSetting("TEAM1_WITHER_ALTER_REGION1")).split(",");
        Location team1WLoc1 = new Location(_map.getWorld(), Double.parseDouble(team1W[0]), 0, Double.parseDouble(team1W[1]));
        team1W = ((String) _map.getConfigurationSetting("TEAM1_WITHER_ALTER_REGION2")).split(",");
        Location team1WLoc2 = new Location(_map.getWorld(), Double.parseDouble(team1W[0]), 256, Double.parseDouble(team1W[1]));
        CuboidRegion team1WRegion = new CuboidRegion(flags, "Tower", TEAM1_WITHER_ALTER_REGION, _map.getWorld(), team1WLoc1, team1WLoc2);
        _regionManager.createRegion(team1WRegion);

        // Team1 spawn region
        String[] team1 = ((String) _map.getConfigurationSetting("TEAM1_SPAWN_REGION1")).split(",");
        Location team1Loc1 = new Location(_map.getWorld(), Double.parseDouble(team1[0]), 0, Double.parseDouble(team1[1]));
        team1 = ((String) _map.getConfigurationSetting("TEAM1_SPAWN_REGION2")).split(",");
        Location team1Loc2 = new Location(_map.getWorld(), Double.parseDouble(team1[0]), 256, Double.parseDouble(team1[1]));
        CuboidRegion team1Region = new CuboidRegion(flags, "Tower", TEAM1_SPAWN_REGION, _map.getWorld(), team1Loc1, team1Loc2);
        _regionManager.createRegion(team1Region);

        // Team2 small tower region
        String[] team2S = ((String) _map.getConfigurationSetting("TEAM2_SMALL_TOWER_REGION1")).split(",");
        Location team2SLoc1 = new Location(_map.getWorld(), Double.parseDouble(team2S[0]), 0, Double.parseDouble(team2S[1]));
        team2S = ((String) _map.getConfigurationSetting("TEAM2_SMALL_TOWER_REGION2")).split(",");
        Location team2SLoc2 = new Location(_map.getWorld(), Double.parseDouble(team2S[0]), 256, Double.parseDouble(team2S[1]));
        CuboidRegion team2SRegion = new CuboidRegion(flags, "Tower", TEAM2_SMALL_TOWER_REGION, _map.getWorld(), team2SLoc1, team2SLoc2);
        _regionManager.createRegion(team2SRegion);

        // Team2 medium tower region
        String[] team2M = ((String) _map.getConfigurationSetting("TEAM2_MEDIUM_TOWER_REGION1")).split(",");
        Location team2MLoc1 = new Location(_map.getWorld(), Double.parseDouble(team2M[0]), 0, Double.parseDouble(team2M[1]));
        team2M = ((String) _map.getConfigurationSetting("TEAM2_MEDIUM_TOWER_REGION2")).split(",");
        Location team2MLoc2 = new Location(_map.getWorld(), Double.parseDouble(team2M[0]), 256, Double.parseDouble(team2M[1]));
        CuboidRegion team2MRegion = new CuboidRegion(flags, "Tower", TEAM2_MEDIUM_TOWER_REGION, _map.getWorld(), team2MLoc1, team2MLoc2);
        _regionManager.createRegion(team2MRegion);

        // Team2 large tower region
        String[] team2L = ((String) _map.getConfigurationSetting("TEAM2_LARGE_TOWER_REGION1")).split(",");
        Location team2LLoc1 = new Location(_map.getWorld(), Double.parseDouble(team2L[0]), 0, Double.parseDouble(team2L[1]));
        team2L = ((String) _map.getConfigurationSetting("TEAM2_LARGE_TOWER_REGION2")).split(",");
        Location team2LLoc2 = new Location(_map.getWorld(), Double.parseDouble(team2L[0]), 256, Double.parseDouble(team2L[1]));
        CuboidRegion team2LRegion = new CuboidRegion(flags, "Tower", TEAM2_LARGE_TOWER_REGION, _map.getWorld(), team2LLoc1, team2LLoc2);
        _regionManager.createRegion(team2LRegion);

        // Team2 wither alter region
        String[] team2W = ((String) _map.getConfigurationSetting("TEAM2_WITHER_ALTER_REGION1")).split(",");
        Location team2WLoc1 = new Location(_map.getWorld(), Double.parseDouble(team2W[0]), 0, Double.parseDouble(team2W[1]));
        team2W = ((String) _map.getConfigurationSetting("TEAM2_WITHER_ALTER_REGION2")).split(",");
        Location team2WLoc2 = new Location(_map.getWorld(), Double.parseDouble(team2W[0]), 256, Double.parseDouble(team2W[1]));
        CuboidRegion team2WRegion = new CuboidRegion(flags, "Tower", TEAM2_WITHER_ALTER_REGION, _map.getWorld(), team2WLoc1, team2WLoc2);
        _regionManager.createRegion(team2WRegion);

        // Team2 spawn region
        String[] team2 = ((String) _map.getConfigurationSetting("TEAM2_SPAWN_REGION1")).split(",");
        Location team2Loc1 = new Location(_map.getWorld(), Double.parseDouble(team2[0]), 0, Double.parseDouble(team2[1]));
        team2 = ((String) _map.getConfigurationSetting("TEAM2_SPAWN_REGION2")).split(",");
        Location team2Loc2 = new Location(_map.getWorld(), Double.parseDouble(team2[0]), 256, Double.parseDouble(team2[1]));
        CuboidRegion team2Region = new CuboidRegion(flags, "Tower", TEAM2_SPAWN_REGION, _map.getWorld(), team2Loc1, team2Loc2);
        _regionManager.createRegion(team2Region);

        // Center side1 region
        String[] center1 = ((String) _map.getConfigurationSetting("CENTER_SIDE1_REGION1")).split(",");
        Location center1Loc1 = new Location(_map.getWorld(), Double.parseDouble(center1[0]), 0, Double.parseDouble(center1[1]));
        center1 = ((String) _map.getConfigurationSetting("CENTER_SIDE1_REGION2")).split(",");
        Location center1Loc2 = new Location(_map.getWorld(), Double.parseDouble(center1[0]), 256, Double.parseDouble(center1[1]));
        CuboidRegion center1Region = new CuboidRegion(flags, "Tower", CENTER_SIDE1_REGION, _map.getWorld(), center1Loc1, center1Loc2);
        _regionManager.createRegion(center1Region);

        // Center side2 region
        String[] center2 = ((String) _map.getConfigurationSetting("CENTER_SIDE2_REGION1")).split(",");
        Location center2Loc1 = new Location(_map.getWorld(), Double.parseDouble(center2[0]), 0, Double.parseDouble(center2[1]));
        center2 = ((String) _map.getConfigurationSetting("CENTER_SIDE2_REGION2")).split(",");
        Location center2Loc2 = new Location(_map.getWorld(), Double.parseDouble(center2[0]), 256, Double.parseDouble(center2[1]));
        CuboidRegion center2Region = new CuboidRegion(flags, "Tower", CENTER_SIDE2_REGION, _map.getWorld(), center2Loc1, center2Loc2);
        _regionManager.createRegion(center2Region);
    }

    @EventHandler
    public void onBlockBreakInRegion(BlockBreakInRegionEvent event)
    {
        if (event.getRegionID().equals(TEAM1_SMALL_TOWER_REGION) ||
                event.getRegionID().equals(TEAM1_MEDIUM_TOWER_REGION) ||
                event.getRegionID().equals(TEAM1_LARGE_TOWER_REGION) ||
                event.getRegionID().equals(TEAM1_WITHER_ALTER_REGION) ||
                event.getRegionID().equals(TEAM1_SPAWN_REGION) ||
                event.getRegionID().equals(TEAM2_SMALL_TOWER_REGION) ||
                event.getRegionID().equals(TEAM2_MEDIUM_TOWER_REGION) ||
                event.getRegionID().equals(TEAM2_LARGE_TOWER_REGION) ||
                event.getRegionID().equals(TEAM2_WITHER_ALTER_REGION) ||
                event.getRegionID().equals(TEAM2_SPAWN_REGION) ||
                event.getRegionID().equals(CENTER_SIDE1_REGION) ||
                event.getRegionID().equals(CENTER_SIDE2_REGION))
        {
            event.getBlockBreakEvent().setCancelled(true);
            event.getBlockBreakEvent().getPlayer().sendMessage(ChatColor.GRAY + "This block is protected by a magical force");
        }
    }

    @EventHandler
    public void onBlockPlaceInRegion(BlockPlaceInRegionEvent event)
    {
        if (event.getRegionID().equals(TEAM1_SMALL_TOWER_REGION) ||
                event.getRegionID().equals(TEAM1_MEDIUM_TOWER_REGION) ||
                event.getRegionID().equals(TEAM1_LARGE_TOWER_REGION) ||
                event.getRegionID().equals(TEAM1_WITHER_ALTER_REGION) ||
                event.getRegionID().equals(TEAM1_SPAWN_REGION) ||
                event.getRegionID().equals(TEAM2_SMALL_TOWER_REGION) ||
                event.getRegionID().equals(TEAM2_MEDIUM_TOWER_REGION) ||
                event.getRegionID().equals(TEAM2_LARGE_TOWER_REGION) ||
                event.getRegionID().equals(TEAM2_WITHER_ALTER_REGION) ||
                event.getRegionID().equals(TEAM2_SPAWN_REGION) ||
                event.getRegionID().equals(CENTER_SIDE1_REGION) ||
                event.getRegionID().equals(CENTER_SIDE2_REGION))
        {
            event.getBlockPlaceEvent().setCancelled(true);
            event.getBlockPlaceEvent().getPlayer().sendMessage(ChatColor.GRAY + "This block is protected by a magical force");
        }
    }

}
