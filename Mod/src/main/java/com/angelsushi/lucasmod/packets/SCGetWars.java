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

public class SCGetWars implements IMessage {

    @AllArgsConstructor
    public class WarData {
        public ArrayList<FactionWar> wars = new ArrayList<FactionWar>();
        public int winCount;
        public int defeatCount;
        public int allWars;

        public WarData() {}
    }

    @AllArgsConstructor
    public class FactionWar {
        public String attackerName;
        public String defenderName;
        public int attackerKill;
        public int defenderKill;
        public boolean finish;
        public String winner;
    }

    public WarData warData;

    public SCGetWars() { }

    @Override
    public void fromBytes(ByteBuf buf) {

        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        warData = new WarData();
        int size = reader.readInt();
        for(int i = 0;i<size;i++) {
            String attackerName = reader.readUTF();
            String defenderName = reader.readUTF();
            int attackerKill = reader.readInt();
            int defenderKill = reader.readInt();
            boolean isFinish = reader.readBoolean();
            String winner = reader.readUTF();

            FactionWar war = new FactionWar(attackerName,defenderName,attackerKill,defenderKill,isFinish,winner);
            warData.wars.add(war);
        }

        int winCount = reader.readInt();
        int defeatCount = reader.readInt();

        warData.winCount = winCount;
        warData.defeatCount = defeatCount;
        warData.allWars = size;
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<SCGetWars, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCGetWars msg, MessageContext ctx) {

            Minecraft.getMinecraft().thePlayer.closeScreen();
            GuiFaction.warData = msg.warData;
            Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(8));

            return null;
        }

    }

}
