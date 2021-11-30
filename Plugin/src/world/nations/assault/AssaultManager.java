package world.nations.assault;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.massivecraft.factions.entity.Faction;

import world.nations.Core;
import world.nations.assault.data.AssaultData;
import world.nations.assault.listener.AssaultListener;
import world.nations.utils.timings.CountdownTimer;

public class AssaultManager {
	private List<AssaultData> list = new ArrayList<AssaultData>();

	private Core plugin;

	public AssaultManager(Core plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(new AssaultListener(this.plugin), plugin);
	}
	
	public AssaultData getFaction(Faction faction) {
		for (AssaultData data : list) {
			if (data.getFactionName() == faction.getName() || data.getFactionTarget() == faction.getName())
				return data;
		}
		
		return null;
	}

	public void addFaction(Faction faction, Faction factionTarget, CountdownTimer timer) {
		list.add(new AssaultData(faction, factionTarget, timer));
	}

	public List<AssaultData> getAssaultList() {
		return this.list;
	}
}
