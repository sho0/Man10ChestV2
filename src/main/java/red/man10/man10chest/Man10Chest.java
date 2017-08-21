package red.man10.man10chest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.UUID;

public final class Man10Chest extends JavaPlugin implements Listener {


    public boolean playerInMenu = false;
    public ArrayList<UUID> playerInMenuList = new ArrayList<UUID>();
    public Man10ChestAPI api = new Man10ChestAPI();

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.saveDefaultConfig();
        getCommand("mchest").setExecutor(new Man10ChestCommand(this));
        this.getServer().getPluginManager().registerEvents(this,this);
        api.loadAllChestsToMemory();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for(UUID u : playerInMenuList){
            Player p = Bukkit.getPlayer(u);
            p.closeInventory();
            playerInMenuList.remove(u);
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(playerInMenu == false){
            return;
        }
        if(playerInMenuList.contains(e.getWhoClicked().getUniqueId())){
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onCloseMenu(InventoryCloseEvent e){
        if(playerInMenu == false){
            return;
        }
        if(playerInMenuList.contains(e.getPlayer().getUniqueId())){
            playerInMenuList.remove(e.getPlayer().getUniqueId());
        }
    }

    //ラージチェストかどうかを見る
    public boolean isLargeChest(Location l){
        if(l.getBlock().getType() != Material.CHEST){
            return false;
        }
        Location[] ll = {new Location(l.getWorld(),l.getX() -1,l.getY(),l.getZ()),new Location(l.getWorld(),l.getX() + 1,l.getY(),l.getZ()),new Location(l.getWorld(),l.getX(),l.getY(),l.getZ() - 1),new Location(l.getWorld(),l.getX(),l.getY(),l.getZ() + 1)};
        for(int i = 0;i < ll.length; i++){
            if(ll[i].getBlock().getType() == Material.CHEST){
                return true;
            }
        }
        return false;
    }
    public Location getLargeChestLocation(Location l){
        if(l.getBlock().getType() != Material.CHEST){
            return null;
        }
        Location[] ll = {new Location(l.getWorld(),l.getX() -1,l.getY(),l.getZ()),new Location(l.getWorld(),l.getX() + 1,l.getY(),l.getZ()),new Location(l.getWorld(),l.getX(),l.getY(),l.getZ() - 1),new Location(l.getWorld(),l.getX(),l.getY(),l.getZ() + 1)};
        for(int i = 0;i < ll.length; i++){
            if(ll[i].getBlock().getType() == Material.CHEST){
                return ll[i].getBlock().getLocation();
            }
        }
        return null;
    }
}
