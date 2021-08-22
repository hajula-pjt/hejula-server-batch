package com.hejula.server.batch.hejulaserverbatch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "custom_dates")
public class CustomDates {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long customDatesSeq;

    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd", timezone = "Asia/Seoul")
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fullDay;

    @Column(nullable = false)
    private String year;

    @Column(nullable = false)
    private String month;

    @Column(nullable = false)
    private String dayOfWeek;
}
