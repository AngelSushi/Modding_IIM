package com.angelsushi.lucasmod.gui;

import com.angelsushi.lucasmod.LucasMod;
import com.angelsushi.lucasmod.packets.SCFactionInfo;
import com.angelsushi.lucasmod.packets.SCFactionList;
import com.angelsushi.lucasmod.packets.SCGetBank;
import com.angelsushi.lucasmod.packets.SCOnlinePlayers;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class GuiActionBank extends GuiScreen {

    private ResourceLocation background;

    private GuiTextField inputAmount;
    private boolean withdraw;

    public GuiActionBank(boolean withdraw) {
        this.withdraw = withdraw;
    }

    @Override
    public void initGui() {
        background = withdraw ? new ResourceLocation(LucasMod.MOD_ID,"textures/interface/menuretirer.png") : new ResourceLocation(LucasMod.MOD_ID,"textures/interface/menudeposer.png");

        inputAmount = new GuiTextField(this.fontRendererObj,0,0,170,10);
        inputAmount.setEnableBackgroundDrawing(false);
        inputAmount.setFocused(true);

        this.buttonList.add(new CustomButton(0,this.width / 2 - 60,this.height / 2 + 12,50,15,new ResourceLocation(LucasMod.MOD_ID,"textures/notifications/yes_btn.png")));
        this.buttonList.add(new CustomButton(1,this.width / 2 + 10,this.height / 2 + 12,50,15,new ResourceLocation(LucasMod.MOD_ID,"textures/notifications/no_btn.png")));

        super.initGui();
    }

    @Override
    public void actionPerformed(GuiButton btn) {
        if(btn.id == 0) {
            int amount = -1;

            try {
                amount = Integer.valueOf(inputAmount.getText());
            }catch(Exception e) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("§aMerci d'entrer un numéro valide");
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }

            if(amount > 0 ) {
                if(withdraw)
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/bank withdraw " + amount);
                else
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/bank deposit " + amount);

                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
            else {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("§aMerci d'entrer un numéro strictement supérieur à 0");
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
        if(btn.id == 1)
            Minecraft.getMinecraft().thePlayer.closeScreen();

        super.actionPerformed(btn);
    }

    @Override
    public void drawScreen(int mouseX,int mouseY,float partialTicks) {

        GL11.glColor3f(1f,1f,1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(background);
        Gui.func_152125_a(this.width / 2 - 100,this.height / 2 - 30,0,0,393,93,200,60,393,93);

        super.drawScreen(mouseX,mouseY,partialTicks);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2 - 85,this.height / 2 - 3,1f);
        this.inputAmount.drawTextBox();
        GL11.glPopMatrix();
    }

    @Override
    protected void keyTyped(char typedChar, int keycode) {
        super.keyTyped(typedChar, keycode);
        this.inputAmount.textboxKeyTyped(typedChar,keycode);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.inputAmount.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
