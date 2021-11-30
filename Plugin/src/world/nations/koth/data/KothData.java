package world.nations.koth.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import com.sk89q.worldedit.bukkit.selections.Selection;

import world.nations.utils.Cuboid;

public class KothData {

	private String name;
	private List<String> commands;
	private Location location1;
	private Location location2;
	
	private Location zoneTP;

	public KothData(String name, Selection selection) {
		this.name = name;
		this.location1 = selection.getMaximumPoint();
		this.location2 = selection.getMinimumPoint();
		this.zoneTP = null;
		this.commands = new ArrayList<String>();
	}

	public KothData(String name, Location loc1, Location loc2) {
		this.name = name;
		this.location1 = loc1;
		this.location2 = loc2;
		this.zoneTP = null;
		this.commands = null;
	}

	public Cuboid asCuboid() {
		return new Cuboid(this.location1, this.location2);
	}

	public Location getLocation1() {
		return this.location1;
	}

	public Location getLocation2() {
		return this.location2;
	}
	
	public Location getZoneTP() {
		return this.zoneTP;
	}

	public void setLocation1(Location location1) {
		this.location1 = location1;
	}

	public void setLocation2(Location location2) {
		this.location2 = location2;
	}
	
	public void setZoneTP(Location zoneTP) {
		this.zoneTP = zoneTP;
	}

	public void addCommand(String command) {
		this.commands.add(command);
	}
	
	public void setCommands(List<String> list) {
		this.commands = list;
	}
	
	public List<String> getCommands() {
		return this.commands;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
    public String getName() {
        return this.name;
    }
}

