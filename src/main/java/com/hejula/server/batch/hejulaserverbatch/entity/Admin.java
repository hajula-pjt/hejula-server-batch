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
@Table(name = "admin")
public class Admin {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long adminSeq;

  @Column(nullable = false)
  private String id;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String nickname;

  @Column(nullable = false)
  private long thisMonthVisitors;

  @Column(nullable = false)
  private double thisMonthSales;

  @Column(nullable = false)
  private long thisMonthRateOperation; //이번 달 가동률

}
