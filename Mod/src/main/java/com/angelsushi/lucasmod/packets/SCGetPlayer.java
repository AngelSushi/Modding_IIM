package com.angelsushi.lucasmod.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class SCGetPlayer implements IMessage {

    private UUID player;
    private boolean sender;

    public SCGetPlayer() {}

    public SCGetPlayer(UUID player,boolean sender) {
        this.player = player;
        this.sender = sender;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.sender = buf.readBoolean();
        this.player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.sender);
        ByteBufUtils.writeUTF8String(buf,this.player.toString());
    }

    public static class Handler implements IMessageHandler<SCGetPlayer,IMessage> {

        @Override
        public IMessage onMessage(SCGetPlayer message, MessageContext ctx) {

            /*if(message.player.toString().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getUniqueID().toString())) {
                if(message.sender) {

                }
                else {

                }
            }
*/
            return null;
        }
    }
}
