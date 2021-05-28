package game.settings;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.damage.events.CustomDamageEvent;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class GameSettingsManager extends Component implements Listener {

    private GameSettings _gameSettings;

    public GameSettingsManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Game Settings", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void applyGameSettings(GameSettings newSettings)
    {
        _gameSettings = newSettings;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event)
    {
        if (!_gameSettings.allowBlockPlace &&
                (!_gameSettings.allowLimitedBlockPlace || !_gameSettings.limitedBlockPlaceSet.contains(event.getItemInHand().getType())))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event)
    {
        if (!_gameSettings.allowBlockBreak &&
                (!_gameSettings.allowLimitedBlockBreak || !_gameSettings.limitedBlockBreakSet.contains(event.getBlock().getType())))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamage(CustomDamageEvent event)
    {
        // Checks for fall damage
        if (!_gameSettings.enableFallDamage && event.getDamageCause() == EntityDamageEvent.DamageCause.FALL)
        {
            event.setCancelled("Fall Damage Disabled");
        }

        // Checks for explosion damage
        if (!_gameSettings.enableExplosionPlayerDamage &&
                (event.getDamageCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                 event.getDamageCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
        {
            event.setCancelled("Explosion Damage Disabled");
        }

        // Checks for fire damage
        if (!_gameSettings.enableFireDamage &&
                (event.getDamageCause() == EntityDamageEvent.DamageCause.FIRE ||
                 event.getDamageCause() == EntityDamageEvent.DamageCause.FIRE_TICK))
        {
            event.setCancelled("Fire Damage Disabled");
        }

        // Checks for lava damage
        if (!_gameSettings.enableLavaDamage && event.getDamageCause() == EntityDamageEvent.DamageCause.LAVA)
        {
            event.setCancelled(true);
        }

        // Checks for pvp
        if (!_gameSettings.pvp && event.getEntityDamager() instanceof Player && event.getEntityDamagee() instanceof Player)
        {
            event.setCancelled("PVP Disabled");
        }
        // Checks for pve
        else if (!_gameSettings.pve && event.getPlayerDamager() != null && event.getPlayerDamagee() == null && event.getEntityDamagee() != null)
        {
            event.setCancelled("PVE Disabled");
        }
        // Checks for evp
        else if (!_gameSettings.evp && event.getPlayerDamagee() != null && event.getPlayerDamager() == null && event.getEntityDamager() != null)
        {
            event.setCancelled("EVP Disabled");
        }
    }

    @EventHandler
    public void checkGameModes(TimerEvent event)
    {
        // Check every second
        if (event.getType() != TimerType.SECOND)
            return;

        for (Player player : Bukkit.getOnlinePlayers())
        {
            GameMode newGM;
            if (_gameSettings.allowCustomGameModes && _gameSettings.customGameModes.containsKey(player))
            {
                newGM = _gameSettings.customGameModes.get(player);
            }
            else
            {
                newGM = _gameSettings.defaultGameMode;
            }

            if (newGM != player.getGameMode())
            {
                player.setGameMode(newGM);
            }
        }
    }

    @EventHandler
    public void handItemDurability(PlayerItemDamageEvent event)
    {
        if (!_gameSettings.loseDurability)
        {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onBlockExplosion(BlockExplodeEvent event)
    {
        if (!_gameSettings.allowExplosions)
        {
            event.setCancelled(true);
        }
        else if (!_gameSettings.enableExplosionMapDamage)
        {
            event.blockList().clear();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onEntityExplosion(EntityExplodeEvent event)
    {
        if (!_gameSettings.allowExplosions)
        {
            event.setCancelled(true);
        }
        else if (!_gameSettings.enableExplosionMapDamage)
        {
            event.blockList().clear();
        }
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent event)
    {
        if (!_gameSettings.enableHungerLoss)
        {
            event.setCancelled(true);
        }
    }

}
