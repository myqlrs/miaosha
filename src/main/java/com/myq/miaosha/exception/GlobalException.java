package com.myq.miaosha.exception;

import com.myq.miaosha.result.CodeMsg;

/**
 * @author 孟赟强
 * @date 2021/4/22.
 * <p>
 * 自定义全局异常类
 */
public class GlobalException extends RuntimeException {

    private static final long servialVersionUID = 1L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
