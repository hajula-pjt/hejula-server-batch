package com.hejula.server.batch.hejulaserverbatch.repository;


import com.hejula.server.batch.hejulaserverbatch.entity.Accommodation;
import com.hejula.server.batch.hejulaserverbatch.entity.AccommodationStatisticsTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccommodationStatisticsTagRepository extends JpaRepository<AccommodationStatisticsTag, Long> {
    public AccommodationStatisticsTag findByAccommodationSeqAndName(long accommodationSeq, String name);
}