package cn.loli.client.injection

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin
import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import theresa.loader.Loader

class MixinLoaderPlugin : IFMLLoadingPlugin {

    private var isObfuscatedEnvironment = false


    init {
        Loader.load()
        if (Loader.shouldInit) {
            MixinBootstrap.init()
            Mixins.addConfiguration("mixins.theresa.json")
            MixinEnvironment.getDefaultEnvironment().obfuscationContext = "searge"
            MixinEnvironment.getDefaultEnvironment().side = MixinEnvironment.Side.CLIENT
        }
    }

    override fun getModContainerClass(): String? = null

    override fun getASMTransformerClass(): Array<String> = emptyArray()

    override fun getSetupClass(): String? = null

    override fun injectData(data: Map<String?, Any?>) {
        isObfuscatedEnvironment = (data["runtimeDeobfuscationEnabled"] as Boolean?)!!
    }

    override fun getAccessTransformerClass(): String? = null

}