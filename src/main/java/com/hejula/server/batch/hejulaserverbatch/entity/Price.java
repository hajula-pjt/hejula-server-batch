package com.hejula.server.batch.hejulaserverbatch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@Table(name = "price")
public class Price {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long priceSeq;

/*  @JsonBackReference(value = "accAndPriceReference")
  @ManyToOne(targetEntity = Accommodation.class)
  @JoinColumn(name = "accommodation_seq")
  private Accommodation accommodation;*/
  @Column(nullable = false)
  private long accommodationSeq;

  @Column(nullable = false)
  private long month;

  @Column(nullable = false)
  private long day;

  @Column(nullable = false)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-mm-dd", timezone = "Asia/Seoul")
  private Date fullDay;

  @Column(nullable = false)
  private long price;

}
