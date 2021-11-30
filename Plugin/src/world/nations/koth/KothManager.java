package world.nations.koth;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Lists;

import world.nations.Core;
import world.nations.koth.data.KothData;
import world.nations.koth.runnable.KothTracker;
import world.nations.koth.runnable.RunningKoth;
import world.nations.utils.ConfigCreator;
import world.nations.utils.LocationSerializer;
import world.nations.utils.Utils;

public class KothManager {
	
	private Core plugin;
	private ConfigCreator config;
	
	private List<KothData> koths;
	
	private KothTracker tracker = null;
	//private ScoreboardZone scoreboard = null;
	
	public boolean isEnabled = false;
	
	public KothManager(Core plugin) {
		this.plugin = plugin;
		this.config = new ConfigCreator(plugin, "zones");
		this.koths = Lists.newArrayList();
		this.reloadData();
	}

	public KothData getKoth(String name) {
		for (KothData koth : this.koths) {
			if (!koth.getName().equalsIgnoreCase(name))
				continue;
			return koth;
		}
		return null;
	}
	
	public void startCountdown(KothData koth) {
		this.startKoth(koth);
	}
	
    public void startKoth(KothData koth) {
        RunningKoth runner = new RunningKoth(koth);
        
		this.isEnabled = true;
        
		this.tracker = new KothTracker(plugin, runner);
		//this.scoreboard = new ScoreboardZone(plugin, runner);
		

        Bukkit.broadcastMessage(Utils.color("&6[Capture] &eLa zone &9&l" + koth.getName() + " &evient d'\u00eatre lanc\u00e9e."));
    }

    public void stopKoth(KothData koth) {
        if (this.tracker != null) {
            this.tracker.cancel();
        }

        this.isEnabled = false;
    }

    public void saveData() {
        ConfigurationSection section = this.config.createSection("zone");
        
        for (KothData koth : this.koths) {
            section.set(String.valueOf(koth.getName()) + ".loc1", LocationSerializer.stringifyLocation(koth.getLocation1()));
            section.set(String.valueOf(koth.getName()) + ".loc2", LocationSerializer.stringifyLocation(koth.getLocation2()));
            
            if (koth.getZoneTP() != null)
            	section.set(String.valueOf(koth.getName()) + ".tp", LocationSerializer.stringifyLocation(koth.getZoneTP()));
            if (!koth.getCommands().isEmpty())
            	section.set(String.valueOf(koth.getName()) + ".commands", koth.getCommands());
        }
        
        this.config.save();
    }

    public void reloadData() {
        ConfigurationSection section = this.config.getConfigurationSection("zone");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                Location loc1 = LocationSerializer.destringifyLocation(section.getString(String.valueOf(key) + ".loc1"));
                Location loc2 = LocationSerializer.destringifyLocation(section.getString(String.valueOf(key) + ".loc2"));
                Location tp = LocationSerializer.destringifyLocation(section.getString(String.valueOf(key) + ".tp"));
                List<String> list = section.getStringList(String.valueOf(key) + ".commands");
                KothData data = new KothData(key, loc1, loc2);
                data.setZoneTP(tp);
                data.setCommands(list);
                this.koths.add(data);
            }
        }
    }

    public List<KothData> getKoths() {
        return this.koths;
    }
}
