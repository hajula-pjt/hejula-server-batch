package com.hejula.server.batch.hejulaserverbatch.repository;


import com.hejula.server.batch.hejulaserverbatch.entity.DailyVisitors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailyVisitorsRepository extends JpaRepository<DailyVisitors, Long> {
   // public  List<Auth> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date compareWithStartDate, Date compareWithEndDate);
}