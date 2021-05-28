package game.gamelist.towerdefense.tower;

import org.bukkit.entity.Entity;

public class Tower
{

    private int _startingHealth;
    private int _health;
    private Entity _crystal;

    public Tower(int health)
    {
        _startingHealth = health;
        _health = health;
    }

    /**
     * Applies damage to the tower.
     */
    public void damageTower(int damage)
    {
        _health -= damage;
    }

    /**
     * Heals the tower.
     */
    public void healTower(int health)
    {
        _health += health;
    }

    /**
     * Gets the current health of the tower.
     */
    public int getHealth()
    {
        return _health;
    }

    /**
     * Gets the health of the tower at the start of the game.
     */
    public int getStartingHealth()
    {
        return _startingHealth;
    }

    public void setTowerCrystal(Entity entity)
    {
        _crystal = entity;
    }

    public Entity getCrystal()
    {
        return _crystal;
    }

}
