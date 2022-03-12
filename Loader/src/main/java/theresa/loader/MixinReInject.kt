package theresa.loader

import net.minecraft.launchwrapper.Launch
import org.spongepowered.asm.lib.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import java.io.File
import java.net.MalformedURLException

class MixinReInject : IMixinConfigPlugin {
    private var mixins: MutableList<String> = ArrayList()

    override fun onLoad(mixinPackage: String?) {
        with(Loader) {
            if (shouldInit) {
                try {
                    Launch.classLoader.addURL(refMapFile.toURI().toURL())
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

                refMapFile.writeToFile(refMapByte)
                refMapFile.deleteOnExit()

                refMapByte = ByteArray(0)

                mixinCache.forEach {
                    mixins.add(it)
                }

                mixinCache.clear()
            }
        }
    }

    override fun getRefMapperConfig(): String? {
        return if (!Loader.shouldInit) {
            "mixins.loli.refmap.json"
        } else {
            try {
                val refMap = Loader.refMapFile.absolutePath.replace(File.separatorChar, '/')
                (if (refMap.startsWith("/")) "file:" else "file:/") + refMap
            } catch (e: MalformedURLException) {
                null
            }
        }
    }

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?): Boolean {
        return true
    }

    override fun getMixins(): MutableList<String> {
        return mixins
    }

    override fun acceptTargets(myTargets: MutableSet<String>?, otherTargets: MutableSet<String>?) {
    }


    override fun preApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }

    override fun postApply(targetClassName: String?, targetClass: ClassNode?, mixinClassName: String?, mixinInfo: IMixinInfo?) {
    }
}