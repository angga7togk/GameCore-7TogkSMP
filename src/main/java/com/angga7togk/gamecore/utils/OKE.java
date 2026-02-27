package com.angga7togk.gamecore.utils;

import javax.annotation.Nullable;

/**
 * 可携带异常信息的的结果
 *
 * @param <E> the message parameter
 */
public record OKE<E>(boolean ok, @Nullable E message) {

    public OKE(boolean ok){
        this(ok, null);
    }
    
    public OKE(){
        this(true, null);
    }
    
    public static <E> OKE<E> ok(E message) {
        return new OKE<>(true, message);
    }

    public static <E> OKE<E> fail(E message) {
        return new OKE<>(false, message);
    }   
}