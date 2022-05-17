package dev.xix.feature.module;

import cn.loli.client.utils.player.InventoryUtil;
import cn.loli.client.utils.player.PlayerUtils;
import cn.loli.client.utils.player.movement.MoveUtils;
import cn.loli.client.utils.player.rotation.RotationUtils;
import dev.xix.TheresaClient;
import dev.xix.event.impl.module.ModuleStatusEvent;
import dev.xix.feature.ITheresaFeature;
import dev.xix.feature.module.input.IInputtableTheresaModule;
import dev.xix.feature.module.status.IToggleableTheresaModule;
import dev.xix.gui.element.AbstractElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

import java.util.*;

public abstract class AbstractTheresaModule implements ITheresaFeature, IInputtableTheresaModule, IToggleableTheresaModule {

    protected final String name;
    protected final String identifier;

    protected final TheresaModuleCategory theresaModuleCategory;

    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final GameSettings game = mc.gameSettings;
    protected static final Random r = new Random();

    protected static final PlayerUtils playerUtils = PlayerUtils.getInstance();
    protected static final RotationUtils rotationUtils = RotationUtils.getInstance();
    protected static final InventoryUtil inventoryUtil = InventoryUtil.getInstance();
    protected static final MoveUtils moveUtils = MoveUtils.getInstance();

    protected boolean enabled;
    protected int key;

    private final Map<String, AbstractElement> elements;

    protected AbstractTheresaModule(final String name, final TheresaModuleCategory category) {
        this.name = name;
        this.identifier = name.replaceAll(" ", "");
        this.theresaModuleCategory = category;

        this.elements = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void addElements(AbstractElement... elements) {
        for (final AbstractElement element : elements) {
            this.elements.put(element.getIdentifier(), element);
        }
    }

    public AbstractElement getElement(final String identifier) {
        return elements.getOrDefault(identifier, null);
    }

    public TheresaModuleCategory getTheresaModuleCategory() {
        return theresaModuleCategory;
    }

    @Override
    public int getKey() {
        return key;
    }

    @Override
    public void setKey(final int key) {
        this.key = key;
    }

    @Override
    public void pressKey() {
        toggle();
    }

    @Override
    public boolean getEnabled() {
        return false;
    }

    @Override
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void toggle() {
        enabled = !enabled;

        final ModuleStatusEvent event = new ModuleStatusEvent(this);

        TheresaClient.getInstance().getEventBus().register(event);

        if (event.isCancelled()) {
            enabled = !enabled;
            return;
        }

        if (this.enabled) {
            onEnable();
        } else {
            onDisable();
        }
    }

    public void onEnable() {
        this.enabled = true;
        TheresaClient.getInstance().getEventBus().register(this);
    }

    public void onDisable() {
        this.enabled = false;
        TheresaClient.getInstance().getEventBus().unregister(this);
    }

    public Map<String, AbstractElement> getElements() {
        return elements;
    }
}
