package com.hejula.server.batch.hejulaserverbatch.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Date;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "file")
public class FileEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long fileSeq;

//    @ManyToOne(targetEntity = Accommodation.class)
//    @JoinColumn(name = "accommodation_seq")
//    private Accommodation accommodation;
    @Column(nullable = false)
    private long accommodationSeq;

//    @ManyToOne(targetEntity = Customer.class)
//    @JoinColumn(name = "customer_seq")
//    private Customer customer;
@Column(nullable = false)
private long customerSeq;

//    @ManyToOne(targetEntity = Admin.class)
//    @JoinColumn(name = "admin_seq")
//    private Admin admin;
@Column(nullable = false)
private long adminSeq;

    @Column(nullable = false)
    private String fileNm;

    @Column(nullable = false)
    private String originalFileNm;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private long fileSize;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private Date registDate;

}
