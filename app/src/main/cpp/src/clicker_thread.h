//
// Created by sgpublic on 2022/9/9.
//

#include "point.h"
#include "string_format.h"
#include "logcat.h"

#ifndef CLICKERPRO_CLICKER_THREAD_H
#define CLICKERPRO_CLICKER_THREAD_H

class clicker_thread {
public:
    clicker_thread(int x, int y, bool root) {
        this->click = new point(x, y);
        this->root = root;
    }

    void start() {
        pthread_create(&pthread, nullptr, start_click, this);
    }

    void stop() {
        this->flag = false;
    }

private:
    point* click;
    bool root;
    bool flag = true;

    pthread_t pthread;

    static void* start_click(void* arg) {
        clicker_thread* clicker = (clicker_thread*) arg;
        if (clicker->root) {
            clicker->root_clicker();
        } else {
            clicker->input_clicker();
        }
        return nullptr;
    }

    const char* input_tap = "input tap %d %d";
    /** 使用 input 指令的点击 */
    void input_clicker() {
        while (flag) {
            int code = system(format.of(input_tap, click->getX(), click->getY()));
            if (code != 0) {

                stop();
                break;
            }
            usleep(20000);
        }
    }

    const char* root_sendevent = "sendevent /dev/input/event0 %d %d %x";
    /** 使用 root 的点击 */
    void root_clicker() {
        const char* command3 = format.of(root_sendevent, 1, 330, 1);
        const char* command4 = format.of(root_sendevent, 1, 330, 0);
        const char* command_end = format.of(root_sendevent, 1, 330, 1);
        while (flag) {
            // 设置 x 坐标
            system(format.of(root_sendevent, 3, 0, click->getX()));
            // 设置 y 坐标
            system(format.of(root_sendevent, 3, 0, click->getY()));
            // 按下
            system(command3);
            system(command_end);
            usleep(10000);
            // 抬起
            system(command4);
            system(command_end);
            usleep(30000);
        }
    }
};

#endif //CLICKERPRO_CLICKER_THREAD_H
