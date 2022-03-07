package cn.loli.client.module.modules.render;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.RenderUtils;
import cn.loli.client.value.BooleanValue;
import cn.loli.client.value.ColorValue;
import cn.loli.client.value.NumberValue;
import com.darkmagician6.eventapi.EventTarget;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Trail extends Module {

    private final ArrayList<double[]> positions = new ArrayList<>();

    public final BooleanValue rainbow = new BooleanValue("Rainbow", false);
    public static final NumberValue<Integer> length = new NumberValue<>("Length", 1000, 100, 3000);

    public final BooleanValue showInFirstPerson = new BooleanValue("Show In FirstPerson", true);

    public final ColorValue trailColor = new ColorValue("Trail-Color", new Color(147, 144, 144, 210));
    int rainbowOffset;

    public Trail() {
        super("Trail", "Bread crumbs", ModuleCategory.RENDER);
    }

    @EventTarget
    private void onRender(RenderEvent event) {
        Color customColor = trailColor.getObject();
        if (mc.gameSettings.thirdPersonView != 0 || showInFirstPerson.getObject()) {
            GL11.glPushMatrix();

            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(2F);

            GL11.glBegin(GL11.GL_LINE_STRIP);

            int offset = 0;
            for (double[] pos : positions) {
                if (rainbow.getObject())
                    customColor = RenderUtils.getRainbow((offset + rainbowOffset) * 100, 6000, 0.8F, 1F);
                GL11.glColor4f(customColor.getRed() / 255F, customColor.getGreen() / 255F, customColor.getBlue() / 255F, customColor.getAlpha() / 255F);
                GL11.glVertex3d(pos[0] - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosX(), pos[1] - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosY(), pos[2] - ((IAccessorRenderManager) mc.getRenderManager()).getRenderPosZ());
                offset++;
            }

            GL11.glVertex3d(0, 0.01, 0);
            GL11.glEnd();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);

            GL11.glPopMatrix();
        }
    }

    @EventTarget
    private void onUpdate(UpdateEvent event) {
        for (int i = 0; i < positions.size(); i++) {
            final double[] position = positions.get(i);
            if (System.currentTimeMillis() - position[3] > length.getObject()) {
                rainbowOffset++;
                positions.remove(position);
            }
        }
    }

    @EventTarget
    private void onMotion(MotionUpdateEvent event) {
        positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.posY + 0.01, mc.thePlayer.posZ, System.currentTimeMillis()});
    }

}
