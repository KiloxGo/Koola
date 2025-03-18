package cn.peyriat.koola

class GameStatus {
    class Vector3f {
        var x: Float = 0f
        var y: Float = 0f
        var z: Float = 0f

        constructor(x: Float, y: Float, z: Float) {
            this.x = x
            this.y = y
            this.z = z
        }

        constructor() {
            this.x = 0f
            this.y = 0f
            this.z = 0f
        }
    }
    class StateVectorComp {
        var currentPos: Vector3f = Vector3f()
        var eyePos: Vector3f = Vector3f()
        var velocity: Vector3f = Vector3f()
    }
    class AABBShapeComp {
        var lowerPos: Vector3f = Vector3f()
        var upperPos: Vector3f = Vector3f()
        var width: Float = 0f
        var height: Float = 0f
    }
    class RotationComp {
        var pitch: Float = 0f
        var yaw: Float = 0f
        var pitchFake: Float = 0f
        var yawFake: Float = 0f
    }
    class PlayerStatus {
        var stateVectorComp: StateVectorComp = StateVectorComp()
        var aabbShapeComp: AABBShapeComp = AABBShapeComp()
        var rotationComp: RotationComp = RotationComp()
    }
}
