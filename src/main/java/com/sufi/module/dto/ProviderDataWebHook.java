package com.sufi.module.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderDataWebHook {
    private String event_type;
    private Integer listing_id;
    private String timestamp;
    private ProviderDataPayloadWebHook data;
}
