package com.cwy.remotecontrol.Exception;

/**
 * Created by cwy on 2016/5/5.
 */
public class MyException extends Exception {

    private String msg = null;
    public MyException() {
    }

    /**
     * 构造一个有参的异常对象来携带消息传到上一级
     * @param msg
     */
    public MyException(String msg) {
        this.msg = msg;
    }
    @Override
    public String getMessage() {
        return this.msg;
    }
}
