package red.man10.man10chest;

import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by sho on 2017/08/21.
 */
public class Man10ChestAPI {
    static HashMap<String, Inventory> chests = new HashMap<>();

    public int createChest(Inventory inv, String name,boolean isLargeChest) {
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Chest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + name + ".yml");
        if (f.exists()) {
            return -1;
        }
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        int count = 0;
        for (int i = 0; i < inv.getContents().length; i++) {
            if (inv.getContents()[i] != null) {
                data.set("item." + i, inv.getContents()[i]);
                count++;
            } else {
                data.set("item." + i, "null");
            }
            data.set("count", count);
            data.set("isLargeChest",isLargeChest);
        }
        try {
            data.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Inventory getChest(String name){
        if(chests.containsKey(name)){
            return chests.get(name);
        }
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Chest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + name + ".yml");
        FileConfiguration data = YamlConfiguration.loadConfiguration(f);
        if(!f.exists()){
            return null;
        }
        boolean isLargeChest = data.getBoolean("isLargeChest");
        Inventory inv = null;
        if(isLargeChest){
            inv = Bukkit.createInventory(null,54,name);
        }else{
            inv = Bukkit.createInventory(null,27,name);
        }
        Set<String> keys = data.getConfigurationSection("item").getKeys(false);
        for(int i = 0;i < keys.size();i++){
            String string = data.getString("item." + keys.toArray()[i]);
            if(!string.equals("null")){
                ItemStack item = data.getItemStack("item." + keys.toArray()[i]);
                inv.setItem(i,item);
            }
        }
        return inv;
    }

    public int deleteChest(String name){
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Chest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + name + ".yml");
        if(!f.exists()){
            return -1;
        }
        chests.remove(name);
        f.delete();
        return 0;
    }

    public boolean doesChestExists(String name){
        if(chests.containsKey(name)){
            return true;
        }
        File dataa = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Chest").getDataFolder(), File.separator + "Chests");
        File f = new File(dataa, File.separator + name + ".yml");
        if(!f.exists()){
            return false;
        }
        return true;
    }

    public boolean isLargeChest(String name){
        if(!doesChestExists(name)){
            return false;
        }
        if(chests.containsKey(name)){
            if(chests.get(name).getSize() == 54){
                return false;
            }
            return true;
        }
        int i  = loadSingleChestToMemory(name);
        if(i == -1){
            return false;
        }
        if(chests.containsKey(name)) {
            if (chests.get(name).getSize() == 54) {
                return false;
            }
        }
        return true;
    }

    public int loadSingleChestToMemory(String name){
        if(!doesChestExists(name)){
            return -1;
        }
        chests.put(name,getChest(name));
        return 0;
    }

    public int loadAllChestsToMemory(){
        chests.clear();
        List<String> names = getAllChestNames();
        for(int i = 0;i < names.size();i++){
            chests.put(names.get(i),getChest(names.get(i)));
        }
        return 0;
    }

    public List<String> getAllChestNames(){
        List<String> name = new ArrayList<>();
        File folder = new File(Bukkit.getServer().getPluginManager().getPlugin("Man10Chest").getDataFolder(), File.separator + "Chests");
        File[] listOfFiles = folder.listFiles();
        for(int i = 0;i < listOfFiles.length;i++){
            name.add(deleteExtention(listOfFiles[i].getName()));
        }
        return name;
    }

    private String deleteExtention(String fileName){
        String fname = fileName;
        int pos = fname.lastIndexOf(".");
        if (pos > 0) {
            fname = fname.substring(0, pos);
        }
        return fname;
    }
}
