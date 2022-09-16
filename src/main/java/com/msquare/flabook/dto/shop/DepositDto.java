package com.msquare.flabook.dto.shop;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 쇼핑몰 사용자 예치금 정보
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true, value = ("memNm"))
@NoArgsConstructor
public class DepositDto implements Serializable {
    private String deposit;
    private String memNo;

    public BigDecimal getDeposit() {
        return new BigDecimal(deposit);
    }
}
