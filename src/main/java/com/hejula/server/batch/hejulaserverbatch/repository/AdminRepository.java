package com.hejula.server.batch.hejulaserverbatch.repository;


import com.hejula.server.batch.hejulaserverbatch.entity.AccommodationStatisticsTag;
import com.hejula.server.batch.hejulaserverbatch.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
}