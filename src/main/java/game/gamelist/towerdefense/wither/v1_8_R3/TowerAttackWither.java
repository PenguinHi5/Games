package game.gamelist.towerdefense.wither.v1_8_R3;

import game.common.ReflectionUtil;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.util.List;

public class TowerAttackWither extends EntityWither
{

    public TowerAttackWither(org.bukkit.World world)
    {
        super(((CraftWorld)world).getHandle());

        List goalB = (List) ReflectionUtil.getPrivateField("b", PathfinderGoalSelector.class, goalSelector);
        goalB.clear();
        List goalC = (List) ReflectionUtil.getPrivateField("c", PathfinderGoalSelector.class, goalSelector);
        goalC.clear();
        List targetB = (List) ReflectionUtil.getPrivateField("b", PathfinderGoalSelector.class, targetSelector);
        targetB.clear();
        List targetC = (List) ReflectionUtil.getPrivateField("c", PathfinderGoalSelector.class, targetSelector);
        targetC.clear();
    }

}
