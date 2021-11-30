package com.angelsushi.lucasmod;

import com.angelsushi.lucasmod.gui.CustomButton;
import com.angelsushi.lucasmod.gui.GuiFaction;
import com.angelsushi.lucasmod.proxy.ClientProxy;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class EventListener {

    private final ResourceLocation inviteNotif = new ResourceLocation(LucasMod.MOD_ID,"textures/notifications/country.png");
    private final ResourceLocation yesBtn = new ResourceLocation(LucasMod.MOD_ID,"textures/notifications/yes_btn.png");
    private final ResourceLocation noBtn = new ResourceLocation(LucasMod.MOD_ID,"textures/notifications/no_btn.png");
    private final ResourceLocation assault = new ResourceLocation(LucasMod.MOD_ID,"textures/notifications/assaut.png");

    @Getter @Setter  public String assaultName;
    @Getter @Setter  public String defendName;
    @Getter @Setter  public String invitationText;
    @Getter @Setter  public String facName;

    @Getter @Setter public int scoreAssault,scoreDefend;
    @Getter @Setter public float timer;

    @Getter @Setter public boolean inAssault;
    @Getter @Setter public boolean displayInvite,needButton,needTimer;

    @Getter public int tick;

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void keyInput(InputEvent.KeyInputEvent e) {
        if(ClientProxy.keyTest.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiFaction());
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void render(RenderGameOverlayEvent.Post e) {

        if(e.type == RenderGameOverlayEvent.ElementType.ALL) {
            if(isInAssault()) {
                GL11.glPushMatrix();

                GL11.glColor3f(1.0f,1.0f,1.0F);
                GL11.glTranslatef(e.resolution.getScaledWidth() / 2 - 109,0,1);
                Minecraft.getMinecraft().getTextureManager().bindTexture(assault);
                Gui.func_152125_a(0,0,0,0,542,49,217,19,542,49);

                GL11.glScalef(0.7f,0.7f,1f);
                Minecraft.getMinecraft().fontRenderer.drawString(assaultName + EnumChatFormatting.YELLOW +  " VS " + EnumChatFormatting.WHITE + defendName,155,2, Color.WHITE.getRGB());

                Minecraft.getMinecraft().fontRenderer.drawString("" + scoreAssault + " | " + scoreDefend,40,12, Color.WHITE.getRGB());
                int minutes = (int)timer / 60;
                int seconds = (int)timer % 60;
                Minecraft.getMinecraft().fontRenderer.drawString("" + minutes + "m " + seconds + "s",225,12, Color.WHITE.getRGB());

                GL11.glPopMatrix();
            }

            if(isDisplayInvite()) {
                displayInvitation(invitationText);
            }
        }
    }

    private void displayInvitation(String text) {
        GL11.glColor3f(1.0f,1.0f,1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(inviteNotif);
        Gui.func_152125_a(0,0,0,0,1000,464,150,69,1000,464);

        String[] texts = {"","",""};
        if(70 + Minecraft.getMinecraft().fontRenderer.getStringWidth(text) > 140) {
            int line = text.length() / 25;
            if(text.length() % 25 != 0)
                line++;

            for(int i = 0;i<line;i++) {
                if(25 + 25 * i <= text.length())
                    texts[i] = text.substring(25 * i,25 + 25 * i);
                else
                    texts[i] = text.substring(25 * i);
            }
        }
        else
            texts[0] = text;

        Minecraft.getMinecraft().fontRenderer.drawString(texts[0],70 - (Minecraft.getMinecraft().fontRenderer.getStringWidth(texts[0]) / 2),20, Color.WHITE.getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString(texts[1],70 - (Minecraft.getMinecraft().fontRenderer.getStringWidth(texts[1]) / 2),30, Color.WHITE.getRGB());
        Minecraft.getMinecraft().fontRenderer.drawString(texts[2],70 - (Minecraft.getMinecraft().fontRenderer.getStringWidth(texts[2]) / 2),40, Color.WHITE.getRGB());

        if(needButton) {
            GL11.glColor3f(1.0f,1.0f,1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(yesBtn);
            Gui.func_152125_a(10,50,0,0,469,131,50,14,469,131);

            GL11.glColor3f(1.0f,1.0f,1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(noBtn);
            Gui.func_152125_a(80,50,0,0,469,131,50,14,469,131);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        if(e.entity instanceof EntityPlayer) {
            if(LucasMod.instance.listener.isDisplayInvite())
                LucasMod.instance.listener.setDisplayInvite(false);
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.PlayerTickEvent event) {
        if(needTimer && displayInvite) {
            tick++;

            if(tick >= 40 * 5) {
                tick = 0;
                displayInvite = false;
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post e) {
        if(e.gui instanceof GuiChat && isDisplayInvite() && isNeedButton()) {
            e.buttonList.add(new CustomButton(0, 10, 50, 50, 14, new ResourceLocation(LucasMod.MOD_ID, "textures/notifications/yes_btn.png")));
            e.buttonList.add(new CustomButton(1, 80, 50, 50, 14, new ResourceLocation(LucasMod.MOD_ID, "textures/notifications/no_btn.png")));
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onActionGuiPerformed(GuiScreenEvent.ActionPerformedEvent.Pre e) {
        if(e.gui instanceof GuiChat) {
            if (e.button.id == 0) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/f join " + facName);
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
            else {
                displayInvite = false;
                needButton = false;
                needTimer = false;
                Minecraft.getMinecraft().thePlayer.closeScreen();
            }
        }
    }
}
