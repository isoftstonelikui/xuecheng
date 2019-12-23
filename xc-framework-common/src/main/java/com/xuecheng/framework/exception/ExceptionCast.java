package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

public final class ExceptionCast {
    
    private ExceptionCast() {
    }

    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }
}
