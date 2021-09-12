package com.hejula.server.batch.hejulaserverbatch.repository;


import com.hejula.server.batch.hejulaserverbatch.entity.Accommodation;
import com.hejula.server.batch.hejulaserverbatch.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Long> {
    public Statistics findByAccommodationSeqAndYear(long accommodationSeq, String year);
}