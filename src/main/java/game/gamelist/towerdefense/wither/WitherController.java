package game.gamelist.towerdefense.wither;

import game.common.EntityTypes;
import game.gamelist.towerdefense.TowerDefense;
import game.gamelist.towerdefense.wither.v1_8_R3.TowerAttackWither;
import org.bukkit.Location;

public class WitherController
{

    private TowerDefense _towerDefense;
    private TowerAttackWither _team1Wither, _team2Wither;

    public WitherController(TowerDefense towerDefense)
    {
        _towerDefense = towerDefense;
    }

    public void spawnTeam1Wither()
    {
        String spawnLocS = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM1_WITHER_SPAWN");
        String[] split = spawnLocS.split(",");
        Location spawnLoc = new Location(_towerDefense.getCurrentMap().getWorld(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        _team1Wither = new TowerAttackWither(_towerDefense.getCurrentMap().getWorld());
        EntityTypes.spawnEntity(_team1Wither, spawnLoc);
    }

    public void spawnTeam2Wither()
    {
        String spawnLocS = (String) _towerDefense.getCurrentMap().getConfigurationSetting("TEAM2_WITHER_SPAWN");
        String[] split = spawnLocS.split(",");
        Location spawnLoc = new Location(_towerDefense.getCurrentMap().getWorld(), Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        _team2Wither = new TowerAttackWither(_towerDefense.getCurrentMap().getWorld());
        EntityTypes.spawnEntity(_team2Wither, spawnLoc);
    }

}
