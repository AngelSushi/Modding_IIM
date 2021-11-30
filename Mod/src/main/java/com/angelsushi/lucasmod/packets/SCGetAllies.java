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
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class SCGetAllies implements IMessage {

    @AllArgsConstructor
    @Getter
    public class FactionRelation {

        public ArrayList<String> alliesFac;
        public ArrayList<String> trucesFac;
    }

    public SCGetAllies() {}

    public FactionRelation relation;

    @Override
    public void fromBytes(ByteBuf buf) {

        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        int alliesSize = reader.readInt();
        ArrayList<String> alliesFac = new ArrayList<String>();
        for(int i = 0;i<alliesSize;i++)
            alliesFac.add(reader.readUTF());


        int trucesSize = reader.readInt();
        ArrayList<String> trucesFac = new ArrayList<String>();
        for(int i = 0;i<trucesSize;i++)
            trucesFac.add(reader.readUTF());

        relation = new FactionRelation(alliesFac,trucesFac);

    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCGetAllies, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCGetAllies msg, MessageContext ctx) {

            Minecraft.getMinecraft().thePlayer.closeScreen();
            GuiFaction.relation = msg.relation;
            Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(6));
            return null;
        }

    }
}
