

package cn.loli.client.value;

import cn.loli.client.Main;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.events.MoveFlyEvent;
import cn.loli.client.utils.misc.ChatUtils;


import dev.xix.event.bus.IEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueManager {
    @NotNull
    public final HashMap<String, List<Value>> valueMap = new HashMap<>();

    @NotNull
    public final HashMap<Value, String> ownerMap = new HashMap<>();

    @NotNull
    public final HashMap<Value, Integer> keyBind = new HashMap<>();

    @NotNull
    public final HashMap<Value, Integer> modeSelect = new HashMap<>();

    @NotNull
    public final HashMap<Value, Number> numberPick = new HashMap<>();

    public ValueManager() {
        Main.INSTANCE.eventBus.register(this);
    }

    /**
     * @param name   The name of the owner
     * @param object The object where value-fields are declared
     */
    public void registerObject(String name, @NotNull Object object) {
        List<Value> values = new ArrayList<>();
        for (final Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(object);

                if (obj instanceof Value) {
                    values.add((Value) obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        valueMap.put(name, values);
    }


    /**
     * @param name   The name of the owner
     * @param object The Value-object
     *               this method will add the value to the owner if it doesn't exist
     */
    public void register(String name, @NotNull Value object) {
        List<Value> values = new ArrayList<>();
        if (valueMap.get(name) != null)
            values = valueMap.get(name);

        values.add(object);
        valueMap.put(name, values);
    }

    /**
     * @param name The name of the owner
     * @return If there's an owner with this name (the case is ignored) it will return all values of it else it will return null
     */
    public List<Value> getAllValuesFrom(String name) {
        for (Map.Entry<String, List<Value>> stringListEntry : valueMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }

    public @NotNull HashMap<String, List<Value>> getAllValues() {
        return valueMap;
    }

    /**
     * @param owner The name of the owner
     * @param name  The name of the value
     * @return The value or null
     */
    @Nullable
    public Value get(String owner, @NotNull String name, boolean ignoreSpace) {
        List<Value> found = getAllValuesFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> name.equalsIgnoreCase(ignoreSpace ? val.getName().replaceAll(" ", "") : val.getName())).findFirst().orElse(null);
    }


    private final IEventListener<KeyEvent> onKey = event -> {
        for (Value value : keyBind.keySet())
            if (keyBind.get(value) == event.getKey()) {
                if (value instanceof BooleanValue) {
                    BooleanValue booleanValue = (BooleanValue) value;
                    booleanValue.setObject(!booleanValue.getObject());
                    ChatUtils.info("Value " + value.getName() + " is now " + value.getObject());
                }
                if (value instanceof ModeValue) {
                    ModeValue modeValue = (ModeValue) value;
                    Integer integer = modeSelect.get(value);
                    modeSelect.put(modeValue, ((ModeValue) value).getObject());
                    modeValue.setObject(integer);
                    ChatUtils.info("Value " + value.getName() + " is now " + value.getObject());
                }
                if (value instanceof NumberValue) {
                    NumberValue numberValue = (NumberValue) value;
                    Number number = numberPick.get(numberValue);
                    numberPick.put(numberValue, (Number) numberValue.getObject());
                    if (numberValue.getObject() instanceof Integer) {
                        numberValue.setObject(number.intValue());
                    } else if (numberValue.getObject() instanceof Float) {
                        numberValue.setObject(number.floatValue());
                    } else {
                        numberValue.setObject(number.doubleValue());
                    }
                    ChatUtils.info("Value " + value.getName() + " is now " + value.getObject());
                }

            }
    };


}
