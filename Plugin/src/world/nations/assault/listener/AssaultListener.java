package world.nations.assault.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.massivecraft.factions.entity.UPlayer;

import world.nations.Core;
import world.nations.assault.data.AssaultData;
import world.nations.utils.DurationFormatter;
import world.nations.utils.timings.CooldownTimer;

public class AssaultListener implements Listener {
	
	private Core plugin;
	
	public AssaultListener(Core plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		UPlayer fplayer = UPlayer.get(event.getEntity());
		if (fplayer.hasFaction()) {
			if (CooldownTimer.isOnCooldown("assault", fplayer.getFaction())) {

				UPlayer kplayer = UPlayer.get(event.getEntity().getKiller());

				for (AssaultData consumer : this.plugin.getAssaultManager().getAssaultList()) {
					
					if (consumer.getFactionName() == fplayer.getFactionName() && consumer.getFactionTarget() == kplayer.getFactionName()) {
						consumer.addTargetPoints(1);
						Bukkit.broadcastMessage("§6" + fplayer.getPlayer().getName() + " §eest mort au combat, §c" + kplayer.getFactionName() + " §egagne un point (" + consumer.getPointsTarget() + ")");
						Bukkit.broadcastMessage("§6Temps restant : " + DurationFormatter.getRemaining((consumer.getTimer().getSecondsLeft() * 1000), false));
						break;
					} else if (consumer.getFactionTarget() == fplayer.getFactionName() && consumer.getFactionName() == kplayer.getFactionName()) {
						consumer.addPoints(1);
						Bukkit.broadcastMessage("§6" + fplayer.getPlayer().getName() + " §eest mort au combat, §c" + kplayer.getFactionName() + " §egagne un point (" + consumer.getPoints() + ")");
						Bukkit.broadcastMessage("§6Temps restant : " + DurationFormatter.getRemaining((consumer.getTimer().getSecondsLeft() * 1000), false));
						break;
					}
				}
			}
		}
	}
}
