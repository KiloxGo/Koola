
import androidx.annotation.Keep
import java.lang.reflect.Method
open class GameEvent(val timestamp: Long = System.currentTimeMillis())
class GameUpdateEvent : GameEvent()
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class SubscribeEvent(val priority: Int = 0)

object EventBus {
    private val subscribers = mutableMapOf<Class<*>, MutableList<Subscriber>>()
    private data class Subscriber(
        val instance: Any,
        val method: Method,
        val priority: Int
    )
    fun register(subscriber: Any) {
        subscriber::class.java.declaredMethods.forEach { method ->
            method.getAnnotation(SubscribeEvent::class.java)?.let { annotation ->
                val paramType = method.parameterTypes.firstOrNull()
                    ?: throw IllegalArgumentException("Subscriber method must have exactly one parameter")
                method.isAccessible = true
                subscribers.getOrPut(paramType) { mutableListOf() }.apply {
                    add(Subscriber(subscriber, method, annotation.priority))
                    sortByDescending { it.priority }
                }
            }
        }
    }
    fun unregister(subscriber: Any) {
        subscribers.values.forEach { list ->
            list.removeAll { it.instance == subscriber }
        }
    }
    fun post(event: GameEvent) {
        subscribers[event.javaClass]?.forEach { subscriber ->
            try {
                subscriber.method.invoke(subscriber.instance, event)
            } catch (e: Exception) {
                System.err.println("Error invoking event handler: ${e.message}")
            }
        }
    }
}