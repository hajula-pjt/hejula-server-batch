package com.hejula.server.batch.hejulaserverbatch.repository;


import com.hejula.server.batch.hejulaserverbatch.entity.Review;
import com.hejula.server.batch.hejulaserverbatch.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    public Review findTop1ByAccommodationSeqAndStars(long accommodationSeq, double stars);
}