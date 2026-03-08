package org.springboot.pdv.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

public class ResponseDTO {

    @Getter
    private final List<String> messages;

    public ResponseDTO(List<String> messages) {
        this.messages = messages;
    }

    public ResponseDTO(String messages) {
        this.messages = Collections.singletonList(messages);
    }
}
