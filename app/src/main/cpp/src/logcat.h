//
// Created by 33006 on 2022/9/9.
//

#include <android/log.h>
#include "string_format.h"

#ifndef CLICKERPRO_LOGCAT_H
#define CLICKERPRO_LOGCAT_H

static const char* format_tag(const char* file_name, const char* file, int line) {
    return format.of("%s (%s:%d)", file_name, file, line);
}

class Log {
    static void d(const char* file_name, const char* file, int line, const char* msg) {
        __android_log_print(ANDROID_LOG_DEBUG, format_tag(file_name, file, line), "%s", msg);
    }
    static void i(const char* file_name, const char* file, int line, const char* msg) {
        __android_log_print(ANDROID_LOG_INFO, format_tag(file_name, file, line), "%s", msg);
    }
    static void e(const char* file_name, const char* file, int line, const char* msg) {
        __android_log_print(ANDROID_LOG_ERROR, format_tag(file_name, file, line), "%s", msg);
    }
};

#endif //CLICKERPRO_LOGCAT_H