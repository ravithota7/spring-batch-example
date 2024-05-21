package com.batch.example.domain;


import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Trade {

    private Long transactionId;
    private String ticker;
    private String securityDescription;
    private BigDecimal price;
    private Integer quantity;
    private String reportingCurrency;
    private BigDecimal fxRate;
    private String tradeType;
    private LocalDate transactionDate;

}
