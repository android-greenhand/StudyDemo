#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <android/log.h>

#define  LOG_TAG    "nativeprint"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGD(...)  __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...)  __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

using namespace std;

jstring StringFromJNI(JNIEnv *ev);

JNIEXPORT jint JNI_OnLoad(JavaVM
                          *vm,
                          void *unused
) {
    const JNINativeMethod method[] = {

            {"StringFromJNI", "()Ljava/lang/String;", (jstring *) StringFromJNI}
    };

    JNIEnv *env = NULL;
    jint result = -1;


    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        LOGE("failed");
        return result;
    }

    jclass jclass1 = env->FindClass("com/example/studyApp/JNI/JNITools");
    if (jclass1 == NULL) {
        LOGE("gzp");
        return -1;
    }

    jint ret = env->RegisterNatives(jclass1, method, 1);
    if (ret < 0) {
        LOGE("gzp RegisterNatives failed %d", ret);
        return result;
    }
    LOGE("gzp RegisterNatives sucessued %d", ret);

    return JNI_VERSION_1_4;
}


jstring StringFromJNI(JNIEnv *ev) {
    std::string hello = "Hello from C++  gzp";
    return ev->NewStringUTF(hello.c_str());
}

