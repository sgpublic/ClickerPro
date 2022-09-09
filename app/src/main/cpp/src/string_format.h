//
// Created by 33006 on 2022/9/9.
//
#include <string>

#ifndef CLICKERPRO_STRRING_FORMAT_H
#define CLICKERPRO_STRRING_FORMAT_H

using namespace std;

#define format formater()

class formater {
public:
    /**
     * 格式化一个指令
     * @tparam Args 参数类型
     * @param format 待格式化的指令
     * @param args 格式化参数
     * @return 已格式化的指令
     */
    template<typename ... Args>
    static const char* of(const char* str, Args ... args) {
        size_t length = snprintf(nullptr, 0, str, args...);
        if (length <= 0) return "";

        char* buf = new char[length + 1];
        snprintf(buf, length + 1, str, args...);

        return buf;
    }
};

#endif //CLICKERPRO_STRRING_FORMAT_H

