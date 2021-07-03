package game.common;

import org.bukkit.plugin.java.JavaPlugin;

public class ElapsedTimeTracker
{

    private long _lapStartTime;
    private long _totalTime = 0L;
    private boolean _paused = true;

    public ElapsedTimeTracker(JavaPlugin plugin)
    {

    }

    public void start()
    {
        _lapStartTime = System.currentTimeMillis();
        _paused = false;
    }

    public void stop()
    {
        if (!_paused)
            _totalTime += System.currentTimeMillis() - _lapStartTime;

        _paused = true;
    }

    public void reset()
    {
        stop();
        _totalTime = 0;
    }

    public long getTime()
    {
        return _totalTime + System.currentTimeMillis() - _lapStartTime;
    }

}
