package cn.peyriat.koola



object NativeHook {
    init {
            System.loadLibrary("koola")
        }
    external fun initHook():Int

    external fun flytosky(state: Boolean):Int



}





