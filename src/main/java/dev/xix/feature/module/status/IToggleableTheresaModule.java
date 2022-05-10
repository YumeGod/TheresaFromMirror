package dev.xix.feature.module.status;

public interface IToggleableTheresaModule {
    boolean getEnabled();

    void setEnabled(final boolean enabled);

    void toggle();
}
