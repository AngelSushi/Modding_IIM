package com.angelsushi.lucasmod.packets;

import com.angelsushi.lucasmod.EventListener;
import com.angelsushi.lucasmod.LucasMod;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import tv.twitch.Core;

import java.util.ArrayList;

public class SCDisplayInvite implements IMessage {

    public SCDisplayInvite() {}

    private String displayInvitation;
    private String facName;
    private boolean displayButton;
    private int timer;

    @Override
    public void fromBytes(ByteBuf buf) {
        ByteArrayDataInput reader = ByteStreams.newDataInput(buf.array());

        displayInvitation = reader.readUTF();
        facName = reader.readUTF();
        displayButton = reader.readBoolean();
        timer = reader.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<SCDisplayInvite,IMessage> {

        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(SCDisplayInvite msg, MessageContext ctx) {

            LucasMod.instance.listener.setDisplayInvite(true);
            LucasMod.instance.listener.setNeedButton(msg.displayButton);
            LucasMod.instance.listener.setNeedTimer(true);
            LucasMod.instance.listener.setTimer(msg.timer);
            LucasMod.instance.listener.setFacName(msg.facName);
            LucasMod.instance.listener.setInvitationText(msg.displayInvitation);
            return null;
        }

    }
}
