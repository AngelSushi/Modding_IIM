package world.nations.koth.command;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import world.nations.Core;
import world.nations.koth.KothManager;
import world.nations.koth.data.KothData;
import world.nations.utils.ItemBuilder;
import world.nations.utils.Utils;
import world.nations.utils.command.Command;
import world.nations.utils.command.CommandArgs;

public class KothCommand {

	private Core plugin;
	private KothManager kothManager;
	private WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");

	public KothCommand(Core plugin) {
		this.plugin = plugin;
		this.kothManager = plugin.getKothManager();
	}

	@Command(name = "zone", permission = "zone.staff")
	public void kothCmd(CommandArgs args) {
		Player player = args.getPlayer();
		player.sendMessage(Utils.LINE);
		player.sendMessage(Utils.color("&e/zone create <nom>"));
		player.sendMessage(Utils.color("&e/zone delete <nom>"));
		player.sendMessage(Utils.color("&e/zone start <nom>"));
		player.sendMessage(Utils.color("&e/zone stop <nom>"));
		player.sendMessage(Utils.color("&e/zone reward <nom> <power/money> <nombre>"));
		player.sendMessage(Utils.color("&e/zone tp <nom>"));
		player.sendMessage(Utils.color("&e/zone list"));
		player.sendMessage(Utils.LINE);
	}

	@Command(name = "zone.create", permission = "op")
	public void kothCreateCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (args.length() < 1) {
			player.sendMessage(Utils.color("&e/zone create <nom>"));
			return;
		}

		if (this.worldEditPlugin.getSelection(player) == null) {
			player.sendMessage(Utils.color("&eS\u00e9l\u00e9ction de la zone invalide."));
			return;
		}

		String name = args.getArgs(0);
		KothData koth = new KothData(name, this.worldEditPlugin.getSelection(player));

		plugin.getKothManager().getKoths().add(koth);

		player.sendMessage(Utils.color("&eVous venez de cr\u00e9er la &9zone &b" + name + "&e."));
	}

	@Command(name = "zone.delete", permission = "op")
	public void kothDeleteCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (args.length() < 1) {
			player.sendMessage(Utils.color("&e/zone delete <nom>"));
			return;
		}

		String name = args.getArgs(0);

		if (this.kothManager.getKoth(name) == null) {
			player.sendMessage(Utils.color("&eCette zone n'existe pas"));
			return;
		}

		KothData koth = this.kothManager.getKoth(name);
		this.kothManager.getKoths().remove(koth);

		player.sendMessage(Utils.color("&eVous venez de supprimer la &9zone &b" + name + "&e."));
	}

	@Command(name = "zone.start", permission = "op")
	public void kothStartCmd(CommandArgs args) {
		Player player = args.getPlayer();
		if (args.length() < 1) {
			player.sendMessage(Utils.color("&e/zone start <nom>"));
			return;
		}

		String name = args.getArgs(0);

		if (this.kothManager.getKoth(name) == null) {
			player.sendMessage(Utils.color("&eCette zone n'existe pas"));
			return;
		}

		if (this.kothManager.isEnabled) {
			player.sendMessage(Utils.color("&eCette zone est d\u00e9j\u00e0 en cours."));
			return;
		}

		this.kothManager.startCountdown(this.kothManager.getKoth(name));
	}

	@Command(name = "zone.stop", permission = "op")
	public void kothStopCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (args.length() < 1) {
			player.sendMessage(Utils.color("&e/zone stop <nom>"));
			return;
		}

		String name = args.getArgs(0);
		if (this.kothManager.getKoth(name) == null) {
			player.sendMessage(Utils.color("&eCette zone n'existe pas"));
			return;
		}

		KothData koth = this.kothManager.getKoth(name);
		if (!this.kothManager.isEnabled) {
			player.sendMessage(Utils.color("&eCette zone n'est pas en cours."));
			return;
		}

		this.kothManager.stopKoth(this.kothManager.getKoth(name));
		Bukkit.broadcastMessage(Utils.color("&6[Capture] &eLa zone &9&l" + koth.getName() + " &evient d'\u00eatre stopp\u00e9."));
	}
	
	@Command(name = "zone.reward", permission = "zone.reward")
	public void kothRewardCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (args.length() < 2) {
			player.sendMessage(Utils.color("&e/zone reward <nom> <power/money> <nombre>"));
			return;
		}
		
		String name = args.getArgs(0);

		if (this.kothManager.getKoth(name) == null) {
			player.sendMessage(Utils.color("&eCette zone n'existe pas"));
			return;
		}
		
		KothData koth = this.kothManager.getKoth(name);
		
		int amount = 0;
		
		if (args.getArgs(1).equalsIgnoreCase("power")) {
			amount = Integer.parseInt(args.getArgs(2));
			koth.addCommand("f powerboost f %faction% " + amount);
			player.sendMessage("§eVous venez de définir le power a gagner sur §6" + amount + " §epour la zone §6" + koth.getName());
			return;
		} else if (args.getArgs(1).equalsIgnoreCase("money")) {
			amount = Integer.parseInt(args.getArgs(2));
			koth.addCommand("eco give %player% " + amount);
			player.sendMessage("§eVous venez de définir l'argent a gagner sur §6" + amount + " §epour la zone §6" + koth.getName());
			return;
		}
	}
	
	@Command(name = "zone.settp", permission = "zone.settp")
	public void kothsetTpCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (args.length() < 1) {
			player.sendMessage(Utils.color("&e/zone settp <nom>"));
			return;
		}

		String name = args.getArgs(0);

		if (this.kothManager.getKoth(name) == null) {
			player.sendMessage(Utils.color("&eCette zone n'existe pas"));
			return;
		}

		KothData koth = this.kothManager.getKoth(name);
		koth.setZoneTP(player.getLocation());

		player.sendMessage(Utils.color("&eVous venez de définir la zone de tp."));
	}

	@Command(name = "zone.tp", permission = "zone.tp")
	public void kothTpCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (!this.kothManager.getKoths().isEmpty()) {

			Inventory inv = Bukkit.createInventory(null, 9, "§eZone TP");

			for (KothData data : this.kothManager.getKoths()) {
				inv.addItem(new ItemBuilder(Material.PAPER).setName("§e" + data.getName()).setLore("§7Cliquez pour vous téléporter.").toItemStack());
			}

			player.openInventory(inv);
			player.updateInventory();
			return;
		} else {
			player.sendMessage("Il n'y a aucune zone de créée !");
			return;
		}

	}

	@Command(name = "zone.list", permission = "op")
	public void kothListCmd(CommandArgs args) {
		Player player = args.getPlayer();

		if (this.kothManager.getKoths().isEmpty()) {
			player.sendMessage(Utils.color("&eAucune &9zone &edisponible."));
			return;
		}

		for (KothData koth : this.kothManager.getKoths()) {
			player.sendMessage(Utils.color("&7- &6" + koth.getName()));
		}
	}
}
