package xyz.tryfle.capeinjector.mixin.api

import org.spongepowered.asm.service.IGlobalPropertyService
import org.spongepowered.asm.service.IPropertyKey

class ClientMixinGlobalPropertyService : IGlobalPropertyService {

    private val props: MutableMap<IPropertyKey, Any> = HashMap()

    override fun resolveKey(name: String?): IPropertyKey = Key(name)

    override fun <T : Any?> getProperty(key: IPropertyKey): T? = props[key] as T?

    override fun setProperty(key: IPropertyKey, p1: Any): Unit {
        props[key] = p1
    }

    override fun <T : Any> getProperty(key: IPropertyKey, p1: T): T =
        props[key] as T? ?: p1

    override fun getPropertyString(
        key: IPropertyKey,
        p1: String
    ): String =
        getProperty(key, p1)

    internal class Key(name1: String?) : IPropertyKey
}