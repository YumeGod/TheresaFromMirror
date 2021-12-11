

package cn.loli.client.injection.mixins;

import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityPlayerSP.class)
public interface IAccessorEntityPlayerSP {
    @Accessor("sprintToggleTimer")
    void setSprintToggleTimer(int sprintToggleTimer);
}
