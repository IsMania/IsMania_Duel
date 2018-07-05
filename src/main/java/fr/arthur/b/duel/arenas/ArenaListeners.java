package fr.arthur.b.duel.arenas;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.arthur.b.duel.Main;
import fr.arthur.b.duel.arenas.*;
public class ArenaListeners implements Listener {

	private Main main;
	
	public ArenaListeners(Main main) { this.main = main; }

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player victime = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			
			Arena victimeArena = main.getArenaManager().getArenaByPlayer(victime);
			/*if(victimeArena == null || !victimeArena.getPlayers().contains(damager))
			{
				event.setCancelled(true);
			}*/
		}
	}
	@EventHandler
	public void onKill(PlayerDeathEvent event)
	{
		if(event.getEntity().getKiller() instanceof Player)
		{
			Player victime = (Player) event.getEntity();
			Player killer = (Player) victime.getKiller();
			Arena arena = main.getArenaManager().getArenaByPlayer(killer);
			if(arena != null) {
			arena.eliminate(victime);
			}
			Location spawn = new Location(Bukkit.getWorld("ASkyBlock"), -66.5, 88, -7.5, -180, 0);
			Player p = killer.getPlayer();
			Player p2 = victime.getPlayer();
			p.teleport(spawn);
			p2.teleport(spawn);
			p.sendMessage("§7[§aIs§2M§8Duel§7] §7Vous avez été téléporté au spawn!");
			p2.sendMessage("§7[§aIs§2M§8Duel§7] §7Vous avez été téléporté au spawn!");
		}
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player leaver = event.getPlayer();
		Arena arena = main.getArenaManager().getArenaByPlayer(leaver);
		
		if(arena != null) {
			arena.eliminate(leaver);
			}
	}
	
}
