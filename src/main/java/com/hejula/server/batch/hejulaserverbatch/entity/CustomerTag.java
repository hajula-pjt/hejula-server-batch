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
@Table(name = "customer_tag")
public class CustomerTag {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long customerTagSeq;

//  @ManyToOne(targetEntity = Customer.class)
//  @JoinColumn(name = "customer_seq")
  @Column(nullable = false)
  private long customerSeq;

  private String name;

}
