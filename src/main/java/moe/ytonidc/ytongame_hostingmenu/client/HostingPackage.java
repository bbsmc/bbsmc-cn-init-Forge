package moe.ytonidc.ytongame_hostingmenu.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import moe.ytonidc.ytongame_hostingmenu.Ytongame_hostingmenu;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HostingPackage {
    private final String name;
    private final String processor;
    private final String memory;
    private final int defaultBackupSlots;
    private final int maxBackupSlots;
    private final String storage;
    private final String recommendedPlayers;
    private final int price;
    private final int color;
    private final String tag;

    private static List<HostingPackage> ALL_PACKAGES = new ArrayList<>();

    public HostingPackage(String name, String processor, String memory,
                          int defaultBackupSlots, int maxBackupSlots,
                          String storage, String recommendedPlayers, int price, int color, String tag) {
        this.name = name;
        this.processor = processor;
        this.memory = memory;
        this.defaultBackupSlots = defaultBackupSlots;
        this.maxBackupSlots = maxBackupSlots;
        this.storage = storage;
        this.recommendedPlayers = recommendedPlayers;
        this.price = price;
        this.color = color;
        this.tag = tag;
    }

    public String getName() { return name; }
    public String getProcessor() { return processor; }
    public String getMemory() { return memory; }
    public int getDefaultBackupSlots() { return defaultBackupSlots; }
    public int getMaxBackupSlots() { return maxBackupSlots; }
    public String getStorage() { return storage; }
    public String getRecommendedPlayers() { return recommendedPlayers; }
    public int getPrice() { return price; }
    public int getColor() { return color; }
    public String getTag() { return tag; }

    public static List<HostingPackage> getAllPackages() {
        return ALL_PACKAGES;
    }

    public static void loadFromResources() {
        try {
            InputStream is = HostingPackage.class.getResourceAsStream("/hosting_packages.json");
            if (is != null) {
                loadFromStream(is);
                Ytongame_hostingmenu.LOGGER.info("Loaded {} hosting packages from resources", ALL_PACKAGES.size());
            } else {
                Ytongame_hostingmenu.LOGGER.error("Could not find hosting_packages.json in resources");
                loadDefaultPackages();
            }
        } catch (Exception e) {
            Ytongame_hostingmenu.LOGGER.error("Failed to load hosting packages from resources", e);
            loadDefaultPackages();
        }
    }

    public static void loadFromStream(InputStream is) {
        try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray packagesArray = root.getAsJsonArray("packages");

            List<HostingPackage> packages = new ArrayList<>();
            for (JsonElement element : packagesArray) {
                JsonObject obj = element.getAsJsonObject();

                String name = obj.get("name").getAsString();
                String processor = obj.get("processor").getAsString();
                String memory = obj.get("memory").getAsString();
                int defaultBackupSlots = obj.get("defaultBackupSlots").getAsInt();
                int maxBackupSlots = obj.get("maxBackupSlots").getAsInt();
                String storage = obj.get("storage").getAsString();
                String recommendedPlayers = obj.get("recommendedPlayers").getAsString();
                int price = obj.get("price").getAsInt();

                // 解析颜色（支持 "0xFF888888" 格式）
                String colorStr = obj.get("color").getAsString();
                int color = parseColor(colorStr);

                // 解析标签（可能为 null）
                String tag = null;
                if (obj.has("tag") && !obj.get("tag").isJsonNull()) {
                    tag = obj.get("tag").getAsString();
                }

                packages.add(new HostingPackage(name, processor, memory, defaultBackupSlots,
                        maxBackupSlots, storage, recommendedPlayers, price, color, tag));
            }

            ALL_PACKAGES = packages;
        } catch (Exception e) {
            Ytongame_hostingmenu.LOGGER.error("Failed to parse hosting packages JSON", e);
            loadDefaultPackages();
        }
    }

    private static int parseColor(String colorStr) {
        try {
            if (colorStr.startsWith("0x") || colorStr.startsWith("0X")) {
                return (int) Long.parseLong(colorStr.substring(2), 16);
            } else if (colorStr.startsWith("#")) {
                return (int) Long.parseLong("FF" + colorStr.substring(1), 16);
            }
            return Integer.parseInt(colorStr);
        } catch (Exception e) {
            return 0xFFFFFFFF;
        }
    }

    private static void loadDefaultPackages() {
        ALL_PACKAGES = new ArrayList<>();
        ALL_PACKAGES.add(new HostingPackage("入门型", "AMD EPYC 7R13 3.6GHz", "8G", 1, 1, "30G", "2-3人", 58, 0xFF888888, null));
        ALL_PACKAGES.add(new HostingPackage("标准型", "Intel Core I7-14700K / AMD Ryzen9 9950X", "10G", 1, 2, "30G", "4-6人", 88, 0xFF5555FF, "热销"));
        ALL_PACKAGES.add(new HostingPackage("灵活型", "Intel Core I7-14700K / AMD Ryzen9 9950X", "12G", 1, 2, "30G", "6-8人", 108, 0xFF55FF55, null));
        ALL_PACKAGES.add(new HostingPackage("悦享型", "Intel Core I7-14700K / AMD Ryzen9 9950X", "18G", 1, 3, "30G", "9-12人", 168, 0xFFAA55FF, "多人推荐"));
        ALL_PACKAGES.add(new HostingPackage("曜石型", "Intel Core I7-14700K / AMD Ryzen9 9950X", "24G", 1, 5, "30G", "12-15人", 238, 0xFFFFAA00, null));
        Ytongame_hostingmenu.LOGGER.info("Loaded default hosting packages");
    }
}
