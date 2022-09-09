#include <jni.h>
#include <cstdlib>
#include <string>
#include <random>
#include <unordered_map>
#include <unistd.h>
#include "clicker_thread.h"

using namespace std;

static unordered_map<jstring, clicker_thread*> map;

/**
 * 停止点击任务
 * @param id 任务 id
 */
static void stopTask(jstring id) {
    if (map.find(id) == map.end()) {
        return;
    }
    map[id]->stop();
    map.erase(id);
}

extern "C"
/**
 * 开始连续点击任务
 * @param x 基础 x 坐标
 * @param y 基础 y 坐标
 * @param id 任务 id
 * @param root 是否使用 root
 */
JNIEXPORT void JNICALL
Java_io_github_clickerpro_core_util_Clicker2_startClick(JNIEnv *env, jobject thiz, jint x, jint y,
                                                        jstring id, jboolean root) {
    // 先停止现有任务再创建新的任务
    stopTask(id);
    auto* thread = new clicker_thread(x, y, root);
    map.insert(pair<jstring, clicker_thread*>(id, thread));
    thread->start();
}

extern "C"
/**
 * 停止点击任务
 * @param id 任务 id
 */
JNIEXPORT void JNICALL
Java_io_github_clickerpro_core_util_Clicker2_stopClick(JNIEnv *env, jobject thiz, jstring id) {
    stopTask(id);
}

extern "C"
/** 清除所有点击任务 */
JNIEXPORT void JNICALL
Java_io_github_clickerpro_core_util_Clicker2_clear(JNIEnv *env, jobject thiz) {
    for (auto &v : map) {
        stopTask(v.first);
    }
    map.clear();
}