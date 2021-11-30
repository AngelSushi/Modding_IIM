package world.nations.koth.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import world.nations.Core;
import world.nations.koth.data.KothData;
import world.nations.utils.Utils;

public class KothListener implements Listener {
	
	private Core plugin;
	
	public KothListener(Core plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void InventoryInteractEvent(InventoryClickEvent e) {
		if (!e.getInventory().getName().equalsIgnoreCase("§eZone TP")) {
			return;
		}

		e.setCancelled(true);

		if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR|| !e.getCurrentItem().hasItemMeta()) {
			return;
		}
	}
	
	@EventHandler
	public void onInvetoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (event.getInventory().getTitle().equalsIgnoreCase("§eZone TP")) {
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.PAPER) {
				for (KothData data : this.plugin.getKothManager().getKoths()) {
					if (event.getCurrentItem().getItemMeta().getDisplayName().contains(data.getName())) {
						if (data.getZoneTP() != null) {
							player.teleport(data.getZoneTP());
							player.sendMessage(Utils.color("&eVous \u00eates maintenant dans la zone &b" + data.getName() + "&e."));
							break;
						} else {
							player.sendMessage(Utils.color("&cCette zone ne possède pas de TP !"));
							break;
						}
					}
				}
			}
		}
	}
}
