

package cn.loli.client.file;

import cn.loli.client.Main;
import cn.loli.client.module.Module;
import cn.loli.client.utils.misc.ChatUtils;
import cn.loli.client.value.ModeValue;
import cn.loli.client.value.NumberValue;
import cn.loli.client.value.Value;
import com.google.common.io.Files;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileManager {
    private final File clientDir = new File(Minecraft.getMinecraft().mcDataDir, Main.CLIENT_NAME);
    private final File extraFontDir = new File(Minecraft.getMinecraft().mcDataDir, "font");

    private final File backupDir = new File(clientDir, "backups");
    public final File scriptsDir = new File(clientDir, "scripts");

    public final File fontDir = new File(scriptsDir, "font");

    private final File saveFile = new File(clientDir, "client.json");
    private final File modulesFile = new File(clientDir, "modules.json");
    private final File valuesFile = new File(clientDir, "values.json");

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void save() throws Exception {
        clientDir.mkdirs();

        if (!saveFile.exists() && !saveFile.createNewFile())
            throw new IOException("Failed to create " + saveFile.getAbsolutePath());
        if (!modulesFile.exists() && !modulesFile.createNewFile())
            throw new IOException("Failed to create " + modulesFile.getAbsolutePath());
        if (!valuesFile.exists() && !valuesFile.createNewFile())
            throw new IOException("Failed to create " + valuesFile.getAbsolutePath());

        JsonElement clientElement = new JsonParser().parse(toClientJsonObject().toString());
        JsonElement modulesElement = new JsonParser().parse(toModulesJsonObject(true).toString());
        JsonElement valuesElement = new JsonParser().parse(toValuesJsonObject().toString());

        Files.write(gson.toJson(clientElement).getBytes(StandardCharsets.UTF_8), saveFile);
        Files.write(gson.toJson(modulesElement).getBytes(StandardCharsets.UTF_8), modulesFile);
        Files.write(gson.toJson(valuesElement).getBytes(StandardCharsets.UTF_8), valuesFile);
    }

    public void saveCfg(String name) throws Exception {
        File cfg = new File(clientDir, name + ".cfg");
        if (!cfg.exists() && !cfg.createNewFile())
            throw new IOException("Failed to create " + cfg.getAbsolutePath());

        JsonElement valuesElement = new JsonParser().parse(toCfgJsonObject().toString());
        Files.write(gson.toJson(valuesElement).getBytes(StandardCharsets.UTF_8), cfg);
        ChatUtils.info("Saved " + name + ".cfg");
    }

    @NotNull
    private JsonObject toClientJsonObject() {
        Main.INSTANCE.logger.info("Saving settings");

        JsonObject obj = new JsonObject();

        {
            JsonObject metadata = new JsonObject();

            metadata.addProperty("clientVersion", Main.CLIENT_VERSION_NUMBER);

            obj.add("metadata", metadata);
        }

        return obj;
    }

    @NotNull
    private JsonObject toModulesJsonObject(boolean saveKeyBind) {
        JsonObject obj = new JsonObject();

        {
            JsonObject modulesObject = new JsonObject();

            for (Module module : Main.INSTANCE.moduleManager.getModules()) {
                JsonObject moduleObject = new JsonObject();

                moduleObject.addProperty("state", module.getState());

                if (saveKeyBind)
                    moduleObject.addProperty("keybind", module.getKeybind());

                modulesObject.add(module.getName(), moduleObject);
            }

            obj.add("modules", modulesObject);
        }

        return obj;
    }

    @NotNull
    private JsonObject toValuesJsonObject() {
        JsonObject obj = new JsonObject();

        {
            JsonObject valuesObject = new JsonObject();

            for (Map.Entry<String, List<Value>> stringListEntry : Main.INSTANCE.valueManager.getAllValues().entrySet()) {
                JsonObject value = new JsonObject();

                for (Value value1 : stringListEntry.getValue()) value1.addToJsonObject(value);

                valuesObject.add(stringListEntry.getKey(), value);
            }

            obj.add("values", valuesObject);
        }

        return obj;
    }

    @NotNull
    private JsonObject toCfgJsonObject() {
        JsonObject obj = new JsonObject();

        {
            JsonObject cfgModuleObject = new JsonObject();
            JsonObject cfgValueObject = new JsonObject();
            JsonObject cfgKeyBindObject = new JsonObject();

            for (Module module : Main.INSTANCE.moduleManager.getModules()) {
                JsonObject jsonObject = new JsonObject();

                jsonObject.addProperty("state", module.getState());

                cfgModuleObject.add(module.getName(), jsonObject);
            }

            obj.add("modules", cfgModuleObject);

            for (Map.Entry<String, List<Value>> stringListEntry : Main.INSTANCE.valueManager.getAllValues().entrySet()) {
                JsonObject jsonObject = new JsonObject();

                for (Value values : stringListEntry.getValue()) values.addToJsonObject(jsonObject);

                cfgValueObject.add(stringListEntry.getKey(), jsonObject);
            }

            obj.add("values", cfgValueObject);

            for (Map.Entry<String, List<Value>> stringListEntry : Main.INSTANCE.valueManager.getAllValues().entrySet()) {
                JsonObject jsonObject = new JsonObject();
                JsonObject fatherObject = new JsonObject();

                for (Value values : stringListEntry.getValue()) {
                    int keybind = Main.INSTANCE.valueManager.keyBind.get(values) == null ? 0 : Main.INSTANCE.valueManager.keyBind.get(values);
                    if (values instanceof NumberValue) {
                        NumberValue numberValue = (NumberValue) values;
                        if (numberValue.getObject() instanceof Integer) {
                            jsonObject.addProperty("value", ((Number) numberValue.getObject()).intValue());
                        } else if (numberValue.getObject() instanceof Float) {
                            jsonObject.addProperty("value", ((Number) numberValue.getObject()).floatValue());
                        } else {
                            jsonObject.addProperty("value", ((Number) numberValue.getObject()).doubleValue());
                        }
                    }
                    if (values instanceof ModeValue) {
                        jsonObject.addProperty("mode", ((ModeValue) values).getObject());
                    }
                    jsonObject.addProperty("keybind", keybind);
                    fatherObject.add(values.getName(), jsonObject);
                }

                cfgKeyBindObject.add(stringListEntry.getKey(), fatherObject);
            }

            obj.add("keybinds", cfgKeyBindObject);
        }

        return obj;
    }

    public void load(String name) throws IOException {
        File cfg = new File(clientDir, name + ".cfg");
        if (!cfg.exists() && !cfg.createNewFile())
            throw new IOException("Failed to create " + cfg.getAbsolutePath());

        try {
            JsonObject cfgObject = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(cfg)));
            // Modules
            JsonElement modulesElement = cfgObject.get("modules");

            if (modulesElement instanceof JsonObject) {
                JsonObject modules = (JsonObject) modulesElement;

                for (Map.Entry<String, JsonElement> stringJsonElementEntry : modules.entrySet()) {
                    Module module = Main.INSTANCE.moduleManager.getModule(stringJsonElementEntry.getKey(), true);
                    if (module == null) {
                        continue;
                    }

                    if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                        JsonElement state = moduleObject.get("state");

                        if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                            module.setState(state.getAsBoolean());
                        }

                    }
                }
            }

            // Values
            JsonElement valuesElement = cfgObject.get("values");

            if (valuesElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) valuesElement).entrySet()) {
                    List<Value> values = Main.INSTANCE.valueManager.getAllValuesFrom(stringJsonElementEntry.getKey());

                    if (values == null) {
                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {
                        continue;
                    }

                    JsonObject valueObject = (JsonObject) stringJsonElementEntry.getValue();

                    for (Value value : values) {
                        try {
                            value.fromJsonObject(valueObject);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            JsonElement keyBindsElement = cfgObject.get("keybinds");

            if (keyBindsElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) keyBindsElement).entrySet()) {
                    List<Value> values = Main.INSTANCE.valueManager.getAllValuesFrom(stringJsonElementEntry.getKey());

                    if (values == null) {
                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {
                        continue;
                    }

                    JsonObject valueObject = (JsonObject) stringJsonElementEntry.getValue();

                    for (Value value : values) {
                        if (values instanceof NumberValue) {
                            NumberValue numberValue = (NumberValue) values;
                            if (numberValue.getObject() instanceof Integer) {
                                Main.INSTANCE.valueManager.numberPick.put(value, ((Number) numberValue.getObject()).intValue());
                            } else if (numberValue.getObject() instanceof Float) {
                                Main.INSTANCE.valueManager.numberPick.put(value, ((Number) numberValue.getObject()).floatValue());
                            } else {
                                Main.INSTANCE.valueManager.numberPick.put(value, ((Number) numberValue.getObject()).doubleValue());
                            }
                        }
                        if (values instanceof ModeValue) {
                            Main.INSTANCE.valueManager.modeSelect.put(value, ((ModeValue) values).getObject());
                        }

                        Main.INSTANCE.valueManager.keyBind.put(value, valueObject.get(value.getName()).getAsJsonObject().get("keybind").getAsInt());
                    }
                }
            }


            ChatUtils.info("Loaded config: " + name);
        } catch (FileNotFoundException e) {
            Main.INSTANCE.println(e.getMessage());
        }

    }

    public void load() {
        if (!saveFile.exists() || !modulesFile.exists() || !valuesFile.exists()) return;

        List<String> backupReasons = new ArrayList<>();

        try {
            JsonObject clientObject = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(saveFile)));
            JsonObject modulesObject = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(modulesFile)));
            JsonObject valuesObject = (JsonObject) new JsonParser().parse(new InputStreamReader(new FileInputStream(valuesFile)));

            // Metadata
            if (clientObject.has("metadata")) {
                JsonElement metadataElement = clientObject.get("metadata");

                if (metadataElement instanceof JsonObject) {
                    JsonObject metadata = (JsonObject) metadataElement;

                    JsonElement clientVersion = metadata.get("clientVersion");

                    if (clientVersion != null && clientVersion.isJsonPrimitive() && ((JsonPrimitive) clientVersion).isNumber()) {
                        double version = clientVersion.getAsDouble();

                        if (version > Main.CLIENT_VERSION_NUMBER) {
                            backupReasons.add("Version number of save file (" + version + ") is higher than " + Main.CLIENT_VERSION_NUMBER);
                        }

                        if (version < Main.CLIENT_VERSION_NUMBER) {
                            backupReasons.add("Version number of save file (" + version + ") is lower than " + Main.CLIENT_VERSION_NUMBER);
                        }
                    } else {
                        backupReasons.add("'clientVersion' object is not valid.");
                    }
                } else {
                    backupReasons.add("'metadata' object is not valid.");
                }

            } else {
                backupReasons.add("Save file has no metadata");
            }

            // Modules
            JsonElement modulesElement = modulesObject.get("modules");

            if (modulesElement instanceof JsonObject) {
                JsonObject modules = (JsonObject) modulesElement;

                for (Map.Entry<String, JsonElement> stringJsonElementEntry : modules.entrySet()) {
                    Module module = Main.INSTANCE.moduleManager.getModule(stringJsonElementEntry.getKey(), true);

                    if (module == null) {
                        backupReasons.add("Module '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (stringJsonElementEntry.getValue() instanceof JsonObject) {
                        JsonObject moduleObject = (JsonObject) stringJsonElementEntry.getValue();

                        JsonElement state = moduleObject.get("state");

                        if (state instanceof JsonPrimitive && ((JsonPrimitive) state).isBoolean()) {
                            module.setState(state.getAsBoolean());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/state' isn't valid");
                        }

                        JsonElement keybind = moduleObject.get("keybind");

                        if (keybind instanceof JsonPrimitive && ((JsonPrimitive) keybind).isNumber()) {
                            module.setKeybind(keybind.getAsInt());
                        } else {
                            backupReasons.add("'" + stringJsonElementEntry.getKey() + "/keybind' isn't valid");
                        }
                    } else {
                        backupReasons.add("Module object '" + stringJsonElementEntry.getKey() + "' isn't valid");
                    }
                }
            } else {
                backupReasons.add("'modules' object is not valid");
            }

            // Values
            JsonElement valuesElement = valuesObject.get("values");

            if (valuesElement instanceof JsonObject) {
                for (Map.Entry<String, JsonElement> stringJsonElementEntry : ((JsonObject) valuesElement).entrySet()) {
                    List<Value> values = Main.INSTANCE.valueManager.getAllValuesFrom(stringJsonElementEntry.getKey());

                    if (values == null) {
                        backupReasons.add("Value owner '" + stringJsonElementEntry.getKey() + "' doesn't exist");
                        continue;
                    }

                    if (!stringJsonElementEntry.getValue().isJsonObject()) {
                        backupReasons.add("'values/" + stringJsonElementEntry.getKey() + "' is not valid");
                        continue;
                    }

                    JsonObject valueObject = (JsonObject) stringJsonElementEntry.getValue();

                    for (Value value : values) {
                        try {
                            value.fromJsonObject(valueObject);
                        } catch (Exception e) {
                            backupReasons.add("Error while applying 'values/" + stringJsonElementEntry.getKey() + "' " + e);
                        }
                    }
                }
            } else {
                backupReasons.add("'values' is not valid");
            }

            if (backupReasons.size() > 0) {
                backup(backupReasons);
            }
        } catch (FileNotFoundException e) {
            Main.INSTANCE.println(e.getMessage());
        }
    }

    private void backup(@NotNull List<String> backupReasons) {
        Main.INSTANCE.logger.warn("Creating backup " + backupReasons);

        try {
            backupDir.mkdirs();
            scriptsDir.mkdirs();
            fontDir.mkdirs();

            File out = new File(backupDir, "backup_" + System.currentTimeMillis() + ".zip");
            out.createNewFile();

            StringBuilder reason = new StringBuilder();

            for (String backupReason : backupReasons) {
                reason.append("- ").append(backupReason).append("\n");
            }

            ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(out));

            outputStream.putNextEntry(new ZipEntry("client.json"));
            outputStream.putNextEntry(new ZipEntry("modules.json"));
            outputStream.putNextEntry(new ZipEntry("values.json"));
            Files.copy(saveFile, outputStream);
            outputStream.closeEntry();

            outputStream.putNextEntry(new ZipEntry("reason.txt"));
            outputStream.write(reason.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.closeEntry();

            outputStream.close();
        } catch (Exception e) {
            Main.INSTANCE.logger.error("Failed to backup");
            Main.INSTANCE.println(e.getMessage());
        }
    }


}
