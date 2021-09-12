package com.hejula.server.batch.hejulaserverbatch.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "statistics")
public class Statistics {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long statisticsSeq;

//  @ManyToOne(targetEntity = Admin.class)
//  @JoinColumn(name = "admin_seq")
@Column(nullable = false)
  private long adminSeq;

//  @ManyToOne(targetEntity = Accommodation.class)
//  @JoinColumn(name = "accommodation_seq")
@Column(nullable = false)
  private long accommodationSeq;

  @ColumnDefault(value = "0")
  private long teens;

  @ColumnDefault(value = "0")
  private long twenties;

  @ColumnDefault(value = "0")
  private long thirties;

  @ColumnDefault(value = "0")
  private long fourties;

  @ColumnDefault(value = "0")
  private long fifties;

  @ColumnDefault(value = "0")
  private double averageRating;

  @ColumnDefault(value = "0")
  private double bestRating;

  @ColumnDefault(value = "0")
  private long maxReviewSeq;

  @ColumnDefault(value = "0")
  private double worstRating;

  @ColumnDefault(value = "0")
  private long minReviewSeq;

  @ColumnDefault(value = "0")
  private long janVisitors;

  @ColumnDefault(value = "0")
  private long febVisitors;

  @ColumnDefault(value = "0")
  private long marVisitors;

  @ColumnDefault(value = "0")
  private long aprVisitors;

  @ColumnDefault(value = "0")
  private long mayVisitors;

  @ColumnDefault(value = "0")
  private long junVisitors;

  @ColumnDefault(value = "0")
  private long julVisitors;

  @ColumnDefault(value = "0")
  private long augVisitors;

  @ColumnDefault(value = "0")
  private long sepVisitors;

  @ColumnDefault(value = "0")
  private long octVisitors;

  @ColumnDefault(value = "0")
  private long novVisitors;

  @ColumnDefault(value = "0")
  private long decVisitors;

  private String year;

}