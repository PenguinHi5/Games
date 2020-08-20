package game.common;

import org.bukkit.ChatColor;

public enum ColorTeamNames {

    GREEN("Green", ChatColor.GREEN, (byte)5),
    PURPLE("Magenta", ChatColor.LIGHT_PURPLE, (byte)2),
    RED("Red", ChatColor.RED, (byte)14),
    BLUE("Blue", ChatColor.BLUE, (byte)3),
    YELLOW("Yellow", ChatColor.YELLOW, (byte)4),
    ORANGE("Orange", ChatColor.GOLD, (byte)1),
    AQUA("Aqua", ChatColor.AQUA, (byte)3),
    DARK_AQUA("Cyan", ChatColor.DARK_AQUA, (byte)9),
    GRAY("Gray", ChatColor.GRAY, (byte)8),
    DARK_PURPLE("Dark Purple", ChatColor.DARK_PURPLE, (byte)10),
    DARK_GREEN("Dark Green", ChatColor.DARK_GREEN, (byte)13),
    DARK_RED("Dark Red", ChatColor.DARK_RED, (byte)14),
    WHITE("White", ChatColor.WHITE, (byte)0),
    DARK_BLUE("Dark Blue", ChatColor.DARK_BLUE, (byte)11),
    DARK_GRAY("Dark Gray", ChatColor.DARK_GRAY, (byte)7),
    BLACK("Black", ChatColor.BLACK, (byte)15);

    public String name;
    public ChatColor textColor;
    public byte woolColor;

    ColorTeamNames(String name, ChatColor textColor, byte woolColor)
    {
        this.name = name;
        this.textColor = textColor;
        this.woolColor = woolColor;
    }

}
