package world.nations.bank.listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsCreate;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsInvitedChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.ps.PS;

import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;
import world.nations.Core;
import world.nations.bank.EconomyManager;
import world.nations.bank.data.BankData;
import world.nations.mod.FactionExchange;
import world.nations.mod.PacketFunctions;
import world.nations.mod.PacketListener;

public class FactionListener implements Listener {
	
	private Core plugin;
	
	public FactionListener(Core plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onCreate(EventFactionsCreate event) {
		String fname = event.getFactionName();
		EconomyManager manager = this.plugin.getEconomyManager();
		
		if (!manager.getFactionsNames().contains(fname)) {
			manager.getFactionsMap().add(new BankData(fname));
		}
		
		if(!Core.factionExchanges.containsKey(event.getFactionId())) 
			Core.factionExchanges.put(event.getFactionId(), new ArrayList<FactionExchange>());			
	}
	
	@EventHandler
	public void onLeaveFaction(EventFactionsMembershipChange e) {
		if(e.getReason() == MembershipChangeReason.JOIN) {
			ByteArrayDataOutput buf = ByteStreams.newDataOutput();
			
	    	buf.writeUTF("Vous venez de rejoindre le pays " + e.getNewFaction().getName());
	    	buf.writeUTF(e.getNewFaction().getName());
	    	buf.writeBoolean(false);
	    	buf.writeInt(5);
	    	
	    	e.getUPlayer().getPlayer().sendPluginMessage(Core.plugin, "displayInvite", buf.toByteArray());
	    	
	    	for(Player p : e.getNewFaction().getOnlinePlayers()) {
	    		buf = ByteStreams.newDataOutput();
	    		
	    		buf.writeUTF(e.getUPlayer().getName() + " vient de rejoindre votre pays");
	    		buf.writeUTF(e.getNewFaction().getName());
		    	buf.writeBoolean(false);
		    	buf.writeInt(5);
		    	
	    		p.sendPluginMessage(Core.plugin, "displayInvite", buf.toByteArray());
	    	}
		}
		
		if(e.getReason() == MembershipChangeReason.LEAVE && e.getNewFaction().getUPlayers().size() > 1) {
			ByteArrayDataOutput buf = ByteStreams.newDataOutput();		
			
	    	buf.writeUTF("Vous venez de quitter le pays " + e.getNewFaction().getName());
	    	buf.writeUTF(e.getNewFaction().getName());
	    	buf.writeBoolean(false);
	    	buf.writeInt(5);
	    	
	    	if(e.getSender() != null && e.getSender() instanceof Player) {
	    		((Player) e.getSender()).sendPluginMessage(Core.plugin, "displayInvite", buf.toByteArray());
	    	}
	    	
	    	for(Player p : e.getNewFaction().getOnlinePlayers()) {
	    		buf = ByteStreams.newDataOutput();
	    		buf.writeUTF(e.getUPlayer().getName() + " vient de quitter votre pays");
		    	buf.writeUTF(e.getNewFaction().getName());
		    	buf.writeBoolean(false);
		    	buf.writeInt(5);
		    	
	    		p.sendPluginMessage(Core.plugin, "displayInvite", buf.toByteArray());
	    	}
		}
		
		
		if((e.getReason() == MembershipChangeReason.LEAVE && e.getNewFaction().getUPlayers().size() == 1) ||  e.getReason() == MembershipChangeReason.DISBAND) {
			if(!Core.availableCountries.containsKey(e.getNewFaction().getName())) {
				if(e.getNewFaction().getHome() != null) {
					Core.availableCountries.put(e.getNewFaction().getName(), e.getNewFaction().getHome().asBukkitLocation());
				}
				else {
					Core.availableCountries.put(e.getNewFaction().getName(), new Location(e.getUPlayer().getPlayer().getLocation().getWorld(),0,90,0));
				}
			}
		}
		
		if(e.getReason() == MembershipChangeReason.CREATE) {
			FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
			if(Core.availableCountries.containsKey(e.getNewFaction().getName()) && Core.availableCountries.get(e.getNewFaction().getName()) != null) {
				e.getUPlayer().tryClaim(e.getNewFaction(), PS.valueOf(Core.availableCountries.get(e.getNewFaction().getName())), true, true);
				coll.getByName(e.getNewFaction().getName()).setHome(PS.valueOf(Core.availableCountries.get(e.getNewFaction().getName())));
				Core.availableCountries.remove(e.getNewFaction().getName());
			}
		}
		
		
	}
	
	@EventHandler
	public void onInvitePlayer(EventFactionsInvitedChange e) {
		if(e.isNewInvited()) {
			ByteArrayDataOutput buf = ByteStreams.newDataOutput();		
			
	    	buf.writeUTF("Vous venez d'être invité dans le pays " + e.getFaction().getName());
	    	buf.writeUTF(e.getFaction().getName());
	    	buf.writeBoolean(true);
	    	buf.writeInt(10);
	    		
	    	e.getUPlayer().getPlayer().sendPluginMessage(Core.plugin, "displayInvite", buf.toByteArray());
			
	    	for(Player p : e.getUSender().getFaction().getOnlinePlayers()) {
	    		
	    		UPlayer player = UPlayer.get(p.getUniqueId());
	    		
	    		if(player.getRole() == Rel.LEADER || player.getRole() == Rel.OFFICER) {
	    			buf = ByteStreams.newDataOutput();
		    		buf.writeUTF(e.getUPlayer().getName() + " vient d'être invité dans votre pays");
		    		buf.writeUTF(e.getFaction().getName());
		    		buf.writeBoolean(false);
			    	buf.writeInt(5);
		    		
	    			p.sendPluginMessage(Core.plugin, "displayInvite", buf.toByteArray());
	    		}
	    	}
		}
	    	
	}
	
	@EventHandler
	public void onDisbandFaction(final EventFactionsDisband event) {
		final String fname = event.getFaction().getName();
		EconomyManager manager = this.plugin.getEconomyManager();
		
		if (manager.getFactionsNames().contains(fname)) {
			manager.getFactionsMap().removeIf(faction -> (faction.getFactionName().equalsIgnoreCase(fname)));
			manager.getFactionsNames().remove(fname);
		}
		
		if(!Core.availableCountries.containsKey(event.getFaction().getName())) {	
			if(event.getFaction().getHome() != null)
				Core.availableCountries.put(event.getFaction().getName(), event.getFaction().getHome().asBukkitLocation());
			else
				Core.availableCountries.put(event.getFaction().getName(), new Location(event.getFaction().getLeader().getPlayer().getWorld(),0,0,0));
		}
		
	}
	
	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		System.out.println("id: " + e.getPlayer().getUniqueId().toString());
		
		UPlayer uPlayer = UPlayer.get(e.getPlayer().getUniqueId());
		
		if(uPlayer.hasFaction()) {
			if(!Core.factionExchanges.containsKey(uPlayer.getFactionId())) 
				Core.factionExchanges.put(uPlayer.getFactionId(), new ArrayList<FactionExchange>());
			
		}
	}
	
