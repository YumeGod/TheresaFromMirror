

package cn.loli.client.gui.altmanager;

import cn.loli.client.injection.mixins.IAccessorMinecraft;
import cn.loli.client.utils.Utils;
import com.mojang.authlib.exceptions.AuthenticationException;
/*
import me.ratsiel.auth.model.mojang.MinecraftAuthenticator;
import me.ratsiel.auth.model.mojang.MinecraftToken;
import me.ratsiel.auth.model.mojang.profile.MinecraftProfile;
*/
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.net.Proxy;

public class GuiAltManager extends GuiScreen {
    private GuiTextField usernameField;
    private GuiPasswordField passwordField;
    private GuiTextField usernamePasswordField;
    private String status;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawString(mc.fontRendererObj, status, width / 2 - fontRendererObj.getStringWidth(Utils.stripColorCodes(status)) / 2, height / 2 - 110, 0xAAAAAA);

        drawString(mc.fontRendererObj, "Username: ", width / 2 - 250 / 2, height / 2 - 220 + 135, -1);
        usernameField.drawTextBox();

        drawString(mc.fontRendererObj, "Password: ", width / 2 - 250 / 2, height / 2 - 220 + 135 + 43, -1);
        passwordField.drawTextBox();

        drawString(mc.fontRendererObj, "Email:Password: ", width / 2 - 250 / 2, height / 2 - 220 + 135 + 45 * 2, -1);
        usernamePasswordField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3) {
            login();
        }

        if (button.id == 4) {
            microsoftLogin();
        }

        if (button.id == 5) {
            usernameField.setText("");
            passwordField.setText("");
            usernamePasswordField.setText("");

            String sessionType = mc.getSession().getSessionType() == Session.Type.MOJANG ? "ONLINE" : "OFFLINE";
            status = "Logged in as " + mc.getSession().getUsername() + " [" + sessionType + "]";
        }

        if (button.id == 6) {
            mc.displayGuiScreen(null);
        }

        super.actionPerformed(button);
    }

    private void login() {
        if (usernamePasswordField.getText().length() != 0 && usernamePasswordField.getText().contains(":") && !usernamePasswordField.getText().endsWith(":")) {
            usernameField.setText(usernamePasswordField.getText().split(":")[0]);
            passwordField.setText(usernamePasswordField.getText().split(":")[1]);
            usernamePasswordField.setText("");
        }

        if (!usernameField.getText().matches(".*\\w.*")) {
            status = "\u00a7cError: Empty username";
            return;
        }

        if (passwordField.getText().length() == 0) {
            Session session = Utils.createOfflineSession(usernameField.getText(), Proxy.NO_PROXY);
            ((IAccessorMinecraft) mc).setSession(session);
            status = "\u00a7eSuccessfully logged in as " + usernameField.getText() + " [OFFLINE]!";
            return;
        }

        try {
            Session session = Utils.createSession(usernameField.getText(), passwordField.getText(), Proxy.NO_PROXY);
            ((IAccessorMinecraft) mc).setSession(session);
            status = "\u00a7aSuccessfully logged in as " + session.getUsername() + " [ONLINE]!";
        } catch (AuthenticationException e) {
            status = "\u00a7cError: " + e.getMessage();
        }
    }

    private void microsoftLogin() {
        /*
        if (usernamePasswordField.getText().length() != 0 && usernamePasswordField.getText().contains(":") && !usernamePasswordField.getText().endsWith(":")) {
            usernameField.setText(usernamePasswordField.getText().split(":")[0]);
            passwordField.setText(usernamePasswordField.getText().split(":")[1]);
            usernamePasswordField.setText("");
        }

        if (!usernameField.getText().matches(".*\\w.*")) {
            status = "\u00a7cError: Empty username";
            return;
        }

        if (passwordField.getText().length() == 0) {
            Session session = Utils.createOfflineSession(usernameField.getText(), Proxy.NO_PROXY);
            ((IAccessorMinecraft) mc).setSession(session);
            status = "\u00a7eSuccessfully logged in as " + usernameField.getText() + " [OFFLINE]!";
            return;
        }

        try {
            MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();
            MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(usernameField.getText(), passwordField.getText());
            MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
            Session session = new Session(minecraftProfile.getUsername(), minecraftProfile.getUuid().toString(), minecraftToken.getAccessToken(), "mojang");
            ((IAccessorMinecraft) mc).setSession(session);
            if (session.getToken() != null) {
                status = "\u00a7aSuccessfully logged in as " + session.getUsername() + " [ONLINE]! (Microsoft Login)";
            } else {
                status = "\u00a7cError: login failed!";
            }
        } catch (Exception e) {
            status = "\u00a7cError: " + e.getMessage();
        }
        */
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_RETURN) {
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            login();
        } else if (keyCode == Keyboard.KEY_TAB) {
            if (usernameField.isFocused()) {
                usernameField.setFocused(false);
                passwordField.setFocused(true);
            } else if (passwordField.isFocused()) {
                passwordField.setFocused(false);
                usernamePasswordField.setFocused(true);
            } else if (usernamePasswordField.isFocused()) {
                usernamePasswordField.setFocused(false);
                usernameField.setFocused(true);
            }
        }

        usernameField.textboxKeyTyped(typedChar, keyCode);
        passwordField.textboxKeyTyped(typedChar, keyCode);
        usernamePasswordField.textboxKeyTyped(typedChar, keyCode);

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        usernameField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordField.mouseClicked(mouseX, mouseY, mouseButton);
        usernamePasswordField.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        usernameField = new GuiTextField(0, mc.fontRendererObj, width / 2 - 250 / 2, height / 2 - 220 + 150, 250, 20);
        passwordField = new GuiPasswordField(mc.fontRendererObj, width / 2 - 250 / 2, height / 2 - 220 + 150 + 42, 250, 20);
        usernamePasswordField = new GuiTextField(1, mc.fontRendererObj, width / 2 - 250 / 2, height / 2 - 220 + 150 + 44 * 2, 250, 20);

        usernameField.setMaxStringLength(64);
        passwordField.setMaxStringLength(64);
        usernamePasswordField.setMaxStringLength(128);

        buttonList.add(new GuiButton(3, width / 2 - 250 / 2, height / 2 - 220 + 150 + 40 * 3, 80, 20, "Login"));
        buttonList.add(new GuiButton(4, width / 2 - 250 / 2 + 85, height / 2 - 220 + 150 + 40 * 3, 80, 20, "MSLogin"));
        buttonList.add(new GuiButton(5, width / 2 - 250 / 2 + 85 + 85, height / 2 - 220 + 150 + 40 * 3, 80, 20, "Clear"));
        buttonList.add(new GuiButton(6, width / 2 - 250 / 2, height / 2 - 220 + 150 + 40 * 3 + 25, 250, 20, "Back"));

        if (mc.getSession() == null) {
            status = "Not logged in.";
        }

        String sessionType = mc.getSession().getSessionType() == Session.Type.MOJANG ? "ONLINE" : "OFFLINE";
        status = "Logged in as " + mc.getSession().getUsername() + " [" + sessionType + "]";

        super.initGui();
    }
}
