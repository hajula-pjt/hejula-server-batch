package com.hejula.server.batch.hejulaserverbatch.repository;


import com.hejula.server.batch.hejulaserverbatch.entity.DailyVisitors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyVisitorsRepository extends JpaRepository<DailyVisitors, Long> {
}