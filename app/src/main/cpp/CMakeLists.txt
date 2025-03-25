cmake_minimum_required(VERSION 3.22.1)


project("koola")
enable_language(ASM)

set(SHADOWHOOK_PATH ${CMAKE_SOURCE_DIR}/shadowhook)
set(EVENT_PATH ${CMAKE_SOURCE_DIR}/eventbus)

file(GLOB SHADOWHOOK_SOURCES
        ${SHADOWHOOK_PATH}/arch/arm64/*.c
        ${SHADOWHOOK_PATH}/common/*.c
        ${SHADOWHOOK_PATH}/include/*.c
        ${SHADOWHOOK_PATH}/third_party/bsd/*.c
        ${SHADOWHOOK_PATH}/third_party/lss/*.c
        ${SHADOWHOOK_PATH}/third_party/xdl/*.c
        ${SHADOWHOOK_PATH}/*.c
)

add_library(shadowhook STATIC ${SHADOWHOOK_SOURCES})

target_include_directories(shadowhook PRIVATE ${SHADOWHOOK_PATH}/include/shadowhook.h)

ADD_SUBDIRECTORY(${EVENT_PATH}/lib)

add_library(${CMAKE_PROJECT_NAME} SHARED
        koola.cpp
)


target_link_libraries(${CMAKE_PROJECT_NAME}
        shadowhook
        Dexode::EventBus
        android
        log
)
