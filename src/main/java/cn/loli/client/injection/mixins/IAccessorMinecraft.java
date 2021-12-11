

package cn.loli.client.injection.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface IAccessorMinecraft {
    @Accessor("session")
    void setSession(Session session);

    @Accessor
    Timer getTimer();

    @Invoker("rightClickMouse")
    void invokeRightClickMouse();

    @Invoker("clickMouse")
    void invokeClickMouse();
}
