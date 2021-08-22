package com.hejula.server.batch.hejulaserverbatch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "daily_visitors")
public class DailyVisitors {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long dailyVisitorsSeq;

//    @ManyToOne(targetEntity = Accommodation.class)
//    @JoinColumn(name = "accommodation_seq")
    @Column(nullable = false)
    private long accommodationSeq;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Temporal(TemporalType.DATE)
    private Date day;

    @Column(nullable = false)
    private int dayOfWeek;

    @Column(nullable = false)
    private long visitors;

    public DailyVisitors(long accommodationSeq, Date day, String dayOfWeek, long visitors){
        this.accommodationSeq = accommodationSeq;
        this.day = day;
        this.dayOfWeek = Integer.parseInt(dayOfWeek);
        this.visitors = visitors;
    }

}
