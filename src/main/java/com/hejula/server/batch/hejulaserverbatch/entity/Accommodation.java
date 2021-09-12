package com.hejula.server.batch.hejulaserverbatch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@ToString(exclude = "schedules")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "accommodation")
public class Accommodation implements Serializable {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long accommodationSeq;

//  @ManyToOne(targetEntity = Admin.class) //Accommodation(N) - Admin(1) 관계
//  @JoinColumn(name = "admin_seq")
//  private Admin admin;

  @Column(nullable = false)
  private long adminSeq;

//  @ManyToOne(targetEntity = Gu.class)
//  @JoinColumn(name = "gu_seq")
//  private Gu gu;
@Column(nullable = false)
private long guSeq;

  private String name;

  private String information;

  private String address;

  private String detailAddress;

  private String postalCode;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
  @Temporal(TemporalType.TIME)
  private Date checkinTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "Asia/Seoul")
  @Temporal(TemporalType.TIME)
  private Date checkoutTime;

  private String selfCheckinWay;

  private double rating;

  @ColumnDefault(value = "0")
  private long views;

  @ColumnDefault(value = "0")
  private long visitors;

  @ColumnDefault(value = "0")
  private long max;

  @ColumnDefault(value = "0")
  private long bedroom;

  @ColumnDefault(value = "0")
  private long bathroom;

  @Column(nullable = false)
  private long xCoordinate;

  @Column(nullable = false)
  private long yCoordinate;

  private double yearAveragePrice; //연 평균 가격

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd HH:mm:ss", timezone = "Asia/Seoul")
  private Date registDate;


}
