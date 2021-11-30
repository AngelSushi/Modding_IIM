package world.nations.assault.command;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.cmd.VisibilityMode;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;

import world.nations.Core;
import world.nations.assault.data.AssaultData;
import world.nations.mod.SendDatas;
import world.nations.stats.data.FactionData;
import world.nations.utils.timings.CooldownTimer;
import world.nations.utils.timings.CountdownTimer;

public class AssaultCommand extends MassiveCommand  {

	private Core plugin;
	
	public AssaultCommand(Core plugin) {
		this.plugin = plugin;
		
		this.addAliases("assault");

		this.addRequiredArg("factionEnemy");
		
		this.addOptionalArg("stop", "");
		this.addOptionalArg("join", "");
		
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.LEADER));
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
		
		this.setVisibilityMode(VisibilityMode.VISIBLE);
	}

	@Override
	public void perform() {
		Player player = (Player) sender;
		UPlayer fplayer = UPlayer.get(player);
		
		Faction otherFaction = this.arg(0, ARFaction.get(sender));
		if (otherFaction == null) return;
		
		if (otherFaction == fplayer.getFaction()) {
			player.sendMessage("§cVous ne pouvez pas vous attaquer !");
			return;
		}
		
		if (this.arg(1) == null && otherFaction.getRelationTo(fplayer.getFaction()) == Rel.ALLY) {
			player.sendMessage("§cVous ne pouvez pas attaquer vos alliés !");
			return;
		}
		
		if (this.arg(1) != null && this.arg(1).equalsIgnoreCase("join")) {
			if (fplayer.getRelationTo(otherFaction) == Rel.ALLY) {
				AssaultData data = this.plugin.getAssaultManager().getFaction(otherFaction);
				
				if (data == null) return;
				
				data.setFactionAllied(new ArrayList<String>(Arrays.asList(fplayer.getFactionName())));
				Bukkit.broadcastMessage("§eL'alliance §9" + fplayer.getFactionName() + " §evient de rejoindre la faction §9" + otherFaction.getName() + " §epour l'assaut.");
				return;
			} else {
				fplayer.sendMessage("§cVotre faction ne peut pas rejoindre cet assaut !");
				return;
			}
		}
		
		if (this.arg(0) != null && this.arg(1) != null && this.arg(1).equalsIgnoreCase("stop")) {
			AssaultData data = this.plugin.getAssaultManager().getFaction(otherFaction);
			if (data == null) return;
			
			if (CooldownTimer.isOnCooldown("assault", data.getFaction()) && CooldownTimer.isOnCooldown("assault", data.getTarget())) {
				CooldownTimer.removeCooldown("assault", data.getFaction());
				CooldownTimer.removeCooldown("assault", data.getTarget());
				Bukkit.getScheduler().cancelTask(data.getTimer().getAssignedTaskId());
				fplayer.sendMessage("§cVous venez d'arrêter l'assaut de la faction §4" + otherFaction.getName());
				return;
			} else {
				fplayer.sendMessage("§cCette faction n'est pas en assaut !");
				return;
			}
		}
		
		if (CooldownTimer.isOnCooldown("timeLeft", fplayer.getFaction())) {
			player.sendMessage("§cVous êtes encore en cooldown pendant " + DurationFormatUtils.formatDuration(CooldownTimer.getCooldownForPlayerLong("timeLeft", fplayer.getFaction()), "HH'heures' mm'mins' ss'sec'", false));
			return;
		}
		
		CooldownTimer.addCooldown("timeLeft", fplayer.getFaction(), 10800);
		
		CooldownTimer.addCooldown("assault", fplayer.getFaction(), 1800);
		CooldownTimer.addCooldown("assault", otherFaction, 1800);
		
		CountdownTimer timer = new CountdownTimer(plugin, 1800, () -> {
			AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());
		
			Bukkit.broadcastMessage("§aLa faction §c" + fplayer.getFactionName() + " §avient de lancer un assaut contre la faction §c" + otherFaction.getName());
			SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), data.getTimer().getSecondsLeft(),data.getFactionAllied(), (Player) sender);
			
		},
		() -> {
			AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());
			
			
			Bukkit.broadcastMessage("§aLa faction §c" + (data.getPoints() > data.getPointsTarget() ? data.getFactionName() + " (" + data.getPoints() : data.getFactionTarget() + " (" + data.getPointsTarget()) + ") §avient de gagner l'assaut contre la faction §c" + (data.getPoints() > data.getPointsTarget() ? data.getFactionTarget() + " (" + data.getPointsTarget() : data.getFactionName() + " (" + data.getPoints()) + ")");
			
			data.setPoints(0);
			data.setPointsTarget(0);
			
			FactionData faction = this.plugin.getStatsManager().getFaction(data.getFactionName());
			FactionData target = this.plugin.getStatsManager().getFaction(data.getFactionTarget());
			
			SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), 0,data.getFactionAllied(), (Player) sender);
			
			
			if (data.getPoints() > data.getPointsTarget()) {
				faction.addWin();
				target.addLose();
			} else {
				target.addWin();
				faction.addLose();
			}
			
		}, (t) -> {
			
			AssaultData data = this.plugin.getAssaultManager().getFaction(fplayer.getFaction());
			SendDatas.sendAssaultDatas(fplayer.getFaction(), otherFaction, data.getPoints(), data.getPointsTarget(), data.getTimer().getSecondsLeft(),data.getFactionAllied(), (Player) sender);
	
		});
		
		timer.scheduleTimer();

		this.plugin.getAssaultManager().addFaction(fplayer.getFaction(), otherFaction, timer);
		return;
	}

}
