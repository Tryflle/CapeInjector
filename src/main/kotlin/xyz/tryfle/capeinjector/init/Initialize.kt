package xyz.tryfle.capeinjector.init

import org.spongepowered.asm.launch.MixinBootstrap
import org.spongepowered.asm.mixin.MixinEnvironment
import org.spongepowered.asm.mixin.Mixins
import org.spongepowered.asm.mixin.transformer.IMixinTransformer
import xyz.tryfle.capeinjector.CapeInjector
import xyz.tryfle.capeinjector.feature.legacy.Test
import xyz.tryfle.capeinjector.mixin.api.CapeInjectorMixinService
import xyz.tryfle.capeinjector.transformer.common.ClientMixinTransformer

object Initialize {

    fun init1_8() {
        println("[CapeInjector] Initializing for 1.8.9")
        TinyParser.parseTiny("mappings/1_8/mappings.tiny")

        setupMixinService()
        initMixin("mixins.capeinjector.json")

        CapeInjector.globalInst.addTransformer(ClientMixinTransformer())

        CapeInjector.bus.subscribe(Test())
    }

    private fun setupMixinService() {
        try {
            System.setProperty("mixin.service", "xyz.tryfle.capeinjector.mixin.api.CapeInjectorMixinService")
            System.setProperty("mixin.env", "CLIENT")
//            System.setProperty("mixin.debug", "true")
//            System.setProperty("mixin.debug.export", "true")
//            System.setProperty("mixin.debug.verbose", "true")

            System.setProperty("mixin.env.obf", "true")
            System.setProperty("mixin.env.disableRefMap", "true")
            System.setProperty("mixin.env.remapRefMap", "false")


            println("[CapeInjector] Setting up Mixin service")
        } catch (e: Exception) {
            println("[CapeInjector] Error setting up Mixin service:")
            e.printStackTrace()
        }
    }

    private fun initMixin(cf: String) {
        try {

            MixinBootstrap.init()

            val env = MixinEnvironment.getDefaultEnvironment()

            env.side = MixinEnvironment.Side.CLIENT

            val configStream = this::class.java.classLoader.getResourceAsStream(cf)
            if (configStream == null) {
                println("[CapeInjector] ERROR: Mixin config file not found: $cf")
                return
            } else {
                configStream.close()
            }

            Mixins.addConfiguration(cf)

            try {
                val configs = Mixins.getConfigs()
                println("[CapeInjector] Registered configurations: ${configs.size}")
                for (config in configs) {
                    println("[CapeInjector] Config: ${config.name} - Parent: ${config.parent}")
                }
            } catch (e: Exception) {
                println("[CapeInjector] Could not list Mixin configs: ${e.message}")
            }

            try {
                val currentPhase = env.phase

                if (currentPhase != MixinEnvironment.Phase.INIT) {
                    val phaseField = MixinEnvironment::class.java.getDeclaredField("phase")
                    phaseField.isAccessible = true
                    phaseField.set(env, MixinEnvironment.Phase.INIT)
                }
            } catch (e: Exception) {
                println("[CapeInjector] Could not manage phase transition: ${e.message}")
            }

            val transformer = getOrCreateMixinTransformer(env)
            if (transformer != null) {
                CapeInjectorMixinService.setTransformer(transformer)
            } else {
                println("[CapeInjector] Failed to initialize Mixin transformer - will continue without it")
            }

        } catch (e: Exception) {
            println("[CapeInjector] Error initializing Mixin:")
            e.printStackTrace()
        }
    }

    private fun getOrCreateMixinTransformer(env: MixinEnvironment): IMixinTransformer? {
        try {
            val transformerField = MixinEnvironment::class.java.getDeclaredField("transformer")
            transformerField.isAccessible = true
            var transformer = transformerField.get(env) as? IMixinTransformer

            if (transformer != null) {
                println("[CapeInjector] Found existing transformer")
                return transformer
            }

            try {
                val getTransformerMethod = MixinEnvironment::class.java.getDeclaredMethod("getActiveTransformer")
                getTransformerMethod.isAccessible = true
                transformer = getTransformerMethod.invoke(env) as? IMixinTransformer
                if (transformer != null) {
                    println("[CapeInjector] Got transformer via getActiveTransformer")
                    return transformer
                }
            } catch (e: Exception) {
                println("[CapeInjector] Method 1 failed: ${e.message}")
            }

            try {
                val transformerClass = Class.forName("org.spongepowered.asm.mixin.transformer.MixinTransformer")

                try {
                    val constructor = transformerClass.getDeclaredConstructor(MixinEnvironment::class.java)
                    constructor.isAccessible = true
                    transformer = constructor.newInstance(env) as IMixinTransformer
                } catch (e: Exception) {
                    val constructor = transformerClass.getDeclaredConstructor()
                    constructor.isAccessible = true
                    transformer = constructor.newInstance() as IMixinTransformer
                    println("[CapeInjector] Created transformer with default constructor")
                }

                transformerField.set(env, transformer)

                return transformer
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                val selectMethod = env.javaClass.getDeclaredMethod("select")
                selectMethod.isAccessible = true
                selectMethod.invoke(env)

                transformer = transformerField.get(env) as? IMixinTransformer
                if (transformer != null) {
                    return transformer
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        } catch (e: Exception) {
            println("[CapeInjector] Warning: Could not get Mixin transformer: ${e.message}")
            e.printStackTrace()
            return null
        }
    }
}