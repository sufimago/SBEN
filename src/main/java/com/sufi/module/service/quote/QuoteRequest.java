package com.sufi.module.service.quote;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class QuoteRequest {
    private String keyOption;

    public QuoteRequest(String keyOption) {
        this.keyOption = keyOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuoteRequest that = (QuoteRequest) o;
        return Objects.equals(keyOption, that.keyOption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyOption);
    }

}
