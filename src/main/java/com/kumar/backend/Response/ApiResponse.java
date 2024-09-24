package com.kumar.backend.Response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ApiResponse {
    private Object data;
    private int status;
    private String message;
}
