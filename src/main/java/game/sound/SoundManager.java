package game.sound;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SoundManager extends Component
{

    public SoundManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("game.Sound", plugin, commandManager);
    }

    public void playTuneForPlayer(Player player, float volume, float pitch)
    {
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, volume, pitch);
    }

    public void playTuneForEveryone(float volume, float pitch)
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            playTuneForPlayer(player, volume, pitch);
        }
    }

}