	@EventHandler
	public void onNameChange(EventFactionsNameChange event) {
		String fname = event.getFaction().getName();
		String oldFname = event.getNewName();
		EconomyManager manager = this.plugin.getEconomyManager();
		manager.getFactionsMap().forEach(faction -> {
			if (faction.getFactionName().equalsIgnoreCase(oldFname)) {
				faction.setFactionName(fname);
				manager.getFactionsNames().remove(oldFname);
				manager.getFactionsNames().add(fname);
			}
		});
	}
	
	@EventHandler
	public void onSendCommand(PlayerCommandPreprocessEvent e) {
		FactionColl coll = PacketListener.getFactionColl("factions_faction@default");
		ByteArrayDataOutput buf = ByteStreams.newDataOutput();
		
		if(e.getMessage().equalsIgnoreCase("/f list")) {
			PacketFunctions.getFactionList(false, coll, buf, e.getPlayer());
			return;
		}
		
		if(e.getMessage().contains("/f f")|| e.getMessage().contains("/F f")) {
			e.setCancelled(true);
			
			if(e.getMessage().length() > 5) {
				String arg = e.getMessage().substring(5, e.getMessage().length());
				
				if(arg.equals("")) {
					UPlayer player = UPlayer.get(e.getPlayer().getUniqueId());
					if(player != null && player.hasFaction()) 	
						PacketFunctions.getFactionInfo(buf, coll, player.getFactionName(), e.getPlayer());			
					else {
						PacketFunctions.getFactionList(false, coll, buf, e.getPlayer());
					}
				}
				else {
					Faction targetFac = coll.getByName(arg);	
					if(targetFac != null) {
						PacketFunctions.getFactionInfo(buf, coll, targetFac.getName(), e.getPlayer());					
					}
					else {
						int numberFacWithArg = 0;
						for(Faction fac : coll.getAll()) {
							if(!fac.getName().equalsIgnoreCase("§aWilderness") && !fac.getName().equalsIgnoreCase("safezone") && !fac.getName().equalsIgnoreCase("warzone")) {
								if(fac.getName().toLowerCase().contains(arg) || fac.getName().contains(arg) || fac.getName().toUpperCase().contains(arg)) {
									targetFac = fac;
									numberFacWithArg++;
								}
							}
						}
						
						if(numberFacWithArg == 1) 
							PacketFunctions.getFactionInfo(buf, coll, targetFac.getName(), e.getPlayer());
						else if(numberFacWithArg == 0) {
							Player p = Bukkit.getPlayer(arg);
							
							if(p != null) {
								UPlayer uPlayer = UPlayer.get(p.getUniqueId());
								PacketFunctions.getFactionInfo(buf, coll, uPlayer.getFaction().getName(), e.getPlayer());
							}
							else {
								for(Faction f : coll.getAll()) {
									for(UPlayer up : f.getUPlayers()) {
										if(up.getName().equals(arg)) {
											PacketFunctions.getFactionInfo(buf, coll, f.getName(), e.getPlayer());
											return;
										}
									}
								}
								
								e.getPlayer().sendMessage("§cAucune faction n'a été trouvé.");	
							}
						}
									
						else 
							e.getPlayer().sendMessage("§cIl y a trop de factions avec ce nom là.");
					}
				}
			}
			else {
				UPlayer player = UPlayer.get(e.getPlayer().getUniqueId());
				if(player != null && player.hasFaction()) 	
					PacketFunctions.getFactionInfo(buf, coll, player.getFactionName(), e.getPlayer());		
				else
					PacketFunctions.getFactionList(false, coll, buf, e.getPlayer());
			}
		}
		
		else if((e.getMessage().contains("/f create") || e.getMessage().contains("/F create")) && !UPlayer.get(e.getPlayer().getUniqueId()).isUsingAdminMode() ) {
			e.setCancelled(true);	
			
			UPlayer uplayer = UPlayer.get(e.getPlayer().getUniqueId());
			
			if(!uplayer.hasFaction()) 
				PacketFunctions.getFactionList(false, coll, buf, e.getPlayer());			
			else
				e.getPlayer().sendMessage("§cVous êtes déjà dans une faction");
		}
	}
}