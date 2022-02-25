package cn.loli.client.module.modules.render;

import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.NumberValue;
import net.minecraft.block.Block;
import net.minecraft.block.BlockMobSpawner;
import net.minecraft.block.BlockOre;

import java.util.Arrays;
import java.util.List;

public class Xray extends Module {

    public final List<Class<? extends Block>> blocksToFind = Arrays.asList(
            BlockOre.class,
            BlockMobSpawner.class
    );

    public final NumberValue<Integer> opacity = new NumberValue<>("Opacity", 120, 0, 255);
    private final BooleanValue bypass = new BooleanValue("Bypass", true);

    public Xray() {
        super("Xray", "Let you find some ore", ModuleCategory.RENDER);
    }


    @Override
    public void onEnable() {
        super.onEnable();

        mc.gameSettings.gammaSetting = 1000;

        if (mc.renderGlobal != null)
            mc.renderGlobal.loadRenderers();
    }

    @Override
    public void onDisable() {
        super.onEnable();

        if (mc.renderGlobal != null)
            mc.renderGlobal.loadRenderers();
    }
}
