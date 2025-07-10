package com.project.common.Result;

import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

@Data
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    public Result() {}

    private Result(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }

    private Result(StatusCode statusCode, T data) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
        this.data = data;
    }

    public static <T> Result<String> success() {
        return new Result<String>(StatusCode.SUCCESS);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(StatusCode.SUCCESS, data);
    }


    public static <T> Result<T> fail() {
        return new Result<>(StatusCode.FAIL);
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = new Result<>(StatusCode.FAIL);
        result.setMessage(message);
        return result;
    }

    public static <T> Result<T> fail(StatusCode statusCode) {
        return new Result<>(statusCode);
    }

    public static <T> Result<T> fail(StatusCode statusCode, String message) {
        Result<T> result = new Result<>(statusCode);
        result.setMessage(message);
        return result;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}