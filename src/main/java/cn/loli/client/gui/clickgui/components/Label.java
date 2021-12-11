

package cn.loli.client.gui.clickgui.components;

import cn.loli.client.gui.clickgui.AbstractComponent;
import cn.loli.client.gui.clickgui.IRenderer;
import cn.loli.client.gui.clickgui.Window;

public class Label extends AbstractComponent {
    private String text;

    public Label(IRenderer renderer, String text) {
        super(renderer);
        setText(text);
    }

    @Override
    public void render() {
//        glClearDepthf(1.0f);
//        glClear(GL_DEPTH_BUFFER_BIT);
//        glColorMask(false, false, false, false);
//        glDepthFunc(GL_LESS);
//        glEnable(GL_DEPTH_TEST);
//        glDepthMask(true);

//        renderer.drawRect(x, y, getWidth() / 1.5, getHeight(), Color.white);


//        glColorMask(true, true, true, true);
//        glDepthMask(true);
//        glDepthFunc(GL_EQUAL);

        renderer.drawString(x, y, text, Window.FOREGROUND);

//        glDisable(GL_DEPTH_TEST);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        setWidth(renderer.getStringWidth(text));
        setHeight(renderer.getStringHeight(text));

        this.text = text;
    }
}
