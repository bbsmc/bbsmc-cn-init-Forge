package moe.ytonidc.ytongame_hostingmenu.client;

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

    public HostingPackage(String name, String processor, String memory,
                          int defaultBackupSlots, int maxBackupSlots,
                          String storage, String recommendedPlayers, int price, int color) {
        this.name = name;
        this.processor = processor;
        this.memory = memory;
        this.defaultBackupSlots = defaultBackupSlots;
        this.maxBackupSlots = maxBackupSlots;
        this.storage = storage;
        this.recommendedPlayers = recommendedPlayers;
        this.price = price;
        this.color = color;
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

    public static final HostingPackage[] ALL_PACKAGES = {
        new HostingPackage(
            "入门型",
            "AMD EPYC 7R13 3.6GHz",
            "8G",
            1, 1,
            "30G",
            "2-3人",
            58,
            0xFF888888  // 灰色
        ),
        new HostingPackage(
            "标准型",
            "Intel Core I7-14700K / AMD Ryzen9 9950X",
            "10G",
            1, 2,
            "30G",
            "4-6人",
            88,
            0xFF5555FF  // 蓝色
        ),
        new HostingPackage(
            "灵活型",
            "Intel Core I7-14700K / AMD Ryzen9 9950X",
            "12G",
            1, 2,
            "30G",
            "6-8人",
            108,
            0xFF55FF55  // 绿色
        ),
        new HostingPackage(
            "悦享型",
            "Intel Core I7-14700K / AMD Ryzen9 9950X",
            "18G",
            1, 3,
            "30G",
            "9-12人",
            168,
            0xFFAA55FF  // 紫色
        ),
        new HostingPackage(
            "曜石型",
            "Intel Core I7-14700K / AMD Ryzen9 9950X",
            "24G",
            1, 5,
            "30G",
            "12-15人",
            238,
            0xFFFFAA00  // 金色
        )
    };
}
