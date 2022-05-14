package cn.loli.client.module.modules.player;


import cn.loli.client.events.PacketEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.utils.render.RenderUtils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.ColorProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmorStand;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemMap;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MurderMystery extends Module {

    private static EntityPlayer murder;
    private final List<String> alartedPlayers = new ArrayList<>();
    private final ColorProperty espColor = new ColorProperty("ESP-Color", Color.BLUE);

    public MurderMystery() {
        super("Murder Mystery", "Find the Murder", ModuleCategory.PLAYER);
    }

    public static boolean isMurder(EntityPlayer player) {
        if (player == null || murder == null)
            return false;

        if (player.isDead || player.isInvisible())
            return false;

        return player.equals(murder);
    }

    @Override
    public void onDisable() {
        this.alartedPlayers.clear();
        
    }


    private final IEventListener<PacketEvent> onRespawn = event ->
    {
        if (event.getPacket() instanceof S01PacketJoinGame)
            this.alartedPlayers.clear();
    };

    private final IEventListener<RenderEvent> onRender = event ->
    {
        if (isMurder(murder))
            RenderUtils.renderBox(murder, espColor.getPropertyValue().getRGB());
    };

    private final IEventListener<UpdateEvent> onUpdate = event ->
    {
        if (mc.theWorld == null || this.alartedPlayers == null)
            return;

        try {
            for (EntityPlayer player : mc.theWorld.playerEntities) {
                if (this.alartedPlayers.contains(player.getName()))
                    continue;

                if (player.getCurrentEquippedItem() != null) {
                    if (checkItem(player.getCurrentEquippedItem().getItem())) {
                        ChatUtils.info(EnumChatFormatting.GOLD + player.getName() + EnumChatFormatting.RESET + " is the murderer!!!");
                        this.alartedPlayers.add(player.getName());
                        murder = player;

                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };


    public boolean checkItem(Item item) {
        return !(item instanceof ItemMap) && !(item instanceof ItemArmorStand) && !item.getUnlocalizedName().equalsIgnoreCase("item.ingotGold") &&
                !(item instanceof ItemBow) && !item.getUnlocalizedName().equalsIgnoreCase("item.arrow") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.potion") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.paper") &&
                !item.getUnlocalizedName().equalsIgnoreCase("tile.tnt") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.web") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.bed") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.compass") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.comparator") &&
                !item.getUnlocalizedName().equalsIgnoreCase("item.shovelWood");
    }
}
