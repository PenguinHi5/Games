package game.common;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum ColorTeamNames {

    BLUE("Blue", ChatColor.BLUE, (byte)3, Color.BLUE),
    RED("Red", ChatColor.RED, (byte)14, Color.RED),
    GREEN("Green", ChatColor.GREEN, (byte)5, Color.LIME),
    PURPLE("Magenta", ChatColor.LIGHT_PURPLE, (byte)2, Color.FUCHSIA),
    YELLOW("Yellow", ChatColor.YELLOW, (byte)4, Color.YELLOW),
    ORANGE("Orange", ChatColor.GOLD, (byte)1, Color.ORANGE),
    AQUA("Aqua", ChatColor.AQUA, (byte)3, Color.TEAL),
    DARK_AQUA("Cyan", ChatColor.DARK_AQUA, (byte)9, Color.AQUA),
    GRAY("Gray", ChatColor.GRAY, (byte)8, Color.GRAY),
    DARK_PURPLE("Dark Purple", ChatColor.DARK_PURPLE, (byte)10, Color.PURPLE),
    DARK_GREEN("Dark Green", ChatColor.DARK_GREEN, (byte)13, Color.GREEN),
    DARK_RED("Dark Red", ChatColor.DARK_RED, (byte)14, Color.RED),
    WHITE("White", ChatColor.WHITE, (byte)0, Color.WHITE),
    DARK_BLUE("Dark Blue", ChatColor.DARK_BLUE, (byte)11, Color.BLUE),
    DARK_GRAY("Dark Gray", ChatColor.DARK_GRAY, (byte)7, Color.GRAY),
    BLACK("Black", ChatColor.BLACK, (byte)15, Color.BLACK);

    public String name;
    public ChatColor textColor;
    public byte woolColor;
    public Color color;

    ColorTeamNames(String name, ChatColor textColor, byte woolColor, Color color)
    {
        this.name = name;
        this.textColor = textColor;
        this.woolColor = woolColor;
        this.color = color;
    }

}
