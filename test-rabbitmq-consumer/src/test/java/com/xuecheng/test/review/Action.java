package com.xuecheng.test.review;

/**
 * @Auther: likui
 * @Date: 2019/9/21 22:38
 * @Description:
 */
public enum Action {
    ACCEPT,  // 处理成功
    RETRY,   // 可以重试的错误
    REJECT,  // 无需重试的错误
}
