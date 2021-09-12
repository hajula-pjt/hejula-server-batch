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
@Table(name = "review")
public class Review {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long reviewSeq;

  @Column(nullable = false)
  private long accommodationSeq;

  @Column(nullable = false)
  private long scheduleSeq;

  @Column(nullable = false)
  private double stars;

  @Column(nullable = false)
  private String comment;

  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Asia/Seoul")
  private Date registDate;


}
