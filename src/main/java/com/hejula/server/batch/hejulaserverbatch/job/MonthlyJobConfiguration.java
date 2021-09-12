package com.hejula.server.batch.hejulaserverbatch.job;

import com.hejula.server.batch.hejulaserverbatch.dto.AdminPriceDto;
import com.hejula.server.batch.hejulaserverbatch.dto.PriceDto;
import com.hejula.server.batch.hejulaserverbatch.dto.VisitorDto;
import com.hejula.server.batch.hejulaserverbatch.entity.Admin;
import com.hejula.server.batch.hejulaserverbatch.entity.Price;
import com.hejula.server.batch.hejulaserverbatch.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 월별 통계를 위한 job
 *   예상 총 방문자수, 예상 총 매출액, 가동률
 *
 * @author jooyeon
 * @since 2021.07.17
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MonthlyJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final AdminRepository adminRepository;

    private int chunkSize = 100;

    @Bean
    public Job monthlyJob(){
        return jobBuilderFactory.get("monthlyJob" + new Date().toString()) //unique 하도록
                //.start(thisMonthVisitorsStep())
                .start(thisMonthSalesStep())
                .build();
    }

    /* 이번달 예상 총 방문자수 */
    @Bean
    public Step thisMonthVisitorsStep(){
        return stepBuilderFactory.get("thisMonthVisitorsStep")
                .<VisitorDto, Admin>chunk(chunkSize)
                .reader(thisMonthVisitorsReader())
                .processor(visitorsAdminProcessor())
                .writer(adminWriter())
                .build();
    }

    /* 이번 달 예상 총 매출액 */
    @Bean
    public Step thisMonthSalesStep(){
        return stepBuilderFactory.get("thisMonthSalesStep")
                .<AdminPriceDto, Admin>chunk(chunkSize)
                .reader(thisMonthSalesReader())
                .processor(priceAdminProcessor())
                .writer(adminWriter())
                .build();
    }

    @Bean
    public JpaPagingItemReader<VisitorDto> thisMonthVisitorsReader(){
        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("month", cal.get(Calendar.MONTH) + 1); //+1하지 않으면 전달

        return new JpaPagingItemReaderBuilder<VisitorDto>()
                .name("monthlyVisitorsReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT new com.hejula.server.batch.hejulaserverbatch.dto.VisitorDto" +
                        "       (ad.adminSeq, sum(s.adult) + sum(s.children), sum(DATEDIFF(s.checkoutDate, s.checkinDate)))" +
                        " FROM Schedule s " +
                        "   INNER JOIN Accommodation a on a.accommodationSeq = s.accommodationSeq " +
                        "   INNER JOIN Admin ad on ad.adminSeq = a.adminSeq " +
                        " WHERE month(s.checkinDate) = :month" +
                        " GROUP BY ad.adminSeq"
                )
                .parameterValues(params)
                .build();
    }

    @Bean
    public JpaPagingItemReader<AdminPriceDto> thisMonthSalesReader(){
        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("month", cal.get(Calendar.MONTH) + 1); //+1하지 않으면 전달

        return new JpaPagingItemReaderBuilder<AdminPriceDto>()
                .name("thisMonthSalesReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT new com.hejula.server.batch.hejulaserverbatch.dto.AdminPriceDto" +
                        "       (a.adminSeq, sum(p.price)) " +
                        " FROM  Schedule s " +
                        "   INNER JOIN Price p on p.accommodationSeq = s.accommodationSeq " +
                        "   INNER JOIN CustomDates d on p.fullDay = d.fullDay " +
                        "   INNER JOIN Accommodation a on s.accommodationSeq = a.accommodationSeq " +
                        " WHERE (month(s.checkinDate) = :month or month(s.checkoutDate) = :month) " +
                        " AND p.fullDay between s.checkinDate and s.checkoutDate" +
                        " AND d.month = :month " +
                        " GROUP BY a.adminSeq"
                )
                .parameterValues(params)
                .build();
    }

    @Bean
    public ItemProcessor<VisitorDto, Admin> visitorsAdminProcessor(){
        Calendar cal = Calendar.getInstance();
        long days = cal.getActualMaximum(Calendar.DAY_OF_MONTH); //해당 달의 말일

        return visitorDto -> {
            Admin admin = adminRepository.findById(visitorDto.getAdminSeq()).orElseThrow(IllegalArgumentException::new);
            admin.setThisMonthVisitors(visitorDto.getVisitors());
            double operation = ((double)visitorDto.getReservationDays()/(double)days)*100;
            admin.setThisMonthRateOperation((long)operation);
            return admin;
        };
    }

    @Bean
    public ItemProcessor<AdminPriceDto, Admin> priceAdminProcessor(){
        return adminPriceDto -> {
            Admin admin = adminRepository.findById(adminPriceDto.getAdminSeq()).orElseThrow(IllegalArgumentException::new);
            admin.setThisMonthSales(adminPriceDto.getPrice());
            return admin;
        };
    }

    @Bean
    public JpaItemWriter<Admin> adminWriter(){
        JpaItemWriter<Admin> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

}
