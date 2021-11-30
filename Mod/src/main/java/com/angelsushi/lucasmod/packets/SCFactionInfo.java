package com.angelsushi.lucasmod.packets;

import com.angelsushi.lucasmod.gui.GuiFaction;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class SCFactionInfo implements IMessage {

    @AllArgsConstructor
    public class FactionData {
        public String leaderName;
        public int landCount;
        public double maxPower;
        public double power;
        public int players;
        public ArrayList<String> playersOnline;
        public ArrayList<String> allPlayers;
        public String age;
    }

    public ArrayList<FactionData> factionData = new ArrayList<FactionData>();
    public String facName;

    public SCFactionInfo() { }

    @Override
    public void fromBytes(ByteBuf buf) {
        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());
        facName = reader.readUTF();
        int players = reader.readInt();
        int landCount = reader.readInt();
        double maxPower = reader.readDouble();
        double power = reader.readDouble();

        String leaderName = reader.readUTF();

        int sizeOnline = reader.readInt();
        ArrayList<String> playersOnline = new ArrayList<String>();
        for(int i = 0;i<sizeOnline;i++)
            playersOnline.add(reader.readUTF());

        ArrayList<String> allPlayers = new ArrayList<String>();
        for(int i = 0;i<players;i++)
            allPlayers.add(reader.readUTF());

        String age = reader.readUTF();

        factionData.add(new FactionData(leaderName,landCount,maxPower,power,players,playersOnline,allPlayers,age));
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<SCFactionInfo, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCFactionInfo msg, MessageContext ctx) {

            Minecraft.getMinecraft().thePlayer.closeScreen();
            GuiFaction.factionData = msg.factionData;
            GuiFaction.currentFacName = msg.facName;


            for(int i = 0;i<msg.factionData.get(0).allPlayers.size();i++) {
                boolean isLeader = msg.factionData.get(0).allPlayers.get(i).contains("**");
                boolean isOfficer = msg.factionData.get(0).allPlayers.get(i).contains("*");
                boolean isMember = msg.factionData.get(0).allPlayers.get(i).contains("+");

                String name =  msg.factionData.get(0).allPlayers.get(i).replace(isLeader ? "**" : isOfficer ? "*" : isMember ? "+" : "-","");

                if(name.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getDisplayName())) {
                    GuiFaction.isPlayerFac = true;
                    break;
                }
                else
                    GuiFaction.isPlayerFac = false;
            }

            Minecraft.getMinecraft().displayGuiScreen( new GuiFaction(5));
            return null;
        }

    }
}