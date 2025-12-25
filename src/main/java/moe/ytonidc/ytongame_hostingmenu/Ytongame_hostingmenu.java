package moe.ytonidc.ytongame_hostingmenu;

import com.mojang.logging.LogUtils;
import moe.ytonidc.ytongame_hostingmenu.client.HostingPackage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Ytongame_hostingmenu.MODID)
public class Ytongame_hostingmenu {
    public static final String MODID = "ytongame_hostingmenu";
    public static final ResourceLocation hostingLogo = new ResourceLocation(MODID, "textures/gui/logo_ytongame.png");
    public static final Logger LOGGER = LogUtils.getLogger();

    public Ytongame_hostingmenu() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);

        // 异步加载套餐数据（优先远程，失败则本地）
        HostingPackage.loadAsync();
    }
}
