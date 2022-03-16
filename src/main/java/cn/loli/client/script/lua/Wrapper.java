package cn.loli.client.script.lua;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;

public class Wrapper {

    static final Minecraft mc = Minecraft.getMinecraft();;
    static EntityPlayerSP player = null;
    static WorldClient world = null;

    public Wrapper(){
        System.out.println("Wrapper init");
        player = mc.thePlayer;
        world = mc.theWorld;
    }
}
