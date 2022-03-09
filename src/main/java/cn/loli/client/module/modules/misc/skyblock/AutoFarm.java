package cn.loli.client.module.modules.misc.skyblock;

import cn.loli.client.events.RenderBlockEvent;
import cn.loli.client.events.TickEvent;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoFarm extends Module {

    final ArrayList<BlockPos> list = new ArrayList<>();
    List<Block> blockType;
    List<Item> plantType;

    public AutoFarm() {
        super("Auto Farm", "Auto do the farm work", ModuleCategory.MISC);
        blockType = Arrays.asList(Blocks.melon_block, Blocks.pumpkin, Blocks.wheat, Blocks.carpet, Blocks.cactus, Blocks.nether_wart, Blocks.potatoes, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.reeds);
        plantType = Arrays.asList(Items.melon_seeds, Items.wheat_seeds, Items.pumpkin_seeds, Items.reeds, Items.potato, Items.carrot);
    }

    //TODO : Auto Farm

    @EventTarget
    public void onRenderBlock(RenderBlockEvent e) {
        BlockPos pos = new BlockPos(e.x, e.y, e.z);
        if (!list.contains(pos) && blockType.contains(e.block))
            list.add(pos);
    }

    @EventTarget
    public void onSort(TickEvent e) {
        list.removeIf(pos -> !blockType.contains(Minecraft.getMinecraft().theWorld.getBlockState(pos).getBlock()));
    }


}
