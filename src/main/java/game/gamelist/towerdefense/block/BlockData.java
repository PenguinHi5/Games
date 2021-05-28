package game.gamelist.towerdefense.block;

import game.common.ColorTeamNames;
import org.bukkit.entity.Player;

public class BlockData
{

    public Player owner;
    public ColorTeamNames team;

    public BlockData(Player owner, ColorTeamNames team)
    {
        this.owner = owner;
        this.team = team;
    }

}
