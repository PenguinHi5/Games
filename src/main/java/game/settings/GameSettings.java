package game.settings;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.HashSet;

public class GameSettings {

    public boolean allowBlockPlace = false;
    public boolean allowLimitedBlockPlace = false;
    public HashSet<Material> limitedBlockPlaceSet = new HashSet<>();
    public boolean allowBlockBreak = false;
    public boolean allowLimitedBlockBreak = false;
    public HashSet<Material> limitedBlockBreakSet = new HashSet<>();
    public boolean enableFallDamage = true;
    public boolean pvp = false;
    public boolean pve = false;
    public boolean evp = false;
    public GameMode defaultGameMode = GameMode.SURVIVAL;
    public boolean allowCustomGameModes = true;
    public HashMap<Player, GameMode> customGameModes = new HashMap<>();
    public boolean loseDurability = false;
    public boolean allowExplosions = false;
    public boolean enableExplosionPlayerDamage = false;
    public boolean enableExplosionMapDamage = false;
    public boolean enableFireDamage = false;
    public boolean enableLavaDamage = false;
    public boolean enableHungerLoss = false;

    public GameSettings()
    {

    }

}
