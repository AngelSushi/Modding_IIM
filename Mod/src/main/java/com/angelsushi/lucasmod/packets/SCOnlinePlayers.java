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

public class SCOnlinePlayers implements IMessage {

    @AllArgsConstructor
    public class PlayerData {
        public String name;
        public String factionName;
        public double power;
    }

    public ArrayList<PlayerData> playersData = new ArrayList<PlayerData>();
    public SCOnlinePlayers() {}


    @Override
    public void fromBytes(ByteBuf buf) {
        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        int size = reader.readInt();

        for(int i = 0;i<size;i++) {
            String name = reader.readUTF();
            double power = reader.readDouble();
            String factionName = reader.readUTF();

            playersData.add(new PlayerData(name,factionName,power));
        }

    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCOnlinePlayers, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCOnlinePlayers msg, MessageContext ctx) {

            Minecraft.getMinecraft().thePlayer.closeScreen();
            GuiFaction.onlinePlayersData = msg.playersData;
            Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(3));
            return null;
        }

    }
}
