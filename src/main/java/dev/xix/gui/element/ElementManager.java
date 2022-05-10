package dev.xix.gui.element;

import cn.loli.client.events.Render2DEvent;
import dev.xix.TheresaClient;
import dev.xix.event.bus.IEventListener;
import dev.xix.feature.ITheresaFeature;
import dev.xix.feature.module.AbstractTheresaModule;
import dev.xix.feature.module.TheresaModuleManager;
import dev.xix.util.render.RenderingUtil;
import net.minecraft.client.renderer.GlStateManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class ElementManager {
    private final Map<String, HashMap<String, AbstractElement>> elements = new HashMap<>();

    public void init() {
        TheresaClient.getInstance().getEventBus().register(this);
    }

    public Collection<AbstractElement> getAllElements() {
        final Collection<AbstractElement> elements = new HashSet<>();
        this.elements.values().forEach((module) -> elements.addAll(module.values()));
        return elements;
    }

    public HashMap<String, AbstractElement> getElements(final ITheresaFeature feature) {
        return getElements(feature.getIdentifier());
    }

    public HashMap<String, AbstractElement> getElements(final String module) {
        return elements.get(module);
    }

    public AbstractElement getElement(final ITheresaFeature module, String identifier) {
        return getElement(module.getIdentifier(), identifier);
    }

    public AbstractElement getElement(final String module, final String identifier) {
        return getElements(module).get(identifier);
    }

    public AbstractElement addElement(final ITheresaFeature feature, final AbstractElement element) {
        return addElement(feature.getIdentifier(), element);
    }

    public AbstractElement addElement(final String feature, AbstractElement element) {
        if (!this.elements.containsKey(feature)) this.elements.put(feature, new HashMap<>());
        HashMap<String, AbstractElement> moduleElements = getElements(feature);
        if (!moduleElements.containsKey(element.getIdentifier())) moduleElements.put(element.getIdentifier(), element);
        else element = moduleElements.get(element.getIdentifier());
        return element;
    }

    public void render() {
        double scale = 2 / RenderingUtil.getScaledFactor();
        GlStateManager.scale(scale, scale, scale);

        for (Map.Entry<String, HashMap<String, AbstractElement>> module : elements.entrySet()) {
            for (final AbstractElement element : module.getValue().values()) {
                if (element.visible && (TheresaClient.getInstance().getModuleManager().getModuleByStringOrNull(module.getKey()).getEnabled() || element.getAnimation() != null)) {
                    element.renderElement();
                }
            }
        }

        scale = RenderingUtil.getScaledFactor() / 2;
        GlStateManager.scale(scale, scale, scale); // Return to default minecraft scaling
    }

    private final IEventListener<Render2DEvent> render2DEvent = event -> render();
}
