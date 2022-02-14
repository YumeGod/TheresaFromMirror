package cn.loli.client.module.modules.misc.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Element {
    public String name;
    public float x, y, width, height;
    public HAlignment hAlign;
    public VHAlignment vAlign;

    public Element(String name, HAlignment hAlign, VHAlignment vAlign) {
        this.name = name;
        this.hAlign = hAlign;
        this.vAlign = vAlign;
    }

    public void draw(int x, int y) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        float height = sr.getScaledHeight();
        float width = sr.getScaledWidth();
        if(hAlign == HAlignment.LEFT) {
            x = (int) (x + this.x);
        }
        else if(hAlign == HAlignment.CENTER) {
            x = (int) (x + this.x - width / 2);
        }
        else if(hAlign == HAlignment.RIGHT) {
            x = (int) (x + this.x - width);
        }
        if(vAlign == VHAlignment.TOP) {
            y = (int) (y + this.y);
        }
        else if(vAlign == VHAlignment.CENTER) {
            y = (int) (y + this.y - height / 2);
        }
        else if(vAlign == VHAlignment.BOTTOM) {
            y = (int) (y + this.y - height);
        }
        this.x = x;
        this.y = y;


    }

    public void mouseClicked(int x, int y, int button) {

    }


}

