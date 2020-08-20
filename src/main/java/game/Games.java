package game;

import core.minecraft.chat.ChatManager;
import core.minecraft.client.ClientManager;
import core.minecraft.combat.CombatManager;
import core.minecraft.command.CommandManager;
import core.minecraft.cooldown.Cooldown;
import core.minecraft.damage.DamageManager;
import core.minecraft.inventory.InventoryManager;
import core.minecraft.region.RegionManager;
import core.minecraft.scoreboard.ScoreManager;
import core.minecraft.server.ServerManager;
import core.minecraft.timer.Timer;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.world.WorldManager;
import core.redis.Location;
import core.redis.connection.RedisManager;
import core.redis.data.BungeeProxy;
import core.redis.data.DedicatedServer;
import core.redis.data.MinecraftServer;
import core.redis.data.ServerType;
import core.redis.message.RedisMessageManager;
import core.redis.repository.ServerRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Games extends JavaPlugin {

    public void onEnable()
    {
        CommandManager commandManager = new CommandManager(this);
        Cooldown.initializeCooldown(this);
        String name = this.generateServers();
        ServerManager serverConfiguration = new ServerManager(this, commandManager);
        ClientManager clientManager = new ClientManager(this, serverConfiguration, commandManager);
        InventoryManager inventoryManager = new InventoryManager(this, clientManager, commandManager);
        TransactionManager transactionManager = new TransactionManager(this, clientManager, commandManager);
        WorldManager worldManager = new WorldManager(this, commandManager);
        RegionManager regionManager = new RegionManager(this, commandManager);
        CombatManager combatManager = new CombatManager(this, commandManager);
        DamageManager damageManager = new DamageManager(combatManager, this, commandManager);
        new Timer(this, commandManager);
        new ChatManager(this, clientManager, commandManager);
        ScoreManager scoreManager = new ScoreManager(this, commandManager);

        GameManager gameManager = new GameManager(this, commandManager, scoreManager, transactionManager, worldManager);
    }

    // TODO -=-=-=-=-=-=-=-=-=-=-=-= EVERYTHIN BELOW IS TEMPORARY FOR TESTING =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    private String generateServers() {
        String ip = "";

        try {
            URL list = new URL("http://checkip.amazonaws.com");
            BufferedReader name = new BufferedReader(new InputStreamReader(list.openStream()));
            ip = name.readLine();
        } catch (Exception var10) {
            var10.printStackTrace();
        }

        List list1 = this.readTempConfig();
        String name1 = (String)list1.get(0);
        RedisMessageManager.getInstance().initializeServer(name1);
        HashMap data = new HashMap();
        data.put("name", "test");
        data.put("maxPlayerLimit", Integer.valueOf(16));
        data.put("minPlayerLimit", Integer.valueOf(0));
        data.put("ram", Integer.valueOf(1024));
        ServerType type = new ServerType(data, new ArrayList());
        MinecraftServer mcServer = new MinecraftServer(ip, 2020, name1, "test", 0, 16, "test server", 1024L, 1024L, "1.8");
        type.addMinecraftServer(mcServer);
        BungeeProxy bungee = new BungeeProxy(ip, 25565, 1024, 1000, 0);
        DedicatedServer ded = new DedicatedServer(ip, "localhost", 10000, 10000, 10000, 10000, Location.US);
        ServerRepository repo = RedisManager.getInstance().getServerRepository();
        repo.addServerType(type);
        repo.addBungeeProxy(bungee);
        repo.addDedicatedServer(ded);
        repo.addMinecraftServer(mcServer);
        //RedisMessageManager.getInstance().addCommandType(TestRedisCommand.class, new TestRedisCommandHandler());
        return name1;
    }

    private List<String> readTempConfig() {
        File file = new File("tempdata.dat");
        Scanner scanner = null;
        ArrayList lines = new ArrayList();

        try {
            scanner = new Scanner(file);

            while(scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException var8) {
            System.out.println("temp cannot be found at " + file.getAbsolutePath());
            var8.printStackTrace();
        } finally {
            if(scanner != null) {
                scanner.close();
            }

        }

        return lines;
    }
}
