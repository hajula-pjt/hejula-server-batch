package com.hejula.server.batch.hejulaserverbatch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accommodation_statistics_tag")
public class AccommodationStatisticsTag {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long statisticsTagSeq;

//  @ManyToOne(targetEntity = Statistics.class)
//  @JoinColumn(name = "statistics_seq")
  @Column(nullable = false)
  private long accommodationSeq;

  private String name;

  private int score;

}
