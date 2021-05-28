package game.team;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.damage.events.CustomDamageEvent;
import game.GameManager;
import game.SoloGame;
import game.common.ColorTeamNames;
import game.common.RandomTeamNames;
import game.gamestate.GameState;
import game.gamestate.event.GameStateChangeEvent;
import game.lobby.event.CancelCountdownEvent;
import game.lobby.event.StartCountdownEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;

public class TeamManager implements Listener {

    private boolean _forceTeamBalancing = true;
    private boolean _assignTeamsOnJoin = false;
    private boolean _useRandomTeamNames = false;
    private int _teamCount;
    private HashMap<ColorTeamNames, HashSet<Player>> _teams = new HashMap<>();
    private HashSet<Player> _players = new HashSet<>();
    private HashSet<Player> _spectators = new HashSet<>();
    private HashSet<Player> _dead = new HashSet<>();
    private GameState _gameState;
    private JavaPlugin _plugin;
    private GameManager _gameManager;

    public TeamManager(JavaPlugin plugin, GameManager gameManager)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        _gameManager = gameManager;
        _plugin = plugin;
    }

    /**
     * Assigns the player to the specified team. If the player is already on the team true is returned.
     * @param player the player being added to a team
     * @param team the team the player is being added to
     * @return true if the player was added to the team, otherwise false
     */
    public boolean assignPlayerToTeam(Player player, ColorTeamNames team)
    {
        if (_teams.containsKey(team))
        {
            // Checks if a team is too big for a player to join
            boolean canJoin = _teams.get(team).contains(player) ||
                    _teams.get(team).size() < (_players.size() / _teamCount) ||
                    (_teams.get(team).size() <= (_players.size() / _teamCount) && (_players.size() % _teamCount) > 0);
            if (canJoin)
            {
                // Removes the player from their existing team
                if (isPlayerInTeam(player))
                {
                    // Removes the player from all lists
                    for (ColorTeamNames t : _teams.keySet())
                    {
                        _teams.get(t).remove(player);
                    }
                }

                _teams.get(team).add(player);
                player.setDisplayName(team.textColor + player.getName());
                return true;
            }
        }
        return false;
    }

    public void assignPlayerToTeam(Player player)
    {
        if (isPlayerInTeam(player) || _teamCount < 1)
            return;

        ColorTeamNames smallestTeam = ColorTeamNames.values()[0];
        int smallestTeamSize = _teams.get(smallestTeam).size();
        for (ColorTeamNames team : _teams.keySet())
        {
            if (_teams.get(team).size() < smallestTeamSize)
            {
                smallestTeam = team;
                smallestTeamSize = _teams.get(team).size();
            }
        }
        _teams.get(smallestTeam).add(player);
        player.setDisplayName(smallestTeam.textColor + player.getName());
    }

    public boolean isPlayerInTeam(Player player)
    {
        for (ColorTeamNames team : _teams.keySet())
        {
            if (_teams.get(team).contains(player))
            {
                return true;
            }
        }
        return false;
    }

    public void assignAllPlayersToTeams()
    {
        ColorTeamNames[] names = ColorTeamNames.values();
        for (Player player : _players)
        {
            if (!isPlayerInTeam(player))
            {
                assignPlayerToTeam(player);
            }
        }
    }

    public HashMap<ColorTeamNames, HashSet<Player>> getAllPlayerTeams()
    {
        return _teams;
    }

    /**
     * Returns the team that the player belongs to. If the player doesn't belong to a team null is returned.
     *
     * @param player the player that you are searching for the team of
     * @return the team the player belongs to, null if they aren't currently in a team
     */
    public ColorTeamNames getPlayersTeam(Player player)
    {
        for (ColorTeamNames team : _teams.keySet())
        {
            if (_teams.get(team).contains(player))
            {
                return team;
            }
        }
        System.out.println("Failed to find a team that the player belongs to");
        return null;
    }

    public void setTeamCount(int count)
    {
        _teamCount = count;

        if (count <= 1)
        {
            _teams.clear();
            return;
        }

        _teams.clear();
        ColorTeamNames[] names = ColorTeamNames.values();
        for (int i = 0; i < count; i++)
        {
            _teams.put(names[i], new HashSet<>());
        }
        if (_assignTeamsOnJoin)
        {
            assignAllPlayersToTeams();
        }
    }

    public void setDead(Player player, boolean isDead)
    {
        if (isDead)
        {
            _dead.add(player);
            //_players.remove(player);
            player.setAllowFlight(true);
            player.setFlying(true);
            //player.setHealth(player.getMaxHealth());
            //player.setSaturation(20);
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.hidePlayer(player);
            }
        }
        else
        {
            _dead.remove(player);
            //_players.add(player);
            player.setAllowFlight(false);
            player.setFlying(false);
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.showPlayer(player);
            }
        }
    }

    public boolean isDead(Player player)
    {
        return _dead.contains(player);
    }

    public void clearDead()
    {
        for (Player player : _dead)
        {
            setDead(player, false);
        }
        _dead.clear();
    }

    public void clearSpectators()
    {
        for (Player player : _dead)
        {
            setSpectate(player, false);
        }
        _dead.clear();
    }

    public void clearTeams()
    {
        _teams.clear();
    }

    public void setSpectate(Player player, boolean spectating)
    {
        if (spectating)
        {
            player.setAllowFlight(true);
            player.setFlying(true);
            _spectators.add(player);
            _players.remove(player);
            _dead.remove(player);
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.hidePlayer(player);
            }
        }
        else
        {
            player.setAllowFlight(false);
            player.setFlying(false);
            _spectators.remove(player);
            _players.add(player);
            for (Player p : Bukkit.getOnlinePlayers())
            {
                p.showPlayer(player);
            }
        }
    }

    public boolean isSpectating(Player player)
    {
        return _spectators.contains(player);
    }

    /**
     * Sets if team balancing should be forced.
     * @param teamBalancing true if team balancing should be enforced, otherwise false
     */
    public void forceTeamBalancing(boolean teamBalancing)
    {
        _forceTeamBalancing = teamBalancing;
    }

    /**
     * If team balancing is being forced.
     * @return true if team balancing is enforced, otherwise false
     */
    public boolean isForceTeamBalancing()
    {
        return _forceTeamBalancing;
    }

    public void useRandomTeamNames(boolean useRandomTeamNames)
    {
        _useRandomTeamNames = useRandomTeamNames;
    }

    public boolean isUsingRandomTeamNames()
    {
        return _useRandomTeamNames;
    }

    /**
     * Returns the current player count excluding both dead players and spectators.
     */
    public int getLivingPlayerCount()
    {
        return _players.size() - _dead.size();
    }

    /**
     * Returns the current player count including dead players, but excluding spectators.
     */
    public int getPlayerCount()
    {
        return _players.size();
    }

    /**
     * Returns a set of all players excluding both dead players and spectators.
     */
    public HashSet<Player> getLivingPlayers()
    {
        HashSet<Player> livingPlayers = (HashSet<Player>) _players.clone();
        livingPlayers.removeAll(_dead);
        return livingPlayers;
    }

    /**
     * Returns a set of all players including dead players, but excluding spectators.
     */
    public HashSet<Player> getPlayers()
    {
        return _players;
    }

    public HashSet<Player> getDeadPlayers()
    {
        return _dead;
    }

    public HashSet<Player> getSpectators()
    {
        return _spectators;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        if (_gameManager.getGameState() == GameState.LOBBY)
        {
            _players.add(event.getPlayer());
            if (_assignTeamsOnJoin)
            {
                assignPlayerToTeam(event.getPlayer());
            }
        }
        else
        {
            setSpectate(event.getPlayer(), true);
            // Hides the dead and spectators
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (_dead.contains(player) || _spectators.contains(player))
                {
                    event.getPlayer().hidePlayer(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        // Removes the player from all lists
        for (ColorTeamNames team : _teams.keySet())
        {
            _teams.get(team).remove(event.getPlayer());
        }
        _players.remove(event.getPlayer());
        _spectators.remove(event.getPlayer());
        _dead.remove(event.getPlayer());
    }

    @EventHandler
    public void onGameStateChange(GameStateChangeEvent event)
    {
        if (event.getNewGameState() == GameState.LOBBY)
        {
            _assignTeamsOnJoin = false;
            clearDead();
            clearSpectators();
            clearTeams();
            _players.addAll(Bukkit.getOnlinePlayers());
            for (Player player : Bukkit.getOnlinePlayers())
            {
                player.setDisplayName(player.getName());
            }

            if (_gameManager.getNextGame() instanceof SoloGame)
            {
                _teamCount = 1;
            }
            /*else if (_gameManager.getNextGame() instanceof TeamGame)
            {
                _teamCount = ((TeamGame) _gameManager.getNextGame()).getTeamCount();
            }*/
        }
        else if (event.getNewGameState() == GameState.POST_GAME)
        {
            setTeamCount(0);
        }
    }

    @EventHandler
    public void onCountdownStartEvent(StartCountdownEvent event)
    {
        if (_teamCount > 1)
        {
            _assignTeamsOnJoin = true;
            assignAllPlayersToTeams();
        }
    }

    @EventHandler
    public void onCountdownCancelEvent(CancelCountdownEvent event)
    {
        _assignTeamsOnJoin = false;
        for (ColorTeamNames team : _teams.keySet())
        {
            _teams.get(team).clear();
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void cancelDamage(CustomDamageEvent event)
    {
        if (event.getPlayerDamagee() != null)
        {
            if (_dead.contains(event.getPlayerDamagee()) || _spectators.contains(event.getPlayerDamagee()))
            {
                event.setCancelled("Player is spectating");
            }
        }
    }

}
