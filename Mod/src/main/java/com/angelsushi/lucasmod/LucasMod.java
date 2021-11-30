package com.angelsushi.lucasmod;

import com.angelsushi.lucasmod.packets.*;
import com.angelsushi.lucasmod.proxy.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = LucasMod.MOD_ID,name="WoNInterfaces ",version = "1.0.0")
public class LucasMod {

    public static final String MOD_ID = "woninterfaces";

    @Mod.Instance
    public static LucasMod instance;

    @SidedProxy(clientSide = "com.angelsushi.lucasmod.proxy.ClientProxy",serverSide = "com.angelsushi.lucasmod.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper NETWORK,NETWORK_FACTIONS_LIST,NETWORK_ONLINE_PLAYERS,NETWORK_FACTION_INFO,NETWORK_BANK,NETWORK_RELATION,NETWORK_WARS,NETWORK_STATS,NETWORK_INVITATION;
    public EventListener listener;
    public static float lastMouseX,lastMouseY;

    public static int lastPage;
    /*
        Data a récupérer :
            


            Alliés & Enemies des factions

     */
    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.register();
        NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("factionData");
        NETWORK_FACTIONS_LIST = NetworkRegistry.INSTANCE.newSimpleChannel("sendFList");
        NETWORK_ONLINE_PLAYERS = NetworkRegistry.INSTANCE.newSimpleChannel("sendOPlayers");
        NETWORK_FACTION_INFO = NetworkRegistry.INSTANCE.newSimpleChannel("sendFInfo");
        NETWORK_BANK = NetworkRegistry.INSTANCE.newSimpleChannel("sendFExchanges");
        NETWORK_RELATION = NetworkRegistry.INSTANCE.newSimpleChannel("sendAllies");
        NETWORK_WARS = NetworkRegistry.INSTANCE.newSimpleChannel("sendWars");
        NETWORK_STATS = NetworkRegistry.INSTANCE.newSimpleChannel("sendStats");
        NETWORK_INVITATION = NetworkRegistry.INSTANCE.newSimpleChannel("displayInvite");

        NetworkRegistry.INSTANCE.newChannel("getFList",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("getOPlayers",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("getFInfo",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("getFExchanges",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("setLeader",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("getAllies",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("getWars",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("getStats",new PacketHandler());
        NetworkRegistry.INSTANCE.newChannel("createFac",new PacketHandler());

        //NETWORK.registerMessage(SCGetPlayer.Handler.class,SCGetPlayer.class,0, Side.CLIENT);
        NETWORK.registerMessage(SCAssaultData.Handler.class,SCAssaultData.class,0, Side.CLIENT);

        NETWORK_FACTIONS_LIST.registerMessage(SCFactionList.Handler.class,SCFactionList.class,0,Side.CLIENT);
        NETWORK_ONLINE_PLAYERS.registerMessage(SCOnlinePlayers.Handler.class,SCOnlinePlayers.class,0,Side.CLIENT);
        NETWORK_FACTION_INFO.registerMessage(SCFactionInfo.Handler.class,SCFactionInfo.class,0,Side.CLIENT);
        NETWORK_BANK.registerMessage(SCGetBank.Handler.class,SCGetBank.class,0,Side.CLIENT);
        NETWORK_RELATION.registerMessage(SCGetAllies.Handler.class,SCGetAllies.class,0,Side.CLIENT);
        NETWORK_WARS.registerMessage(SCGetWars.Handler.class,SCGetWars.class,0,Side.CLIENT);
        NETWORK_STATS.registerMessage(SCGetStats.Handler.class,SCGetStats.class,0,Side.CLIENT);
        NETWORK_INVITATION.registerMessage(SCDisplayInvite.Handler.class,SCDisplayInvite.class,0,Side.CLIENT);

        listener = new EventListener();
        MinecraftForge.EVENT_BUS.register(listener);
        FMLCommonHandler.instance().bus().register(listener);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {

    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {

    }


}
