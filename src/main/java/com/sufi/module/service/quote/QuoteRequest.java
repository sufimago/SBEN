package com.sufi.module.service.quote;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QuoteRequest {
    private String keyOption;

    public QuoteRequest(String keyOption) {
        this.keyOption = keyOption;
    }

}
