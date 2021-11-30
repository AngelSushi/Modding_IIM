package com.angelsushi.lucasmod.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {

    public static KeyBinding keyTest;
    @Override
    public void register() {
        keyTest = new KeyBinding("Gui", Keyboard.KEY_I,"");
        ClientRegistry.registerKeyBinding(keyTest);
    }
}
