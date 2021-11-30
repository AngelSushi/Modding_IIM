package com.angelsushi.lucasmod.packets;

import com.angelsushi.lucasmod.EventListener;
import com.angelsushi.lucasmod.LucasMod;
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
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class SCFactionList implements IMessage {

    @AllArgsConstructor
    @Getter
    public class FactionData {
        String factionName;
        int sizePlayers;
        String age;

        public FactionData(String factionName) {
            this.factionName = factionName;
        }
    }
    public ArrayList<FactionData> factions = new ArrayList<FactionData>();
    public boolean exist;

    public SCFactionList() {}


    @Override
    public void fromBytes(ByteBuf buf) {
        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        int size = reader.readInt();

        for(int i = 0;i<size;i++) {
            String factionName = reader.readUTF();
            exist = reader.readBoolean();
            FactionData fData = new FactionData(factionName);
            if(exist) {
                int sizePlayers = reader.readInt();
                String age = reader.readUTF();
                fData.sizePlayers = sizePlayers;
                fData.age = age;
            }

            factions.add(fData);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCFactionList, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCFactionList msg, MessageContext ctx) {

            Minecraft.getMinecraft().thePlayer.closeScreen();

            GuiFaction.factionDataList = msg.factions;
            if(msg.exist)
                Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(1));

            else
                Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(2));

            return null;
        }

    }
}
