package com.angelsushi.lucasmod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomButton extends GuiButton {

    public ResourceLocation icon;

    private int width,height;

    public CustomButton(int buttonId, int x, int y,int width,int height,ResourceLocation icon) {

        super(buttonId, x, y, width, height, "");
        this.icon = icon;
        this.width = width;
        this.height = height;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY)
    {
        if(this.visible) {

            mc.getTextureManager().bindTexture(icon);

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            Gui.func_152125_a(this.xPosition, this.yPosition, 0, 0, 128, 128, this.width, this.height, 128, 128);
        }
    }
}
