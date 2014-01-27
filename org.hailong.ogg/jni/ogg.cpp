#include <jni.h>
#include <android/log.h>


JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved){
	__android_log_print(ANDROID_LOG_DEBUG,"hailong","ogg JNI_OnLoad");
	return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved){
	__android_log_print(ANDROID_LOG_DEBUG,"hailong","ogg JNI_OnUnload");
}
