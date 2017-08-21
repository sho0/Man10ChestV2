package red.man10.man10chest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sho on 2017/08/21.
 */
public class Man10ChestCommand implements CommandExecutor {

    String prefix = "§7§l[§6§lmChest§7§l] §8§l";

    private final Man10Chest plugin;

    public Man10ChestCommand(Man10Chest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("mchest")) {
            Player p = (Player) sender;
            if(!p.hasPermission("mchest.command")){
                p.sendMessage(noPermissionMessage("mchest.command"));
                return true;
            }
            if(args.length == 0 ){
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
            }
            if(args.length == 1) {
                if (args[0].equalsIgnoreCase("list")) {//ファイルリスト
                    if(!p.hasPermission("mchest.list")){
                        p.sendMessage(noPermissionMessage("mchest.list"));
                        return true;
                    }
                    List<String> listOfFiles = plugin.api.getAllChestNames();
                    p.sendMessage("§8§l===========§7§l[§6§lmChest Files§7§l]§8§l===========");
                    for (int i = 0; i < listOfFiles.size(); i++) {
                        p.sendMessage("§7§l" + listOfFiles.get(i));
                    }
                    return true;
                }
                if(args[0].equalsIgnoreCase("help")){
                    if(!p.hasPermission("mchest.help")){
                        p.sendMessage(noPermissionMessage("mchest.help"));
                        return true;
                    }
                    help(p);
                    return true;
                }
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
                return true;
            }



            if (args.length == 2) {
                if(args[0].equalsIgnoreCase("preview")){
                    if(!p.hasPermission("mchest.preview")){
                        p.sendMessage(noPermissionMessage("mchest.preview"));
                        return true;
                    }
                    Inventory inv = plugin.api.getChest(args[1]);
                    if(inv == null){
                        p.sendMessage(prefix + "チェストが存在しません");
                        return false;
                    }
                    plugin.playerInMenu = true;
                    plugin.playerInMenuList.add(p.getUniqueId());
                    p.openInventory(inv);
                    return true;
                }
                if (args[0].equalsIgnoreCase("create")) {
                    if(!p.hasPermission("mchest.create")){
                        p.sendMessage(noPermissionMessage("mchest.create"));
                        return true;
                    }
                    Block b = p.getTargetBlock(null,100);
                    if(b.getType() != Material.CHEST){
                        p.sendMessage(prefix + "ブロックはチェストのみです");
                        return true;
                    }
                    Chest c = (Chest) b.getState();
                    ItemStack[] contents = c.getInventory().getContents();
                    Inventory inv = Bukkit.createInventory(null,54,"chest");
                    inv.setContents(contents);
                    boolean bool = plugin.isLargeChest(b.getLocation());
                    int i = plugin.api.createChest(inv,args[1],bool);
                    if(i == -1){
                        p.sendMessage(prefix + "チェストが存在するため上書きします");
                        plugin.api.deleteChest(args[1]);
                        plugin.api.createChest(inv,args[1],bool);
                    }
                    p.sendMessage(prefix + "チェストを作成しました『" + args[1] + "』");
                    return true;
                }
                if(args[0].equalsIgnoreCase("load")){
                    if(!p.hasPermission("mchest.load")){
                        p.sendMessage(noPermissionMessage("mchest.load"));
                        return true;
                    }
                    Block b = p.getTargetBlock(null,100);
                    boolean isLargeChest = plugin.api.isLargeChest(args[1]);
                    if(b.getType() != Material.CHEST){
                        p.sendMessage(prefix + "ロード先はチェストで無ければいけません");
                        return true;
                    }
                    if(isLargeChest){
                        if(!plugin.isLargeChest(b.getLocation())){
                            p.sendMessage(prefix + "ラージチェストのロード先はラージチェストじゃなければなりません");
                            return false;
                        }
                        InventoryHolder inv = (InventoryHolder) b.getState();
                        inv.getInventory().setContents(plugin.api.getChest(args[1]).getContents());
                    }else{
                        InventoryHolder inv = (InventoryHolder) b.getState();
                        inv.getInventory().setContents(plugin.api.getChest(args[1]).getContents());
                    }
                    p.sendMessage(prefix + "チェストをロードしました");
                    return true;

                }
                if(args[0].equalsIgnoreCase("delete")){
                    if(!p.hasPermission("mchest.delete")){
                        p.sendMessage(noPermissionMessage("mchest.delete"));
                        return true;
                    }
                    int i = plugin.api.deleteChest(args[1]);
                    if(i == -1){
                        p.sendMessage(prefix + "チェストが存在しません");
                        return false;
                    }
                    p.sendMessage(prefix + args[1] + "を消去しました");
                    return false;
                }
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
                return true;
            }
            if(args.length >= 3){
                p.sendMessage(prefix + "コマンドの使い方が間違ってます /mchest help");
                return true;
            }
        }
        return false;
    }
    public String noPermissionMessage(String s){
        String message = prefix + "§c§lあなたは" + s + "の権限を持っていません";
        return message;
    }
    public void help(Player p){
        p.sendMessage("§8§l===============§7§l[§6§lmChest§7§l]§8§l===============");
        p.sendMessage("§6§l/mchest create <name> §e§lチェストを保存");
        p.sendMessage("§6§l/mchest load <name> §e§lチェストをロード");
        p.sendMessage("§6§l/mchest delete <name> §e§lチェストを消去");
        p.sendMessage("§6§l/mchest preview <name> §e§lチェストを観覧");
        p.sendMessage("§6§l/mchest list §e§l保存されたチェストを見る");
        p.sendMessage("§8§l=====================================");
    }

}
