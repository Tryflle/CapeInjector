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
            { Class.forName("net.minecraft.launchwrapper.Launch"); "Forge" },
            { Class.forName("net.minecraft.client.main.Main"); "Vanilla" }
        )



    fun tryAll(vararg checks: () -> String): String {
        for ((i, check) in checks.withIndex()) {
            try {
                val result = check()
//                println("[CI] Client detection succeeded: $result")
                return result
            } catch (t: Throwable) {
            }
        }
        return "Unknown"
    }
}