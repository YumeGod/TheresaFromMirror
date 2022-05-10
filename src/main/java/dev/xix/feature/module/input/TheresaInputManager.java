package dev.xix.feature.module.input;

import java.util.ArrayList;
import java.util.List;

public final class TheresaInputManager {
    private final List<IInputtableTheresaModule> inputtables;

    public TheresaInputManager() {
        this.inputtables = new ArrayList<>();
    }

    public List<IInputtableTheresaModule> getInputtables() {
        return inputtables;
    }
}
