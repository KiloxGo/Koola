package cn.peyriat.koola;

public class ShadowHook {
    public static native int nativeHookSymAddr(int apiLevel);
    public static native int nativeHookSymName(int apiLevel);
    public static native int nativeUnhook();
    public static native void nativeDlopen();
    public static native void nativeDlclose();
    public static native int nativeRun();
    public static native void nativeDumpRecords(String pathname);

}
