package game.common;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.lang.reflect.Method;
import java.util.Random;

public class FireworkSpawner
{

    // internal references, performance improvements
    private static Method world_getHandle = null;
    private static Method nms_world_broadcastEntityEffect = null;
    private static Method firework_getHandle = null;
    private static FireworkEffect[] fireworkEffects = null;

    /**
     * Play a pretty firework at the location with the FireworkEffect when called
     * @param world
     * @param loc
     * @param fe
     * @throws Exception
     */
    public static void playFirework(World world, Location loc, FireworkEffect fireworkEffect, int power)
    {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.addEffect(fireworkEffect);
        fwm.setPower(1);
        fw.setFireworkMeta(fwm);
    }

    /**
     * Internal method, used as shorthand to grab our method in a nice friendly manner
     * @param cl
     * @param method
     * @return Method (or null)
     */
    private static Method getMethod(Class<?> cl, String method) {
        for(Method m : cl.getMethods()) {
            if(m.getName().equals(method)) {
                return m;
            }
        }
        return null;
    }

    public static FireworkEffect generateRandomEffect()
    {
        if (fireworkEffects == null)
        {
            populateFireworkEffects();
        }

        return fireworkEffects[new Random().nextInt(fireworkEffects.length)];
    }

    private static void populateFireworkEffects()
    {
        FireworkEffect[] effects = new FireworkEffect[]
                {
                        FireworkEffect.builder().withColor(Color.AQUA).build(),
                        FireworkEffect.builder().withColor(Color.BLUE).build(),
                        FireworkEffect.builder().withColor(Color.LIME).build(),
                        FireworkEffect.builder().withColor(Color.ORANGE).build(),
                        FireworkEffect.builder().withColor(Color.RED).build(),
                        FireworkEffect.builder().withColor(Color.YELLOW).build(),
                        FireworkEffect.builder().withColor(Color.AQUA).withFlicker().build(),
                        FireworkEffect.builder().withColor(Color.BLUE).withFlicker().build(),
                        FireworkEffect.builder().withColor(Color.LIME).withFlicker().build(),
                        FireworkEffect.builder().withColor(Color.ORANGE).withFlicker().build(),
                        FireworkEffect.builder().withColor(Color.RED).withFlicker().build(),
                        FireworkEffect.builder().withColor(Color.YELLOW).withFlicker().build(),
                        FireworkEffect.builder().withColor(Color.AQUA).withTrail().build(),
                        FireworkEffect.builder().withColor(Color.BLUE).withTrail().build(),
                        FireworkEffect.builder().withColor(Color.LIME).withTrail().build(),
                        FireworkEffect.builder().withColor(Color.ORANGE).withTrail().build(),
                        FireworkEffect.builder().withColor(Color.RED).withTrail().build(),
                        FireworkEffect.builder().withColor(Color.YELLOW).withTrail().build()
                };
        fireworkEffects = effects;
    }

}
