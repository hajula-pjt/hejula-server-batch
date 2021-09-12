package com.hejula.server.batch.hejulaserverbatch.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class VisitorDto {

    private long accommodationSeq;

    private long adminSeq;

    private int month;

    private long visitors;

    private long reservationDays; //숙박일수

    public VisitorDto(long accommodationSeq, int month, long visitors){

        this.accommodationSeq = accommodationSeq;
        this.month = month;
        this.visitors = visitors;

    }

    public VisitorDto(long adminSeq, long visitors, long reservationDays){

        this.adminSeq = adminSeq;
        this.visitors = visitors;
        this.reservationDays = reservationDays;

    }

}
