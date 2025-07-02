package xyz.tryfle.capeinjector.bus

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class EventBus {

    private val subscribers = ConcurrentHashMap<Class<*>, CopyOnWriteArrayList<Subscriber>>()

    fun subscribe(listener: Any) {
        listener.javaClass.declaredMethods.forEach { method ->
            method.getAnnotation(Subscribe::class.java)?.let {
                val parameterTypes = method.parameterTypes
                if (parameterTypes.size != 1) {
                    throw IllegalArgumentException("Method ${method.name} has @Subscribe annotation but requires ${parameterTypes.size} arguments. It should require a single argument.")
                }

                val eventType = parameterTypes[0]
                subscribers.computeIfAbsent(eventType) { CopyOnWriteArrayList() }.add(Subscriber(listener, method))
            }
        }
    }

    fun unsubscribe(listener: Any) {
        subscribers.values.forEach { it.removeIf { subscriber -> subscriber.listener == listener } }
    }

    fun post(event: Any) {
        subscribers[event.javaClass]?.forEach { it.invoke(event) }
    }
}

data class Subscriber(val listener: Any, val method: Method) {
    fun invoke(event: Any) {
        method.invoke(listener, event)
    }
}