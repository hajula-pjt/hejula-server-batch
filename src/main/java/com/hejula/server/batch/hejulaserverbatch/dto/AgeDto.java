package com.hejula.server.batch.hejulaserverbatch.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class AgeDto {

    private long accommodationSeq;

    private String age;

    private long countAge;

}
