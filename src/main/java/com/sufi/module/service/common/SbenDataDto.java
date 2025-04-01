package com.sufi.module.service.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@SuppressWarnings({"javaarchitecture:S7027", "unused", "java:S3358"})
public class SbenDataDto {

    private Integer hotCode;

    private Integer hpvCode;

    private Integer roomType;

    private String roomTypeDesc;

    private boolean active = true;

    private boolean available;

    @Setter(AccessLevel.NONE)
    private BigDecimal sellPrice = BigDecimal.valueOf(0);

    @Setter(AccessLevel.NONE)
    private BigDecimal netPrice = BigDecimal.valueOf(0);

    private BigDecimal commission = null;

    private BigDecimal sellPriceBase;

    private String countryOfHotel;

    private boolean rc = false;

    private Integer paymentSociety;

    private String billingProvider;

    public void setSellPrice(BigDecimal sellPrice) {
        this.sellPrice = sellPrice;
        if (sellPrice != null) {
            if (Objects.isNull(getCommission()))
                throw new ArithmeticException("Please set the commission");

            this.netPrice = sellPrice.multiply(BigDecimal.ONE.subtract(getCommission()));
        }
    }

    public float getSellPriceFloat() {
        return sellPrice == null ? 0.0f : sellPrice.floatValue();
    }

    public float getNetPriceFloat() {
        return netPrice == null ? 0.0f : netPrice.floatValue();
    }

    public float getCommissionFloat() {
        return getCommission().floatValue();
    }

    public String getHpvCodeStr() {
        if (hpvCode == null) return null;
        return hpvCode.toString();
    }

    public String getHotCodeStr() {
        if (hotCode == null) return null;
        return hotCode.toString();
    }
}
