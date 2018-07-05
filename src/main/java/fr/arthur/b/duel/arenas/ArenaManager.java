package fr.arthur.b.duel.arenas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

public class ArenaManager {
	
	private List<Arena> arenas = new ArrayList<>();
	
	public void addArena(Arena arena) {
		this.arenas.add(arena);
		
	}
	public void joinArena(Player firstPlayer, Player secondPlayer) {
		
		Arena nextArena = getNextArena();
		
		if(nextArena != null)
		{
			nextArena.getPlayers().add(firstPlayer);
			nextArena.getPlayers().add(secondPlayer);
			firstPlayer.teleport(nextArena.getFirstLoc());
			secondPlayer.teleport(nextArena.getSecondLoc());
			getNextArena().setStarted();
		}
		else
		{
			firstPlayer.sendMessage("�7[�aIs�2M�8Duel�7] �cL'ar�ne est occup�e, attendez votre tour !");
			secondPlayer.sendMessage("�7[�aIs�2M�8Duel�7] �cL'ar�ne est occup�e, attendez votre tour !");
		}
		
	}
	
	public Arena getArenaByPlayer(Player player) {
		
		for(Arena arena : arenas) {
			if(arena.getPlayers().contains(player))
			{
				return arena;
			}
		}
		return null;
	}
	private Arena getNextArena() {
		
		for(Arena arena : arenas)
		{
			if(!arena.isStarted())
			{
				return arena;
			}
		}
		
		return null;
	}
	
	public List<Arena> getArenas() {
		return arenas;
	}
	
	
}
