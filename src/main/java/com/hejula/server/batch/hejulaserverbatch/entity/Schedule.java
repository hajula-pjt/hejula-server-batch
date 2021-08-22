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
@Table(name = "schedule")
public class Schedule {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long scheduleSeq;

//  @ManyToOne(targetEntity = Accommodation.class)
//  @JoinColumn(name = "accommodation_seq")
  @Column(nullable = false)
  private long accommodationSeq;

//  @ManyToOne(targetEntity = Customer.class)
//  @JoinColumn(name = "customer_seq")
  @Column(nullable = false)
  private long customerSeq;

  @Column(nullable = false)
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  @Temporal(TemporalType.DATE)
  private Date checkinDate;

  @Column(nullable = false)
//  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
  @Temporal(TemporalType.DATE)
  private Date checkoutDate;

  @Column(nullable = false)
  private long adult;

  @Column(nullable = false)
  private long children;

  private String reservationNo;




  /*
  양방향을 설정해줬을 경우에 주인(Accommodation-Schedule의 경우 Schedule)과 반대에 둘다 넣어 줘야 함.
  그 이유? flush, clear를 한 후에 accommodataion.getSchedule()를 해올 경우에는 올바르게 데이터가 들어오지만,
  그렇지 않으면 캐시에서 값을 가져오기 때문에 update 된 값을 가져 올 수 없음
  setAccommodation 대신 changeAccommodation을 사용할
  setAccommodation로 쓸 경우 기존 setter, getter와 헛갈릴 수 있기 때문에 이름을 바꿈
  */
 /* public void changeAccommodation(Accommodation accommodation){
    this.accommodation = accommodation;
    accommodation.getSchedules().add(this);
  }*/

}
