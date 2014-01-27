LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/include

LOCAL_MODULE    := ogg
LOCAL_SRC_FILES := ogg.cpp src/framing.c src/bitwise.c

include $(BUILD_SHARED_LIBRARY)
