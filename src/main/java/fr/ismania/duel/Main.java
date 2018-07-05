package fr.ismania.duel;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import fr.simania.duel.arenas.Arena;
import fr.simania.duel.arenas.ArenaListeners;
import fr.simania.duel.arenas.ArenaManager;

public class Main extends JavaPlugin {

	private Map<Player, Player> players = new HashMap<Player, Player>();
	private ArenaManager arenaManager = new ArenaManager();
	private File arenaFile;
	private YamlConfiguration arenaConfig;

	@Override
	public void onEnable() {
		
		getCommand("duel").setExecutor(this);
		getServer().getPluginManager().registerEvents(new ArenaListeners(this), this);

		// création du fichier arenas.yml

		if(!getDataFolder().exists())
		{
			getDataFolder().mkdir();
		}
		arenaFile = new File(getDataFolder() + File.separator + "arena.yml");

		if(!arenaFile.exists())
		{
			try {
				arenaFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);


		//charger les fichiers de config des arènes

		ConfigurationSection arenaSection = arenaConfig.getConfigurationSection("arenas");

		for(String string : arenaSection.getKeys(false))
		{
			String loc1 = arenaSection.getString(string + ".loc1");
			String loc2 = arenaSection.getString(string + ".loc2");
			Arena arena = 	new Arena(parseStringToLoc(loc1), parseStringToLoc(loc2));
			arenaManager.addArena(arena);
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if(label.equalsIgnoreCase("duel") && sender instanceof Player) {
			
			Player player = (Player)sender;

			// /duel
			if(args.length == 0) {
				
				player.sendMessage("-> /duel <joueur>");
				player.sendMessage("-> /duel <accept/deny>");
				return true;

			}
			
			// /duel <player>
			if(args.length >= 1) {
				
				String targetName = args[0];
				Player PlayerLaunch = players.get(player);
				
				if(player == PlayerLaunch)
				{
					player.sendMessage("§7[§aIs§2M§8Duel§7] §cTu ne peux pas te défier toi-même !");
					players.remove(player);
				}
				
				if(args[0].equalsIgnoreCase("accept") && player != PlayerLaunch) {
					
					if(players.containsKey(player)) {
						
						player.sendMessage("§7[§aIs§2M§8Duel§7] §7Ok, le duel se lance !");

						//Player PlayerLaunch = players.get(player);
						PlayerLaunch.sendMessage("§7[§aIs§2M§8Duel§7] §7Ok, le duel se lance !");

						// tp
						arenaManager.joinArena(player, PlayerLaunch);

						players.remove(player);
						
					}
					
				}
				else if(args[0].equalsIgnoreCase("deny")  && player != PlayerLaunch) {
					
					if(players.containsKey(player)) {
						
						//Player PlayerLaunch = players.get(player);
						player.sendMessage("§7[§aIs§2M§8Duel§7] §7Vous avez refus§ le duel !");
						PlayerLaunch.sendMessage("§7[§aIs§2M§8Duel§7] §a" + player.getName()+"§7 a refusé votre duel !");

						players.remove(player);
						
					}
					
				}
				else if(args[0].equals("createarena") && player.hasPermission("permission.createarena")) {
					
					if(args.length < 3)
					{
						player.sendMessage("§7[§aIs§2M§dInfo§7] §7Entrez la commande /duel createarena <loc1> <loc2>");
						return true;
					}

					Location loc1 = parseStringToLoc(args[1]);
					Location loc2 = parseStringToLoc(args[2]);
					Arena arena = new Arena(loc1, loc2);
					String arenaName = "arena-" + new Random().nextInt(9999);

					//save en config de l'arène

					arenaConfig.set("arenas." + arenaName + ".loc1", args[1]);
					arenaConfig.set("arenas." + arenaName + ".loc2", args[2]);
					
					try {
						arenaConfig.save(arenaFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					arenaManager.addArena(arena);
					player.sendMessage("§7[§aIs§2M§8Duel§7] §aVous venez de créer l'arène §c" + arenaName);

				}


				else if(Bukkit.getPlayer(targetName) != null) {
					
					Player target = Bukkit.getPlayer(targetName);

					if(players.containsKey(target)) {
						player.sendMessage("§7[§aIs§2M§8Duel§7] §cCe joueur à déjà une demande de duel en cours");
						return true;
					}

					players.put(target, player);
					player.sendMessage("§7[§aIs§2M§8Duel§7] §7Vous venez de demander un duel contre §a" + targetName);
					target.sendMessage("§7[§aIs§2M§8Duel§7] §7Vous venez de recevoir une demande de duel de §a" + player.getName());

				}
				else
				{
					player.sendMessage("§7[§aIs§2M§8Duel§7] §7Le joueur §c" + targetName + " §7n'existe pas ou n'est pas en ligne.");
				}
				
				return true;
			
			}



		}



		return false;
	}

	public ArenaManager getArenaManager() {
		return arenaManager;
	}

	public Location parseStringToLoc(String string) {

		String[] parsedLoc = string.split(",");
		double x = Double.valueOf(parsedLoc[0]);
		double y = Double.valueOf(parsedLoc[1]);
		double z = Double.valueOf(parsedLoc[2]);

		return new Location(Bukkit.getWorld("spawn"), x,y,z); 
	}
	public String unparsedLocToString(Location loc) {
		return loc.getX() + "," + loc.getY() + "," + loc.getZ();
	}

	public void setArenaManager(ArenaManager arenaManager) {
		this.arenaManager = arenaManager;
	}


}
