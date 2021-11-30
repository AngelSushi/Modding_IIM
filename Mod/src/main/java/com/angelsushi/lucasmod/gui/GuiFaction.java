package com.angelsushi.lucasmod.gui;

import com.angelsushi.lucasmod.LucasMod;
import com.angelsushi.lucasmod.packets.*;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.jcraft.jogg.Page;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class GuiFaction extends GuiScreen {

    public static ResourceLocation background = new ResourceLocation(LucasMod.MOD_ID,"textures/interface/lettre.png");
    private ResourceLocation cursor = new ResourceLocation(LucasMod.MOD_ID,"textures/btns/cursor.png");

    private static ArrayList searchList;
    public static String currentFacName;

    private GuiButton ally,truce;
    private int page;

    private int elementPerPage,listPage,maxPage,onlineListPage,onlineMaxPage,allListPage,allMaxPage;
    private int cursorX,cursorY,onlineCursorX,onlineCursorY,allCursorX,allCursorY;


    private GuiTextField search;

    private int line;

    private ArrayList<String> opponentsList = new ArrayList<String>();
    private String currentOpponent;
    public static ArrayList<SCFactionList.FactionData> factionDataList;
    public static  ArrayList<SCOnlinePlayers.PlayerData> onlinePlayersData;
    public static  ArrayList<SCFactionInfo.FactionData> factionData;
    public static  SCGetBank.FactionBank factionBank;
    public static  SCGetAllies.FactionRelation relation;
    public static boolean isPlayerFac;
    public static  SCGetWars.WarData warData;
    public static  SCGetStats.FactionStats stats;


    public boolean openFilter,isFilterSet,displayAlly;

    private boolean simpleCursor,doubleCursor;


    public GuiFaction(int page) {
        this.page = page;
    }


    public GuiFaction() {
        this.page = 0;
    }
    @Override
    public void initGui() {

        cursorX = this.width / 2 + 64;
        cursorY = this.height / 2 - 60;

        allCursorX = this.width / 2 + 68;
        allCursorY = this.height / 2 - 18;

        onlineCursorX = this.width / 2 - 4;
        onlineCursorY = this.height / 2 - 18;

        search = new GuiTextField(this.fontRendererObj, 0, 0, 165, 5);
        search.setEnableBackgroundDrawing(false);
        search.setVisible(false);

       // Mouse.setCursorPosition( (int)LucasMod.lastMouseX,(int) LucasMod.lastMouseY);
       // System.out.println("lastX: " + LucasMod.lastMouseX + " lastY: " +  LucasMod.lastMouseY);

        switch (page) {
            case 0: // MENU PRINCIPAL
                background = new ResourceLocation(LucasMod.MOD_ID,"textures/interface/lettre.png");
                buttonList.add(new CustomButton(0, width / 2 - 65, height / 2 - 85, 117, 35, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/create_country.png")));
                buttonList.add(new CustomButton(1, width / 2 - 65, height / 2 - 40, 117, 35, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/country_list.png")));
                buttonList.add(new CustomButton(2, width / 2 - 65, height / 2 + 5, 117, 35, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/players_online.png")));
                buttonList.add(new CustomButton(3, width / 2 - 65, height / 2 + 50, 117, 35, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/last_invite.png")));
                break;
            case 1: // LISTE DES PAYS DEJA EXISTANTS
                //search.setEnableGuiFaction.backgroundDrawing(false);
                search.setVisible(true);
                search.setFocused(true);
                search.setCanLoseFocus(false);
                simpleCursor = true;
                elementPerPage = 17;
                maxPage = factionDataList.size() / elementPerPage;

                if(factionDataList.size() % elementPerPage != 0)
                    maxPage++;

                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/country_list.png");

                if (factionDataList.size() > 17) {
                    for (int i = 0; i < 17; i++)
                        buttonList.add(new CustomButton(i, width / 2 - 70, height / 2 - 55 + 8 * i, 125, 4, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/transparent.png")));
                } else {
                    for (int i = 0; i < factionDataList.size(); i++)
                        buttonList.add(new CustomButton(i + elementPerPage*listPage, width / 2 - 70, height / 2 - 55 + 8 * i, 125, 4, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/transparent.png")));
                }

                break;

            case 2: // LISTE DES PAYS DISPONIBLE
                simpleCursor = true;
                elementPerPage = 17;
                maxPage = factionDataList.size() / elementPerPage;

                if(factionDataList.size() % elementPerPage != 0)
                    maxPage++;

                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/country_create.png");

                if (factionDataList.size() > 17) {
                    for (int i = 0; i < 17; i++)
                        buttonList.add(new CustomButton(i, width / 2 - 70, height / 2 - 55 + 8 * i, 125, 4, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/transparent.png")));
                } else {
                    for (int i = 0; i < factionDataList.size(); i++)
                        buttonList.add(new CustomButton(i + elementPerPage*listPage, width / 2 - 70, height / 2 - 55 + 8 * i, 125, 4, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/transparent.png")));
                }


                break;

            case 3: // players_online
                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/player_list.png");
                break;

            case 5: // /f f
                doubleCursor = false;
                elementPerPage = 12;

                allMaxPage = factionData.get(0).allPlayers.size() / elementPerPage;
                if(factionData.get(0).allPlayers.size() % elementPerPage != 0) // Si la division a un reste on rajoute une page
                    allMaxPage++;

                onlineMaxPage = factionData.get(0).playersOnline.size() / elementPerPage;
                if(factionData.get(0).playersOnline.size() / elementPerPage != 0)
                    onlineMaxPage++;

                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/country_info.png");
                initFButton();

                if (isPlayerFac)
                    buttonList.add(new CustomButton(5, width / 2 + 80, height / 2 + 40, 65, 20, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/quit.png")));

                break;
            case 6: // Relation
                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/menurelations.png");
                initFButton();

                // FILTER
                buttonList.add(new CustomButton(5, width / 2 - 35, height / 2 - 75, 70, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/boutonrelation.png")));

                ally = new CustomButton(6, width / 2 - 35, height / 2 - 65, 70, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/boutonrelationally.png"));
                truce = new CustomButton(7, width / 2 - 35, height / 2 - 55, 70, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/boutonrelationtruce.png"));

                ally.visible = false;
                truce.visible = false;

                buttonList.add(ally);
                buttonList.add(truce);

                if (!isPlayerFac) {
                    buttonList.add(new CustomButton(8, width / 2 - 75, height / 2 + 88, 70, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/demandeally.png")));
                    buttonList.add(new CustomButton(9, width / 2 + 5, height / 2 + 88, 70, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/demandetruce.png")));
                }
                break;
            case 7: // Bank
                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/bank.png");
                initFButton();

                buttonList.add(new CustomButton(5, width / 2 - 70, height / 2 - 15, 65, 12, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/deposer.png")));
                buttonList.add(new CustomButton(6, width / 2 + 4, height / 2 - 15, 65, 12, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/retirer.png")));
                buttonList.add(new CustomButton(7, width / 2 - 60, height / 2 - 65, 120, 15, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/coffrepays.png")));
                break;

            case 8:
                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/menurguerres.png");

                initFButton();

                buttonList.add(new CustomButton(5, width / 2 + 55, height / 2 - 63, 10, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/flechegauche.png")));
                buttonList.add(new CustomButton(6, width / 2 + 55, height / 2 - 53, 10, 10, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/flechedroite.png")));

                if (!isPlayerFac)
                    buttonList.add(new CustomButton(7, width / 2 - 60, height / 2 + 82, 120, 15, new ResourceLocation(LucasMod.MOD_ID, "textures/btns/declarerguerre.png")));

                for (SCGetWars.FactionWar war : warData.wars) {
                    if (war.attackerName.equals(currentFacName))
                        opponentsList.add(war.defenderName);
                    if (war.defenderName.equals(currentFacName))
                        opponentsList.add(war.attackerName);

                    currentOpponent = opponentsList.get(0);
                }

                break;

            case 9:
                GuiFaction.background = new ResourceLocation(LucasMod.MOD_ID, "textures/interface/menustats.png");

                initFButton();
                break;
        }

        super.initGui();
    }



    @Override
    public void actionPerformed(GuiButton btn) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        if((btn.id == 0 || btn.id == 1) && page == 0) {
            out.writeBoolean(btn.id == 1); // Si true liste des pays déja existant ; si false liste des pays dispo
            C17PacketCustomPayload packet = new C17PacketCustomPayload("getFList", out.toByteArray());
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
        }

        if(btn.id == 2 && page == 0) { // liste des joueurs en ligne
            out.writeUTF("");
            C17PacketCustomPayload packet = new C17PacketCustomPayload("getOPlayers", out.toByteArray());
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
        }

        if(page == 1) {
            int indexName = 0;
            if(factionDataList.size() <= elementPerPage)
                indexName = btn.id;
            else
                indexName = btn.id + elementPerPage * listPage;

            String facName = factionDataList.get(indexName).getFactionName();
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/f f " + facName);
            Minecraft.getMinecraft().thePlayer.closeScreen();

        }

        if(page == 2) {
            int indexName = 0;
            if(factionDataList.size() <= elementPerPage)
                indexName = btn.id;
            else
                indexName = btn.id + elementPerPage * listPage;

            if(indexName < factionDataList.size()) {
                String facName = factionDataList.get(indexName).getFactionName();
                out.writeUTF(facName);
                C17PacketCustomPayload packet = new C17PacketCustomPayload("createFac", out.toByteArray());
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
            }

            Minecraft.getMinecraft().thePlayer.closeScreen();
        }

        if(page == 5 || page == 6 || page == 7 || page == 8 || page == 9) { // menu /f f

            if(btn.id == 0) {
                Minecraft.getMinecraft().thePlayer.closeScreen();
                Minecraft.getMinecraft().displayGuiScreen(new GuiFaction(5));
            }

            if(btn.id == 1) {
                out.writeUTF("");
                out.writeUTF(currentFacName);
                C17PacketCustomPayload packet = new C17PacketCustomPayload("getFExchanges", out.toByteArray());
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
            }

            if(btn.id == 2) {
                out.writeUTF(currentFacName);
                C17PacketCustomPayload packet = new C17PacketCustomPayload("getAllies", out.toByteArray()); // page 6 
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
            }

            if(btn.id == 3) { // Click pour entrer dans le menu de guerre
                out.writeUTF(currentFacName);
                C17PacketCustomPayload packet = new C17PacketCustomPayload("getWars", out.toByteArray()); // page 6
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);

            }

            if(btn.id == 4) {
                out.writeUTF(currentFacName);
                C17PacketCustomPayload packet = new C17PacketCustomPayload("getStats", out.toByteArray()); // page 6
                Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
            }

            if(page == 5 && btn.id == 5) {
                Minecraft.getMinecraft().thePlayer.sendChatMessage("/f leave");
            }

            if(page == 6 && (btn.id == 5 || btn.id == 6 || btn.id == 7)) {
                if(btn.id == 6 || btn.id == 7) {
                    CustomButton filter = (CustomButton) this.buttonList.get(5);
                    filter.icon = ((CustomButton)btn).icon;
                    isFilterSet = true;
                    displayAlly = btn.id == 6;
                }
                ally.visible = !openFilter;
                truce.visible = !openFilter;

                openFilter = !openFilter;
            }

            if(page == 6) {
                if(btn.id == 8)
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/f ally " + currentFacName);
                if(btn.id == 9)
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/f truce " + currentFacName);
            }

            if(page == 7) {
                System.out.println("btn: " + btn.id + " " + isPlayerFac);
                if((btn.id == 5 || btn.id == 6) && isPlayerFac) {
                    System.out.println("condition");
                    Minecraft.getMinecraft().displayGuiScreen(new GuiActionBank(btn.id == 6));
                }
                if(btn.id == 7 && isPlayerFac)
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/chest " + currentFacName);
            }

            if(page == 8) {
                if(btn.id == 7) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/f ennemy " + currentFacName);
                    Minecraft.getMinecraft().thePlayer.closeScreen();
                }

                if(btn.id == 5) {// on monte -1 dans la liste
                    int actualOpponent = opponentsList.indexOf(currentOpponent);

                    if(actualOpponent > 0)
                        currentOpponent = opponentsList.get(actualOpponent - 1);
                }
                if(btn.id == 6) { // on descends +1 dans la lsite
                    int actualOpponent = opponentsList.indexOf(currentOpponent);

                    if(actualOpponent < opponentsList.size() - 1)
                        currentOpponent = opponentsList.get(actualOpponent + 1);

                }
            }
        }



        super.actionPerformed(btn);
    }

    @Override
    public void drawScreen(int mouseX,int mouseY,float partialTicks) {
        GL11.glColor3f(1f,1f,1f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(background);
        Gui.func_152125_a(this.width / 2 - 100,this.height / 2 - 112,0,0,822,757,250,225,822,757);


        super.drawScreen(mouseX,mouseY,partialTicks);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.width / 2 - 63,this.height / 2 + 87,1);
        GL11.glScalef(0.7f,0.7f,1);
        this.search.drawTextBox();
        GL11.glPopMatrix();

        if(page == 0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2 + 27f, height / 2 + 64, 1f);
            GL11.glScalef(0.8f, 0.8f, 1.0F);
            Minecraft.getMinecraft().fontRenderer.drawString("patate", 0, 0, Color.WHITE.getRGB());

            GL11.glPopMatrix();
        }
        if(page == 1 || page == 2) {
            if (search.getText().equals("")) {
                maxPage = factionDataList.size() / elementPerPage;
                if(factionDataList.size() % elementPerPage != 0)
                    maxPage++;

                int line = 0;
                for (int i = 0; i < factionDataList.size(); i++) {

                    GL11.glColor4f(1f, 1f, 1f, 0.9f);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(cursor);
                    Gui.func_152125_a(cursorX, cursorY, 0, 0, 66, 84, 5, 15, 66, 84);

                    if (i >= elementPerPage * listPage && i < (elementPerPage * (listPage + 1))) {
                        SCFactionList.FactionData fData = factionDataList.get(i);
                        if(!fData.getFactionName().contains("Wilderness")) {
                            GL11.glPushMatrix();
                          //  int iCoord = line - elementPerPage * listPage;
                            GL11.glTranslatef(width / 2 - 70f, height / 2 - 57 + 8 * line, 1f);
                            GL11.glScalef(0.7f, 0.7f, 1.0F);
                            Minecraft.getMinecraft().fontRenderer.drawString(fData.getFactionName(), 0, 0, Color.WHITE.getRGB());
                            if (page == 1) {

                                if(fData.getAge().indexOf(" ") > 0)
                                    Minecraft.getMinecraft().fontRenderer.drawString("" + fData.getAge().substring(0,fData.getAge().indexOf(" ") - 1), 80, 0, Color.WHITE.getRGB());
                                Minecraft.getMinecraft().fontRenderer.drawString("" + fData.getSizePlayers(), 150, 0, Color.WHITE.getRGB());
                            }
                            line++;
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
            else {
                int line = 0;
                maxPage = searchList.size() / elementPerPage;
                if(searchList.size() % elementPerPage != 0)
                    maxPage++;

                for (int i = 0; i < searchList.size(); i++) {

                    GL11.glColor4f(1f, 1f, 1f, 0.9f);
                    Minecraft.getMinecraft().getTextureManager().bindTexture(cursor);
                    Gui.func_152125_a(cursorX, cursorY, 0, 0, 66, 84, 5, 15, 66, 84);

                    if (i >= elementPerPage * listPage && i < (elementPerPage * (listPage + 1))) {
                        SCFactionList.FactionData fData = (SCFactionList.FactionData) searchList.get(i);
                        if(!fData.getFactionName().contains("Wilderness")) {
                            GL11.glPushMatrix();

                            GL11.glTranslatef(width / 2 - 70f, height / 2 - 57 + 8 * line, 1f);
                            GL11.glScalef(0.7f, 0.7f, 1.0F);
                            Minecraft.getMinecraft().fontRenderer.drawString(fData.getFactionName(), 0, 0, Color.WHITE.getRGB());
                            if (page == 1) {

                                if(fData.getAge().indexOf(" ") > 0)
                                    Minecraft.getMinecraft().fontRenderer.drawString("" + fData.getAge().substring(0,fData.getAge().indexOf(" ") - 1), 80, 0, Color.WHITE.getRGB());
                                Minecraft.getMinecraft().fontRenderer.drawString("" + fData.getSizePlayers(), 150, 0, Color.WHITE.getRGB());
                            }
                            line++;
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
        }
        if (page == 3) {
            for (int i = 0; i < onlinePlayersData.size(); i++) {
                GL11.glPushMatrix();
                GL11.glTranslatef(width / 2 - 70, height / 2 - 57 + 8 * i, 1.0f);
                GL11.glScalef(0.7f, 0.7f, 1.0F);

                if(onlinePlayersData.get(i).name.length() >= 10)
                    Minecraft.getMinecraft().fontRenderer.drawString(onlinePlayersData.get(i).name.substring(0,10), 0, 0, Color.WHITE.getRGB());
                else
                    Minecraft.getMinecraft().fontRenderer.drawString(onlinePlayersData.get(i).name, 0, 0, Color.WHITE.getRGB());
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glTranslatef(width / 2 - 25, height / 2 - 57 + 8 * i, 1.0f);
                GL11.glScalef(0.7f, 0.7f, 1.0F);
                Minecraft.getMinecraft().fontRenderer.drawString(onlinePlayersData.get(i).factionName, 0, 0, Color.WHITE.getRGB());
                GL11.glPopMatrix();


                GL11.glPushMatrix();
                GL11.glTranslatef(width / 2 + 28, height / 2 - 57 + 8 * i, 1.0f);
                GL11.glScalef(0.7f, 0.7f, 1.0F);
                Minecraft.getMinecraft().fontRenderer.drawString("" + (int) onlinePlayersData.get(i).power, 0, 0, Color.WHITE.getRGB());
                GL11.glPopMatrix();
            }
        }
        if (page == 5) {
            GL11.glColor4f(1f, 1f, 1f, 0.9f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(cursor);
            Gui.func_152125_a(allCursorX, allCursorY, 0, 0, 66, 84, 4, 10, 66, 84);


            GL11.glColor4f(1f, 1f, 1f, 0.9f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(cursor);
            Gui.func_152125_a(onlineCursorX, onlineCursorY, 0, 0, 66, 84, 4, 10, 66, 84);

            for (int i = 0; i < factionData.size(); i++) {
                GL11.glPushMatrix();
                GL11.glTranslatef(width / 2 - 17, height / 2 - 70, 0);
                GL11.glScalef(0.9f, 0.9f, 1.0f);
                Minecraft.getMinecraft().fontRenderer.drawString(currentFacName, 10 - currentFacName.length(), -18, new Color(0,192,0).getRGB());
                GL11.glScalef(1 / 0.9f, 1 / 0.9f, 1f);

                GL11.glScalef(0.7f, 0.7f, 1.0F);

                Minecraft.getMinecraft().fontRenderer.drawString("" + factionData.get(i).age, -21, 49, Color.WHITE.getRGB());
                Minecraft.getMinecraft().fontRenderer.drawString("" + factionData.get(i).players, -60, 27, Color.WHITE.getRGB());
                Minecraft.getMinecraft().fontRenderer.drawString("" + factionData.get(i).landCount, 15, 27, Color.WHITE.getRGB());
                Minecraft.getMinecraft().fontRenderer.drawString("" + (int) factionData.get(i).power + "/" +(int) factionData.get(i).maxPower, 65, 27, Color.WHITE.getRGB());

                for (int j = 0; j < factionData.get(i).playersOnline.size(); j++) {
                    if(factionData.get(i).playersOnline.size() > elementPerPage) {
                        if (j >= elementPerPage * onlineListPage && j < (elementPerPage * (onlineListPage + 1))) {
                            int jCoord = j - elementPerPage * onlineListPage;
                            Minecraft.getMinecraft().fontRenderer.drawString(factionData.get(i).playersOnline.get(j), -70, 77 + 12 * jCoord, Color.WHITE.getRGB());
                        }
                    }
                    else
                        Minecraft.getMinecraft().fontRenderer.drawString(factionData.get(i).playersOnline.get(j), -70, 77 + 12 * j, Color.WHITE.getRGB());

                }


                for (int j = 0; j < factionData.get(i).allPlayers.size(); j++) {
                    if(factionData.get(i).allPlayers.size() > elementPerPage) {
                        if (j >= elementPerPage * allListPage && j < (elementPerPage * (allListPage + 1))) {
                            int jCoord = j - elementPerPage * allListPage;
                            Minecraft.getMinecraft().fontRenderer.drawString(factionData.get(i).allPlayers.get(j), 33, 77 + 12 * jCoord, Color.WHITE.getRGB());
                        }
                    }
                    else
                        Minecraft.getMinecraft().fontRenderer.drawString(factionData.get(i).allPlayers.get(j), 33, 77 + 12 * j, Color.WHITE.getRGB());

                }

                Minecraft.getMinecraft().fontRenderer.drawString(factionData.get(i).leaderName, 3, 232, Color.RED.getRGB());
                GL11.glPopMatrix();
            }
        }
        if (page == 6) {
            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2 - 17, height / 2 - 70, 0);
            GL11.glScalef(0.8f, 0.8f, 1.0F);
            line = 0;

            for (int i = 0; i < relation.getAlliesFac().size(); i++) {
                if (!isFilterSet) {
                    Minecraft.getMinecraft().fontRenderer.drawString(relation.getAlliesFac().get(i), -35, 35 + 12 * line, new Color(102,0,255).getRGB());
                    line++;
                } else {
                    if (displayAlly) {
                        Minecraft.getMinecraft().fontRenderer.drawString(relation.getAlliesFac().get(i), -35, 35 + 12 * line, new Color(102,0,255).getRGB());
                        line++;
                    }
                }
            }

            for (int i = 0; i < relation.getTrucesFac().size(); i++) {
                if (!isFilterSet) {
                    Minecraft.getMinecraft().fontRenderer.drawString(relation.getTrucesFac().get(i), -35, 35 + 12 * line, Color.MAGENTA.getRGB());
                    line++;
                } else {
                    if (!displayAlly) {
                        Minecraft.getMinecraft().fontRenderer.drawString(relation.getTrucesFac().get(i), -35, 35 + 12 * line, Color.MAGENTA.getRGB());
                        line++;
                    }
                }
            }

            GL11.glPopMatrix();
        }
        if (page == 7) {
            GL11.glColor3f(1f, 1f, 1f);
            Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(LucasMod.MOD_ID, "textures/btns/comptepays.png"));
            Gui.func_152125_a(width / 2 - 60, height / 2 - 42, 0, 0, 478, 51, 120, 15, 478, 51);

            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2 + 13, height / 2 - 38, 1f);
            GL11.glScalef(0.9f, 0.9f, 1.0F);
            Minecraft.getMinecraft().fontRenderer.drawString("" + factionBank.getAccount(), 0, 0, Color.WHITE.getRGB());
            GL11.glPopMatrix();

            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2 - 17, height / 2 - 70, 0);
            GL11.glScalef(0.5f, 0.5f, 1.0F);
            line = 0;

            for (int i = 0; i < factionBank.getBankExchanges().size(); i++) {
                if (factionBank.getBankExchanges().get(i).amount > 0) {
                    Minecraft.getMinecraft().fontRenderer.drawString(factionBank.getBankExchanges().get(line).sender.substring(0, 5) + "...", -98, 145 + 12 * line,  new Color(0,192,0).getRGB());
                    Minecraft.getMinecraft().fontRenderer.drawString("" + factionBank.getBankExchanges().get(line).amount, -55, 145 + 12 * line,  new Color(0,192,0).getRGB());
                    Minecraft.getMinecraft().fontRenderer.drawString(CalculateTime(factionBank.getBankExchanges().get(i).date,LocalDateTime.now()), -10, 145 + 12 * line,  new Color(0,192,0).getRGB());
                    line++;
                }
            }

            line = 0;
            for (int i = 0; i < factionBank.getBankExchanges().size(); i++) {
                if (factionBank.getBankExchanges().get(i).amount < 0) {
                    Minecraft.getMinecraft().fontRenderer.drawString(factionBank.getBankExchanges().get(i).sender.substring(0, 5) + "...", 57, 145 + 12 * line, Color.RED.getRGB());
                    Minecraft.getMinecraft().fontRenderer.drawString("" + factionBank.getBankExchanges().get(i).amount, 107, 145 + 12 * line, Color.RED.getRGB());
                    Minecraft.getMinecraft().fontRenderer.drawString(CalculateTime(factionBank.getBankExchanges().get(i).date,LocalDateTime.now()), 150, 145 + 12 * line, Color.RED.getRGB());
                    line++;
                }
            }
            GL11.glPopMatrix();
        }

        if (page == 8) {
            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2 - 45, height / 2 - 41, 0);

            GL11.glScalef(0.7f, 0.7f, 1.0F);
            if (currentOpponent != null)
                Minecraft.getMinecraft().fontRenderer.drawString(currentOpponent, 60 - currentOpponent.length(), -20, Color.WHITE.getRGB());
            GL11.glScalef(1 / 0.7f, 1 / 0.7f, 1.0F);

            GL11.glScalef(0.6f, 0.6f, 1.0F);

            int winAgainstOpponent = 0;
            int defeatAgainstOpponent = 0;
            int equalityAgainstOpponent = 0;

            for (int i = 0; i < warData.wars.size(); i++) {
                SCGetWars.FactionWar war = warData.wars.get(i);
                if ((currentOpponent != null && war.attackerName.equals(currentFacName) && war.defenderName.equals(currentOpponent)) || (war.attackerName.equals(currentOpponent) && war.defenderName.equals(currentFacName))) { // C une guerre entre les deux factions
                    if (war.finish) {
                        if (war.winner.equals(currentFacName))
                            winAgainstOpponent++;
                        else if (war.winner.equals(currentOpponent))
                            defeatAgainstOpponent++;
                        else
                            equalityAgainstOpponent++;
                    }
                }
            }

            Minecraft.getMinecraft().fontRenderer.drawString("" + winAgainstOpponent, 0, 0,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + defeatAgainstOpponent, 67, 0, Color.RED.getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + (winAgainstOpponent + defeatAgainstOpponent + equalityAgainstOpponent), 168, 0, Color.WHITE.getRGB()); // A MODIFIER AVEC LES ASSAULTS OU YA EGALITE

            int line = 0;
            for (int i = warData.wars.size() - 1; i > warData.wars.size() - 9; i--) { // Faire l'historique des factions entre deux factions uniquement
                if (i >= 0 && (warData.wars.get(i).defenderName.equals(currentOpponent) || warData.wars.get(i).attackerName.equals(currentOpponent))) {

                    int color = warData.wars.get(i).winner.equals("") ? Color.WHITE.getRGB() : warData.wars.get(i).winner == currentFacName ?  new Color(0,192,0).getRGB() : Color.RED.getRGB();
                    Minecraft.getMinecraft().fontRenderer.drawString(warData.wars.get(i).attackerName == currentFacName ? warData.wars.get(i).attackerName : warData.wars.get(i).defenderName, 25, 34 + 13 * line, color);
                    line++;
                }
            }
            GL11.glPopMatrix();
        }
        if (page == 9) {
            GL11.glPushMatrix();
            GL11.glTranslatef(width / 2 - 30, height / 2 - 70, 0);
            GL11.glScalef(0.75f, 0.75f, 1.0F);
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.ally, 30, 12,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.kills, 65, 31,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.deaths, 68, 43,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.kd, 18, 55,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.winCount, 64, 78,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.defeatCount, 64, 90,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.warKD, 64, 102,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.landCount, 44, 125,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + (int)stats.bank, 28, 149,  new Color(0,192,0).getRGB());
            Minecraft.getMinecraft().fontRenderer.drawString("" + stats.age, 9, 173,  new Color(0,192,0).getRGB());
            GL11.glPopMatrix();
        }


        LucasMod.lastMouseX = mouseX;
        LucasMod.lastMouseY = mouseY;
        LucasMod.lastPage = page;

    }

    @Override
    protected void keyTyped(char typedChar, int keycode) {
        super.keyTyped(typedChar, keycode);
        this.search.textboxKeyTyped(typedChar,keycode);

        if(search.isFocused()) {
            searchList = new ArrayList();

            for(SCFactionList.FactionData fData : factionDataList) {
                if(fData.getFactionName().toLowerCase().contains(search.getText().toLowerCase()))
                    searchList.add(fData);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.search.mouseClicked(mouseX,mouseY,mouseButton);

        if(simpleCursor) {
            if(mouseX >= this.width / 2 + 64 && mouseX <= this.width / 2 + 64 + 6) {
                if(mouseY >= this.height / 2 - 60 && mouseY <= this.height / 2 + 63 + 15) {
                    cursorY = mouseY;

                    if(mouseY >= this.height / 2 + 63 && mouseY <= this.height / 2 + 63 + 15)
                        cursorY = this.height / 2 + 63;

                    if(maxPage == 0) {

                        maxPage = factionDataList.size() / elementPerPage;

                        if(factionDataList.size() % elementPerPage != 0)
                            maxPage++;
                    }

                    if(maxPage != 0) {
                        int yPage = 138 / (maxPage);

                        int convertY = mouseY - (this.height / 2 - 18); // On converti la mouseY sur l'intervalle [0,97];

                        for(int i = 0;i<maxPage;i++) {
                            if(convertY >= yPage * i && convertY <= yPage * (i + 1)) {
                                listPage = i;
                                break;
                            }
                            if(convertY <= yPage * i && convertY >= yPage * (i - 1)) {
                                listPage = i;
                                break;
                            }
                        }
                    }

                }
            }
        }
        if(doubleCursor) {
            if(mouseX >= this.width / 2 + 68 && mouseX <= this.width / 2 + 68 + 6) {
                if(mouseY >= this.height / 2 - 18 && mouseY <= this.height / 2 + 79) {
                    allCursorY = mouseY;

                    if(allMaxPage == 0) {
                        allMaxPage = factionData.get(0).allPlayers.size() / elementPerPage;
                        if(factionData.get(0).allPlayers.size() % elementPerPage != 0) // Si la division a un reste on rajoute une page
                            allMaxPage++;
                    }
                    if(allMaxPage != 0) {
                        int yPage = 97 / allMaxPage; // 97 est la distance de la barre a scroll

                        int convertY = mouseY - (this.height / 2 - 18); // On converti la mouseY sur l'intervalle [0,97];

                        for(int i = 0;i<allMaxPage;i++) {
                            if(convertY >= yPage * i && convertY <= yPage * (i + 1)) {
                                allListPage = i;
                                break;
                            }
                            if(convertY <= yPage * i && convertY >= yPage * (i - 1)) {
                                allListPage = i;
                                break;
                            }
                        }
                    }
                }
                if(mouseX >= this.width / 2 - 4 && mouseX <= this.width / 2 - 4 + 6) {
                    if(mouseY >= this.height / 2 - 18 && mouseY <= this.height / 2 + 79) {
                        onlineCursorY = mouseY;

                        if(onlineMaxPage == 0) {
                            onlineMaxPage = factionData.get(0).playersOnline.size() / elementPerPage;
                            if(factionData.get(0).playersOnline.size() / elementPerPage != 0)
                                onlineMaxPage++;
                        }
                        if(onlineMaxPage != 0) {
                            int yPage = 97 / onlineMaxPage;
                            int convertY = mouseY - (this.height / 2 - 18); // On converti la mouseY sur l'intervalle [0,97];

                            for(int i = 0;i<onlineMaxPage;i++) {
                                if(convertY >= yPage * i && convertY <= yPage * (i + 1)) {
                                    onlineListPage = i;
                                    break;
                                }
                                if(convertY <= yPage * i && convertY >= yPage * (i - 1)) {
                                    onlineListPage = i;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMpouseButton, long timeSinceLastTick) {
        if(simpleCursor) {
            if(mouseX >= this.width / 2 + 64 && mouseX <= this.width / 2 + 64 + 6) {
                if(mouseY >= this.height / 2 - 60 && mouseY <= this.height / 2 + 63) {
                    cursorY = mouseY;

                    if(maxPage == 0) {

                        maxPage = factionDataList.size() / elementPerPage;

                        if(factionDataList.size() % elementPerPage != 0)
                            maxPage++;
                    }

                    if(maxPage != 0) {

                        int yPage = 138 / (maxPage);

                        int convertY = mouseY - (this.height / 2 - 18); // On converti la mouseY sur l'intervalle [0,97];

                        for (int i = 0; i < maxPage; i++) {
                            if (convertY >= yPage * i && convertY <= yPage * (i + 1)) {
                                listPage = i;
                                break;
                            }
                            if (convertY <= yPage * i && convertY >= yPage * (i - 1)) {
                                listPage = i;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if(doubleCursor) {
            if(mouseX >= this.width / 2 + 68 && mouseX <= this.width / 2 + 68 + 6) {
                if(mouseY >= this.height / 2 - 18 && mouseY <= this.height / 2 + 79) {

                    if(allMaxPage == 0) {
                        allMaxPage = factionData.get(0).allPlayers.size() / elementPerPage;
                        if(factionData.get(0).allPlayers.size() % elementPerPage != 0) // Si la division a un reste on rajoute une page
                            allMaxPage++;
                    }
                    if(allMaxPage != 0) {
                        int yPage = 97 / allMaxPage; // 97 est la distance de la barre a scroll

                        int convertY = mouseY - (this.height / 2 - 18); // On converti la mouseY sur l'intervalle [0,97];

                        for(int i = 0;i<allMaxPage;i++) {
                            if(convertY >= yPage * i && convertY <= yPage * (i + 1)) {
                                allListPage = i;
                                break;
                            }
                            if(convertY <= yPage * i && convertY >= yPage * (i - 1)) {
                                allListPage = i;
                                break;
                            }
                        }
                    }
                }
            }
            if(mouseX >= this.width / 2 - 4 && mouseX <= this.width / 2 - 4 + 6) {
                if(mouseY >= this.height / 2 - 18 && mouseY <= this.height / 2 + 79) {
                    onlineCursorY = mouseY;

                    if(onlineMaxPage == 0) {
                        onlineMaxPage = factionData.get(0).playersOnline.size() / elementPerPage;
                        if(factionData.get(0).playersOnline.size() / elementPerPage != 0)
                            onlineMaxPage++;
                    }
                    if(onlineMaxPage != 0) {
                        int yPage = 97 / onlineMaxPage;
                        int convertY = mouseY - (this.height / 2 - 18); // On converti la mouseY sur l'intervalle [0,97];

                        for(int i = 0;i<onlineMaxPage;i++) {
                            if(convertY >= yPage * i && convertY <= yPage * (i + 1)) {
                                onlineListPage = i;
                                break;
                            }
                            if(convertY <= yPage * i && convertY >= yPage * (i - 1)) {
                                onlineListPage = i;
                                break;
                            }
                        }
                    }
                }
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMpouseButton, timeSinceLastTick);
    }

    private void initFButton() {

        buttonList.add(new CustomButton(0,this.width / 2 - 98,this.height / 2 - 47,20,15,new ResourceLocation(LucasMod.MOD_ID,"textures/btns/list.png")));
        buttonList.add(new CustomButton(1,this.width / 2 - 98,this.height / 2 - 25,20,15,new ResourceLocation(LucasMod.MOD_ID,"textures/btns/bank.png")));
        buttonList.add(new CustomButton(2,this.width / 2 - 98,this.height / 2 - 3,20,15,new ResourceLocation(LucasMod.MOD_ID,"textures/btns/relations.png")));
        buttonList.add(new CustomButton(3,this.width / 2 - 98,this.height / 2 + 13,20,15,new ResourceLocation(LucasMod.MOD_ID,"textures/btns/war.png")));
        buttonList.add(new CustomButton(4,this.width / 2 - 98,this.height / 2 + 32,20,15,new ResourceLocation(LucasMod.MOD_ID,"textures/btns/stats.png")));
    }

        private String CalculateTime(LocalDateTime exchangeDate,LocalDateTime now) {
            double days = ChronoUnit.DAYS.between(exchangeDate,now);
            double hours  = ChronoUnit.HOURS.between(exchangeDate,now);
            double minutes = ChronoUnit.MINUTES.between(exchangeDate,now);

            String date = "";
           if(days > 0) {
               date += days + "j";
           }
           else if(hours > 0) {
               date += hours + "h";
           }
           else if(minutes > 0)
               date +=  minutes + "m";

           return date;
        }
}
