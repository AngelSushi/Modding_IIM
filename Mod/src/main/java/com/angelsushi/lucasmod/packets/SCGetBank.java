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

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SCGetBank implements IMessage {

    @AllArgsConstructor
    @Getter
    public static class FactionBank {
        String factionName;
        int account;
        ArrayList<Exchange> bankExchanges;

        @AllArgsConstructor
        public static class Exchange {
            public int amount;
            public String sender;
            public LocalDateTime date;
        }
    }
    public FactionBank factionBank;
    public boolean exist;

    public SCGetBank() {}


    @Override
    public void fromBytes(ByteBuf buf) {
        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        String facName = reader.readUTF();

        int size = reader.readInt();
        ArrayList<FactionBank.Exchange> allExchanges = new ArrayList<FactionBank.Exchange>();

        for(int i = 0;i<size;i++) {
            int amount = reader.readInt();
            String sender = reader.readUTF();
            String date = reader.readUTF();
            FactionBank.Exchange exchange = new FactionBank.Exchange(amount,sender, LocalDateTime.parse(date));
            allExchanges.add(exchange);
        }

        int account = reader.readInt();


        factionBank = new FactionBank(facName,account,allExchanges);

    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCGetBank, IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCGetBank msg, MessageContext ctx) {

            Minecraft.getMinecraft().thePlayer.closeScreen();
            GuiFaction.factionBank = msg.factionBank;
            Minecraft.getMinecraft().displayGuiScreen( new GuiFaction(7));

            return null;
        }

    }
}
