#ifndef KOOLA_H
#define KOOLA_H

#include <jni.h>
#include <dlfcn.h>
#include <stdio.h>
#include <string.h>
#include <stdint.h>

// Hook function type
typedef int (*HookFunType)(void *func, void *replace, void **backup);

// Unhook function type
typedef int (*UnhookFunType)(void *func);

// Native module loaded callback type
typedef void (*NativeOnModuleLoaded)(const char *name, void *handle);

// Native API structure
typedef struct {
    uint32_t version;
    HookFunType hook_func;
    UnhookFunType unhook_func;
} NativeAPIEntries;

// Native init function pointer type
typedef NativeOnModuleLoaded (*NativeInit)(const NativeAPIEntries *entries);

// Function declarations
extern "C" [[gnu::visibility("default")]] [[gnu::used]]
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries);

#endif // KOOLA_H