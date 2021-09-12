package com.hejula.server.batch.hejulaserverbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class PriceDto {

    private long accommodationSeq;

    private double total;

}
