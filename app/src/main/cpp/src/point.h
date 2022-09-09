//
// Created by 33006 on 2022/9/9.
//
#include <random>

#ifndef CLICKERPRO_POINT_H
#define CLICKERPRO_POINT_H

using namespace std;

/** 连续点击坐标，自动计算偏移量 */
class point {
public:
    point(int x, int y) {
        this->x = x;
        this->y = y;
    }

    /** 随机偏移后的 x 坐标 */
    int getX() {
        return x + randOffset();
    }

    /** 随机偏移后的 y 坐标 */
    int getY() {
        return y + randOffset();
    }
private:
    int x;
    int y;

    // 随机偏移 [-5, 5]
    random_device rd;
    mt19937 eng = mt19937(rd());
    uniform_int_distribution<int> dist = uniform_int_distribution<int>(-5, 5);

    /** 创建随机偏移量 */
    int randOffset() {
        return dist(eng);
    }
};

/** 传递给线程的参数 */
struct arg_point {
    int x; int y; bool root;
};

#endif //CLICKERPRO_POINT_H
