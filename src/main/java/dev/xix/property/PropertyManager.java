package dev.xix.property;

import cn.loli.client.Main;
import cn.loli.client.events.KeyEvent;
import cn.loli.client.events.MoveFlyEvent;
import cn.loli.client.utils.misc.ChatUtils;


import com.google.common.hash.BloomFilter;
import dev.xix.event.bus.IEventListener;
import dev.xix.property.impl.BooleanProperty;
import dev.xix.property.impl.EnumProperty;
import dev.xix.property.impl.NumberProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class PropertyManager {
    @NotNull
    public final HashMap<String, List<AbstractTheresaProperty>> valueMap = new HashMap<>();

    @NotNull
    public final HashMap<AbstractTheresaProperty, Integer> keyBind = new HashMap<>();

    @NotNull
    public final HashMap<AbstractTheresaProperty, Integer> modeSelect = new HashMap<>();

    @NotNull
    public final HashMap<AbstractTheresaProperty, Number> numberPick = new HashMap<>();
    @NotNull
    public final HashMap<AbstractTheresaProperty, String> ownerMap = new HashMap<>();

    public PropertyManager() {
        Main.INSTANCE.eventBus.register(this);
    }

    /**
     * @param name   The name of the owner
     * @param object The object where value-fields are declared
     */
    public void registerObject(String name, @NotNull Object object) {
        List<AbstractTheresaProperty> values = new ArrayList<>();
        for (final Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                final Object obj = field.get(object);

                if (obj instanceof AbstractTheresaProperty) {
                    values.add((AbstractTheresaProperty) obj);
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
    public void register(String name, @NotNull AbstractTheresaProperty object) {
        List<AbstractTheresaProperty> values = new ArrayList<>();
        if (valueMap.get(name) != null)
            values = valueMap.get(name);

        values.add(object);
        valueMap.put(name, values);
    }

    /**
     * @param name The name of the owner
     * @return If there's an owner with this name (the case is ignored) it will return all values of it else it will return null
     */
    public List<AbstractTheresaProperty> getAllValuesFrom(String name) {
        for (Map.Entry<String, List<AbstractTheresaProperty>> stringListEntry : valueMap.entrySet()) {
            if (stringListEntry.getKey().equalsIgnoreCase(name)) return stringListEntry.getValue();
        }
        return null;
    }

    public @NotNull HashMap<String, List<AbstractTheresaProperty>> getAllValues() {
        return valueMap;
    }

    /**
     * @param owner The name of the owner
     * @param name  The name of the value
     * @return The value or null
     */
    @Nullable
    public AbstractTheresaProperty get(String owner, @NotNull String name, boolean ignoreSpace) {
        List<AbstractTheresaProperty> found = getAllValuesFrom(owner);

        if (found == null) return null;

        return found.stream().filter(val -> name.equalsIgnoreCase(ignoreSpace ? val.getName().replaceAll(" ", "") : val.getName())).findFirst().orElse(null);
    }


    private final IEventListener<KeyEvent> onKey = event -> {
        for (AbstractTheresaProperty value : keyBind.keySet())
            if (keyBind.get(value) == event.getKey()) {
                if (value instanceof BooleanProperty) {
                    BooleanProperty booleanValue = (BooleanProperty) value;
                    booleanValue.setPropertyValue(!booleanValue.getPropertyValue());
                    ChatUtils.info("Value " + value.getName() + " is now " + value.getPropertyValue());
                }
                if (value instanceof EnumProperty) {
                    EnumProperty modeValue = (EnumProperty) value;
                    Integer integer = modeSelect.get(value);
                    modeSelect.put(modeValue, Arrays.binarySearch(((EnumProperty<?>) value).getEnumConstants(), value.getPropertyValue()));
                    modeValue.setValue(integer);
                    ChatUtils.info("Value " + value.getName() + " is now " + value.getPropertyValue());
                }
                if (value instanceof NumberProperty) {
                    NumberProperty numberValue = (NumberProperty) value;
                    Number number = numberPick.get(numberValue);
                    numberPick.put(numberValue, (Number) numberValue.getPropertyValue());
                    if (numberValue.getPropertyValue() instanceof Integer) {
                        numberValue.setPropertyValue(number.intValue());
                    } else if (numberValue.getPropertyValue() instanceof Float) {
                        numberValue.setPropertyValue(number.floatValue());
                    } else {
                        numberValue.setPropertyValue(number.doubleValue());
                    }
                    ChatUtils.info("Value " + value.getName() + " is now " + value.getPropertyValue());
                }

            }
    };


}
