package dev.xix.event.bus;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public final class EventBus<T> {
    private final Map<Type, List<EventListenerStorage<T>>> storageMap = new HashMap<>();
    private final Map<Type, List<IEventListener<T>>> callbackMap = new HashMap<>();

    public void register(final Object listeningObject) {
        for (final Field declaredField : listeningObject.getClass().getDeclaredFields()) {
            try {
                if (declaredField.getType().equals(IEventListener.class)) {
                    final boolean accessible = declaredField.isAccessible();
                    if (!accessible) {
                        declaredField.setAccessible(true);
                    }
                    final Type type = ((ParameterizedType) declaredField.getGenericType()).getActualTypeArguments()[0];
                    final IEventListener<T> callback = (IEventListener<T>) declaredField.get(listeningObject);
                    declaredField.setAccessible(accessible);

                    if (storageMap.containsKey(type)) {
                        final List<EventListenerStorage<T>> storages = storageMap.get(type);
                        storages.add(new EventListenerStorage<>(listeningObject, callback));
                    } else {
                        storageMap.put(type, new ArrayList<>(Collections.singletonList(new EventListenerStorage<>(listeningObject, callback))));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        updateCallbacks();
    }

    public void unregister(final Object listeningObject) {
        for (final List<EventListenerStorage<T>> value : storageMap.values()) {
            value.removeIf(eventListenerStorage -> eventListenerStorage.owner() == listeningObject);
        }
        updateCallbacks();
    }

    public void call(final T t) {
        final List<IEventListener<T>> callbacks = callbackMap.get(t.getClass());
        if (callbacks != null) {
            for (final IEventListener<T> callback : callbacks) {
                try {
                    callback.call(t);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private void updateCallbacks() {
        for (final Type type : storageMap.keySet()) {
            final List<EventListenerStorage<T>> storages = storageMap.get(type);
            final List<IEventListener<T>> callbacks = new ArrayList<>(storages.size());
            for (EventListenerStorage<T> storage : storages) {
                callbacks.add(storage.callback());
            }
            callbackMap.put(type, callbacks);
        }
    }
}
