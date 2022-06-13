package com.ginfon.core.utils;

import com.ginfon.core.model.Result;

/**
 * @author James
 */
public class ResultUtil {
    public static Result<Object> success(Object object, String message) {
        Result<Object> result = new Result<>();
        result.setCode( 1 );
        result.setMessage( message == null ? "" : message );
        result.setData( object );
        return result;
    }

    public static Result<Object> success() {
        return success( null, null );
    }

    public static Result<Object> error(Integer code, String message) {
        Result<Object> result = new Result<>();
        result.setCode( code );
        result.setMessage( message );
        result.setData( null );
        return result;
    }
}