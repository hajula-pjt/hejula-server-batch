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
@Table(name = "gu")
public class Gu {

  @Id
  @GeneratedValue(strategy =  GenerationType.IDENTITY)
  private long guSeq;

  @Column(nullable = false)
  private String name;

  @ColumnDefault(value = "0")
  private long xCoordinate;

  @ColumnDefault(value = "0")
  private long yCoordinate;

}
