package com.uteexpress.util;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;
}
