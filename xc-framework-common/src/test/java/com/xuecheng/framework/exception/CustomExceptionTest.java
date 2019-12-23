package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResultCode;

/**
 * @Auther: likui
 * @Date: 2019/12/15 09:55
 * @Description:
 */
public class CustomExceptionTest {

    public static void main(String[] args) {
        ResultCode resultCode=CommonCode.UNAUTHENTICATED;
        CustomException customException=new CustomException(resultCode);
        String message = customException.getMessage();
    }
}
