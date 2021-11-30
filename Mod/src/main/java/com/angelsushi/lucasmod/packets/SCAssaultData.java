package com.angelsushi.lucasmod.packets;

import com.angelsushi.lucasmod.EventListener;
import com.angelsushi.lucasmod.LucasMod;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;

public class SCAssaultData implements IMessage {

    public String facAssaultName;
    public String facDefendName;

    public int scoreAssault,scoreDefend;
    public float timer;

    public ArrayList<String> assaultPlayers = new ArrayList<String>();
    public ArrayList<String> defendPlayers = new ArrayList<String>();

    public SCAssaultData() {}


    @Override
    public void fromBytes(ByteBuf buf) {
        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());
        this.facAssaultName = reader.readUTF();
        this.facDefendName = reader.readUTF();

        this.scoreAssault = reader.readInt();
        this.scoreDefend = reader.readInt();
        this.timer = reader.readFloat();

        int sizeAssault = reader.readInt();

        for(int i = 0;i<sizeAssault;i++)
            this.assaultPlayers.add(reader.readUTF());


        int sizeDefend = reader.readInt();
        for(int i = 0;i<sizeDefend;i++)
            this.defendPlayers.add(reader.readUTF());

    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCAssaultData,IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCAssaultData msg, MessageContext ctx) {
            String id = Minecraft.getMinecraft().thePlayer.getUniqueID().toString();

            EventListener listener = LucasMod.instance.listener;
            listener.setInAssault(!(msg.timer <= 0));

            if(listener.isInAssault()) {
                listener.setAssaultName(msg.facAssaultName);
                listener.setDefendName(msg.facDefendName);
                listener.setScoreAssault(msg.scoreAssault);
                listener.setScoreDefend(msg.scoreDefend);
                listener.setTimer(msg.timer);
            }
            else {
                listener.setAssaultName("");
                listener.setDefendName("");
                listener.setScoreAssault(0);
                listener.setScoreDefend(0);
                listener.setTimer(0f);
            }
            return null;
        }

    }
}
