#include "koola.h"
#include <string>
#include <stdint.h>

static HookFunType hook_func = nullptr;

// Custom variable
const char* customUserDataPath = "";

// Function backup
intptr_t (*backup)();

// Fake original function, returns custom variable
intptr_t fake() {
    return (intptr_t)customUserDataPath;
}

// Set custom path function
extern "C" JNIEXPORT void JNICALL
Java_cn_peyriat_koola_MainHook_00024KoolaNative_setCustomUserDataPath(JNIEnv *env, jobject clazz, jstring path) {
    const char *nativePath = env->GetStringUTFChars(path, 0);
    customUserDataPath = strdup(nativePath); // Use strdup to copy the string
    env->ReleaseStringUTFChars(path, nativePath);
}

// fopen function backup pointer
FILE *(*backup_fopen)(const char *filename, const char *mode);

// Fake fopen function
FILE *fake_fopen(const char *filename, const char *mode) {
    if (strstr(filename, "banned")) return nullptr; // Prevent opening files containing "banned"
    return backup_fopen(filename, mode);
}

// FindClass function backup pointer
jclass (*backup_FindClass)(JNIEnv *env, const char *name);

// Fake FindClass function
jclass fake_FindClass(JNIEnv *env, const char *name) {
    if (!strcmp(name, "dalvik/system/BaseDexClassLoader"))
        return nullptr; // Prevent loading BaseDexClassLoader class
    return backup_FindClass(env, name);
}

// Library loaded callback function
void on_library_loaded(const char *name, void *handle) {
    std::string libName(name);
    if (libName.rfind("libminecraftpe.so") == libName.length() - strlen("libminecraftpe.so")) {
        void *target = dlsym(handle, "nativeGetUserDataPath"); // Find target function
        hook_func(target, (void *) fake, (void **) &backup); // Hook target function
    }
}

// JNI_OnLoad function, initializes JNI hooks
extern "C" [[gnu::visibility("default")]] [[gnu::used]]
jint JNI_OnLoad(JavaVM *jvm, void*) {
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);

    // Hook FindClass function
    hook_func((void *)env->functions->FindClass, (void *)fake_FindClass, (void **)&backup_FindClass);
    return JNI_VERSION_1_6;
}

// native_init function, sets hooks on initialization
extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    hook_func = entries->hook_func;
    // Set system hooks, e.g., hook fopen function
    hook_func((void*) fopen, (void*) fake_fopen, (void**) &backup_fopen);

    return on_library_loaded; // Return library loaded callback function
}