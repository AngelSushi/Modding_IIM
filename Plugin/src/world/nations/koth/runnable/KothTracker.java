package world.nations.koth.runnable;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;

import world.nations.Core;
import world.nations.koth.data.KothData;
import world.nations.stats.data.FactionData;
import world.nations.utils.Cuboid;

public class KothTracker extends BukkitRunnable {
	private RunningKoth runningKoth;
	private Core plugin;
	
	private List<FactionData> list = Lists.newArrayList();

    public KothTracker(Core plugin, RunningKoth runningKoth) {
    	this.plugin = plugin;
        this.runningKoth = runningKoth;
        this.runTaskTimerAsynchronously(plugin, 20, 20);
    }

    @Deprecated
    public void run() {
        KothData koth = this.runningKoth.getKoth();
        Cuboid cubo = koth.asCuboid();
        this.runningKoth.getPlayersInside().clear();
        
        for (Player player : Bukkit.getOnlinePlayers()) {
			if (UPlayer.get(player).hasFaction() && cubo.contains(player.getLocation())) {
				this.runningKoth.getPlayersInside().add(player);
            }
        }
        
        if (!(this.runningKoth.getCapper() == null || cubo.contains(this.runningKoth.getCapper().getLocation()) && this.runningKoth.getCapper().isOnline())) {
            UPlayer fplayer = UPlayer.get(this.runningKoth.getCapper());
    		Core.getPlugin().getStatsManager().getFaction(fplayer.getFactionName()).setPoints(0);
            this.runningKoth.setCapper(null);
            //koth.setRemaining(koth.getDefaultTime(), true, true);
            //if (!list.isEmpty()) list.forEach(faction -> faction.setPoints(faction.getPoints()-1));
            Bukkit.broadcastMessage("§6[Capture] §cLe contr\u00f4le de la zone §6§l" + koth.getName() + " §cvient d'\u00eatre perdu.");
        }
        
        if (this.runningKoth.getCapper() == null && !this.runningKoth.getPlayersInside().isEmpty()) {
            this.runningKoth.setCapper(this.runningKoth.getPlayersInside().get(0));
            //koth.setPaused(false);
            for (Player fp : UPlayer.get(this.runningKoth.getCapper()).getFaction().getOnlinePlayers()) {
                fp.sendMessage("§6[Capture] §eVotre faction contr\u00f4le la zone §6§l" + koth.getName() + "§e.");
            }
        }
        
        if (this.runningKoth.getCapper() != null) {
			// int sec = (int)(koth.getRemaining() / 1000);
			if (!list.isEmpty())
				list.forEach(faction -> {
					if (!Faction.get(faction.getFactionName()).getOnlinePlayers().contains(this.runningKoth.getCapper()) && faction.getPoints() >= 1)
						faction.setPoints(faction.getPoints() - 1);
				});

            UPlayer fplayer = UPlayer.get(this.runningKoth.getCapper());
    		this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).addPoints(1);
    		
    		for (Player player : this.runningKoth.getPlayersInside()) {
    			UPlayer uplayer = UPlayer.get(player);
    			if (uplayer == fplayer) continue;
    			this.plugin.getStatsManager().getFaction(uplayer.getFactionName()).addPoints(1);
    		}
    		
    		list.add(this.plugin.getStatsManager().getFaction(fplayer.getFactionName()));
            
            /*if (sec > 0 && sec % 30 == 0) {
            	//Chaque 30 secondes
            	Bukkit.broadcastMessage("§6[Capture] §eLa zone §6§l" + koth.getName() + " §eest en cours de contr\u00f4le.");
            }*/
    		
    		if (this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() == 900) {
            	Bukkit.broadcastMessage("§6[Capture] §e§lLa zone §6§l" + koth.getName() + " §e§l est en cours de contrôle §c(" + UPlayer.get(this.runningKoth.getCapper()).getFactionName() + ") §e§l" + (this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() / 18) + "%");
    		}
    		
    		if (this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() == 1450) {
            	Bukkit.broadcastMessage("§6[Capture] §e§lLa zone §6§l" + koth.getName() + " §e§l est en cours de contrôle §c(" + UPlayer.get(this.runningKoth.getCapper()).getFactionName() + ") §e§l" + (this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() / 18) + "%");
    		}
    		
    		if ((this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() / 18) % 2 == 0) {
    			this.runningKoth.getCapper().sendMessage("§eVotre faction à §6" + (this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() / 1800) + "% §ede la zone !");
			}
            
            if (this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).getPoints() >= 1800) {
            	//Quand le joueur gagne
            	Bukkit.broadcastMessage("§6[Capture] §e§lLa zone §6§l" + koth.getName() + " §e§lvient d'\u00eatre captur\u00e9 par §6§l" + this.runningKoth.getCapper().getName() + "§c(" + UPlayer.get(this.runningKoth.getCapper()).getFactionName() + ")§e§l.");
				
            	this.plugin.getStatsManager().getFactionsData().forEach(fac -> fac.setPoints(0));
            	this.plugin.getStatsManager().getFaction(fplayer.getFactionName()).addWin();
            	
            	for (String command : this.runningKoth.getKoth().getCommands()) {
            		if (command.contains("%faction%"))
            			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%faction%", fplayer.getFactionName()));
            		else 
                		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", this.runningKoth.getCapper().getName()));
            	}
            	
                this.runningKoth.getCapper().updateInventory();
                this.list.clear();
            }
        }
    }
}