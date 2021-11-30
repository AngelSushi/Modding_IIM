package world.nations.stats.data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FactionData {
	private String factionName;

	private int points;
	private int wins;
	private int loses;
	
	private int deaths;
	private int kills;
	
	private boolean urss;
	private boolean ally;
	private boolean axe;
	
	private List<String> bankAcces;
	
	public FactionData(String factionName) {
		this.factionName = factionName;
		
		this.points = 0;
		this.wins = 0;
		this.loses = 0;
		this.kills = 0;
		this.deaths = 0;
		
		this.urss = false;
		this.ally = false;
		this.axe = false;
		
		this.bankAcces = new ArrayList<String>();
	}
	
	public void addPoints(int amount) {
		this.points += amount;
	}
	
	public void addKill() {
		this.kills += 1;
	}
	
	public void addDeath() {
		this.deaths += 1;
	}
	
	public void addWin() {
		this.wins += 1;
	}
	
	public void addLose() {
		this.loses += 1;
	}
	
	public int getRatio() {
		return (this.wins - this.loses);
	}
	
	public List<String> getBankAccess() {
		return this.bankAcces;
	}
	
	public String getKDR() {
		if (this.deaths != 0 && this.kills >= 0) {
			double result = deaths / kills;
			return new DecimalFormat("##.##").format(result);
		} else {
			return "N/A";
		}
	}
}
