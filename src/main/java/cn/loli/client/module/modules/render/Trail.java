package cn.loli.client.module.modules.render;

import cn.loli.client.events.MotionUpdateEvent;
import cn.loli.client.events.RenderEvent;
import cn.loli.client.events.UpdateEvent;
import cn.loli.client.injection.mixins.IAccessorRenderManager;
import cn.loli.client.module.Module;
import cn.loli.client.module.ModuleCategory;
import cn.loli.client.utils.render.RenderUtils;


import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.ColorProperty;
import dev.xix.property.impl.NumberProperty;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Trail extends Module {

    private final ArrayList<double[]> positions = new ArrayList<>();

    public final BooleanProperty rainbow = new BooleanProperty("Rainbow", false);
    public static final NumberProperty<Integer> length = new NumberProperty<>("Length", 1000, 100, 3000 , 100);

    public final BooleanProperty showInFirstPerson = new BooleanProperty("Show In FirstPerson", true);

    public final ColorProperty trailColor = new ColorProperty("Trail-Color", new Color(147, 144, 144, 210));
    int rainbowOffset;

    public Trail() {
        super("Trail", "Bread crumbs", ModuleCategory.RENDER);
    }

    private final IEventListener<RenderEvent> onRender = event -> {
        Color customColor = trailColor.getPropertyValue();
        if (mc.gameSettings.thirdPersonView != 0 || showInFirstPerson.getPropertyValue()) {
            GL11.glPushMatrix();

            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glLineWidth(2F);

            GL11.glBegin(GL11.GL_LINE_STRIP);

            int offset = 0;
            for (double[] pos : positions) {
                if (rainbow.getPropertyValue())
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
    };

    private final IEventListener<UpdateEvent> onUpdate = event -> {
        for (int i = 0; i < positions.size(); i++) {
            final double[] position = positions.get(i);
            if (System.currentTimeMillis() - position[3] > length.getPropertyValue()) {
                rainbowOffset++;
                positions.remove(position);
            }
        }
    };

    private final IEventListener<MotionUpdateEvent> onMotion = event -> {
        positions.add(new double[]{mc.thePlayer.posX, mc.thePlayer.posY + 0.01, mc.thePlayer.posZ, System.currentTimeMillis()});
    };



}
