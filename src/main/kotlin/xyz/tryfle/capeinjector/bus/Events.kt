package xyz.tryfle.capeinjector.bus

class GameInitEvent : EventType()
class TickEvent : EventType()
class Render3DEvent(val deltaTime: Float, val state: RenderState) : EventType()
class Render2DEvent(val deltaTime: Float, val state: RenderState) : EventType()

enum class RenderState {
    PRE, POST
}