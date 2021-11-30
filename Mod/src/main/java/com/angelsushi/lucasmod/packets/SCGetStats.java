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

public class SCGetStats implements IMessage {

    @AllArgsConstructor
    public class FactionStats {
        public String ally;
        public int kills;
        public int deaths;
        public String kd;
        public int winCount;
        public int defeatCount;
        public int warKD;
        public String age;
        public double bank;
        public int landCount;
    }

    public FactionStats stats;
    public SCGetStats() {}


    @Override
    public void fromBytes(ByteBuf buf) {

        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        String ally = reader.readUTF();
        int kills = reader.readInt();
        int deaths = reader.readInt();
        String kd = reader.readUTF();
        int winCount = reader.readInt();
        int defeatCount = reader.readInt();
        int warKD = reader.readInt();
        String age = reader.readUTF();
        double bank = reader.readDouble();
        int landCount = reader.readInt();


        System.out.println("account: " + bank);
        stats = new FactionStats(ally,kills,deaths,kd,winCount,defeatCount,warKD,age,bank,landCount);
    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCGetStats, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCGetStats msg, MessageContext ctx) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
            GuiFaction.stats = msg.stats;
            Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(9));
            return null;
        }

    }
}
