package com.sufi.module.service.common;

import io.netty.util.internal.StringUtil;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class SbenDto {

    private static final Logger LOGGER = LoggerFactory.getLogger(SbenDto.class);

    @Setter(AccessLevel.NONE)
    private Long startMillis = System.currentTimeMillis();

    /**
     * platform booking code
     */
    private String bookingId;

    /**
     * Start date
     */
    private LocalDate startDate;

    /**
     * End date
     */
    private LocalDate endDate;

    /**
     * Market ("ES")
     */
    private String market;

    /**
     * Stats
     */
    private Map<String, Object> logEntry = new HashMap<>();

    /**
     * Datos
     */
    private List<SbenDataDto> rbenDatas = new ArrayList<>();

    /**
     * Map SbenDataDto
     */
    private Map<Integer, SbenDataDto> rbenDataMap = new HashMap<>();

    /**
     * HashSet activeHpvCodes
     */
    private Set<Integer> activeHpvCodes = new HashSet<>();

    /**
     * HashSet inactiveHpvCodes
     */
    private Set<Integer> inactiveHpvCodes = new HashSet<>();

    /**
     * If true RefundableType includes RF,NRF or FLEX. Else only RF or NRF
     */
    private boolean withAllRefundableTypes = false;

    /**
     * Used in booking read commission configured in booking
     */
    private BigDecimal commission = null;

    /**
     * Used in cofirm, booking read lineOfBusiness configured in logs
     */
    private String lineOfBusiness;

    /**
     * Used in cofirm, booking read Origin configured in logs
     */
    private String origin;

    /**
     * Used in avail, quote, cofirm,  UserID configured in commission partition
     */
    private String userId;

    /**
     * Used in cofirm, booking read Client configured in logs
     */
    private String client;

    /**
     * Used in cofirm, Provider-> By default we always use "RBEN"
     */
    private String platform;

    /**
     * KeyOption[6] optionType -> By default we always use "SOLO HOTEL"
     */
    private String requestType;
    /**
     * Remarks
     */
    private String remarks;

    /**
     * SLA
     */
    private boolean isSla;

    /**
     * LOGITRAVEL LOCATOR
     */
    private String logitravelLocator;

    public SbenDataDto getRbenData() {
        if (rbenDatas.isEmpty())
            rbenDatas.add(new SbenDataDto());
        return rbenDatas.getFirst();
    }

    public void updateDataStructures() {
        rbenDataMap.clear();
        activeHpvCodes.clear();
        inactiveHpvCodes.clear();

        for (SbenDataDto data : rbenDatas) {
            Integer hpvCode = data.getHpvCode();
            if (hpvCode != null) {
                rbenDataMap.put(data.getHpvCode(), data);
                if (data.isActive()) {
                    activeHpvCodes.add(data.getHpvCode());
                } else {
                    inactiveHpvCodes.add(data.getHpvCode());
                }
            }
        }
    }

    public void setRbenDatas(List<SbenDataDto> rbenDatas) {
        this.rbenDatas = rbenDatas;
        updateDataStructures();
    }

    public SbenDataDto getRbenData(Integer hpvCodigo) {
        return rbenDataMap.get(hpvCodigo);
    }

    public List<Integer> getActiveHotelHotCodes() {
        return rbenDatas.parallelStream()
                .filter(SbenDataDto::isActive)
                .map(SbenDataDto::getHotCode)
                .collect(Collectors.toList());
    }

    public boolean isEmptyActiveHotelHotCodes() {
        return rbenDatas.parallelStream().noneMatch(SbenDataDto::isActive);
    }

    public List<Integer> getInvalidHotelHotCodes() {
        return rbenDatas.parallelStream()
                .filter(d -> !d.isActive())
                .map(SbenDataDto::getHotCode)
                .collect(Collectors.toList());
    }

    public List<Integer> getActiveHotelHpvCodes() {
        return new ArrayList<>(activeHpvCodes);
    }

    public List<String> getActiveHotelHpvCodesStr() {
        if (!activeHpvCodes.isEmpty()) {
            return activeHpvCodes.stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return null;
    }

    public String getKeyOption(SbenDataDto data, String roomHash, String serviceHash) {
        return String.format("%s|%s|%s|%s|%s",
                data.getHotCode(),                          // 0 hotelCode
                getMarket(),                                // 1 Market
                data.getHpvCode(),                          //2 hotelPlatform (hpvCode)
                roomHash,                                   //3 roomsHash
                serviceHash,                                //4 PoisHash / unused
                StringUtil.EMPTY_STRING                     //5 specific clients / unused
        );
    }

    public void setFromKeyOption(String keyOption, SbenDataDto data) {
        String[] keys = keyOption.split("\\|");
        int i = 0;
        for (String key : keys) {
            if (i == 0) {        // 0 hotelCode ---------------------------------
                Integer hotCode = Integer.valueOf(key);
                data.setHotCode(hotCode);
            } else if (i == 2) {  // 2 nights ------------------------------------
                int nights = Integer.parseInt(key);
                LocalDate expectedFinalDate = LocalDate.of(getStartDate().getYear(), getStartDate().getMonthValue(), getStartDate().getDayOfMonth());
                expectedFinalDate = expectedFinalDate.plusDays(nights);
                setEndDate(expectedFinalDate);
            } else if (i == 4) {  // 4 Market ---------------------------------
                setMarket(key);
            } else if (i == 14) { //14 hotelPlatform -> hpvCode -> we take it from cache
                data.setHpvCode(Integer.valueOf(key));
            } else if (i == 15) { //15 roomsHash     -> By default we always use 6297
            } else if (i == 21) {
                break;
            }
            i++;
        }
    }
}
