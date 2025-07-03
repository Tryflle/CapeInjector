package xyz.tryfle.capeinjector.init.common

object Detector {

    fun detectVersion(): String {
        val versionString = try {
            // 1.7.2 --- 1.12.2
            Class
                .forName("net.minecraft.realms.RealmsSharedConstants")
                .getField("VERSION_STRING")
                .get(null) as String
        } catch (_: ClassNotFoundException) {
            "Unknown"
        }
        return versionString
    }


    fun detectClient(): String =
        tryAll(
            {
                try {
                    Class.forName("com.moonsworth.lunar.genesis.Genesis")
                } catch (_: Throwable) {
                    Class.forName("wtf.zani.launchwrapper.LunarLaunchWrapperKt")
                }
                "Lunar"
            },
            { Class.forName("net.badlion.client.Wrapper"); "Baldlion" },
            { Class.forName("net.fabricmc.loader.impl.launch.knot.KnotClient"); "Fabric" },
            { Class.forName("net.minecraftforge.fml.loading.FMLLoader"); "Forge" },
            { Class.forName("net.minecraft.client.main.Main"); "Vanilla" }
        )



    fun tryAll(vararg checks: () -> String): String {
        for (check in checks) {
            try {
                return check()
            } catch (_: Throwable) {
            }
        }
        return "Unknown"
    }
}