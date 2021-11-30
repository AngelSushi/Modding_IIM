package world.nations.stats.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import world.nations.stats.StatsManager;
import world.nations.utils.json.FileUtils;
import world.nations.utils.json.Serialize;

public class StatsData implements StatsManager {
	
	private JavaPlugin plugin;
	
	private List<FactionData> datas = new ArrayList<FactionData>();
	
	public StatsData(JavaPlugin plugin) {
		this.plugin = plugin;
		this.reloadFactionsData();
	}

	@Override
	public List<FactionData> getFactionsData() {
		return this.datas;
	}
	
	@Override
	public FactionData getFaction(String faction) {
		for (FactionData data : datas)
			if (data.getFactionName().equalsIgnoreCase(faction))
				return data;

		return null;
	}
	
	@Override
	public boolean contains(String faction) {
		for (FactionData data : datas)
			if (data.getFactionName().equalsIgnoreCase(faction))
				return true;
		return false;
	}
	
	@Override
	public void reloadFactionsData() {
		String json = FileUtils.loadFile(new File(plugin.getDataFolder(), "factions.json"));
		FactionData[] enums = Serialize.deserializeFactionData(json);
		
		if (enums != null)
			this.datas = new ArrayList<FactionData>(Arrays.asList(enums));
		
		if (this.datas == null)
			this.datas = new ArrayList<FactionData>();
		
		this.datas.forEach(fac -> fac.setPoints(0));
	}

	@Override
	public void saveFactionsData() {
		String json = Serialize.serialize(this.datas);
		FileUtils.saveFile(new File(plugin.getDataFolder(), "factions.json"), json);
	}
	
}
