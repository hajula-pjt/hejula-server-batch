package com.hejula.server.batch.hejulaserverbatch.job;

import com.hejula.server.batch.hejulaserverbatch.entity.DailyVisitors;
import com.hejula.server.batch.hejulaserverbatch.entity.Schedule;
import com.hejula.server.batch.hejulaserverbatch.entity.Schedule;
import com.hejula.server.batch.hejulaserverbatch.repository.DailyVisitorsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityManagerFactory;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WeeklyVisitorsJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final DailyVisitorsRepository dailyVisitorsRepository;

    private int chunkSize = 10;

    @Bean
    public Job weeklyVisitorsJob(){
        return jobBuilderFactory.get("weeklyVisitorsJob5")
                .start(deleteDailyVisitor())
                .next(weeklyVisitorsStep())
                .build();
    }

    @Bean
    public Step deleteDailyVisitor(){
        return stepBuilderFactory.get("deleteDailyVisitor")
                .tasklet((stepContribution, chunkContext) -> {
                    dailyVisitorsRepository.deleteAll();
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step weeklyVisitorsStep(){
        return stepBuilderFactory.get("weeklyVisitorsStep5")
                .<DailyVisitors, DailyVisitors>chunk(chunkSize)
                .reader(scheduleItemReader())
                .writer(ScheduleItemWriter())
//                .tasklet(((stepContribution, chunkContext) -> {
//                    log.info(">>>>>>>> This is Step1");
//                    return RepeatStatus.FINISHED;
//                }))
                .build();
    }

    @Bean
    public JpaPagingItemReader<DailyVisitors> scheduleItemReader(){
        return new JpaPagingItemReaderBuilder<DailyVisitors>()
                .name("scheduleItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(getQueryString())
                .parameterValues(getParameters())
                .build();
    }

    @Bean
    public JpaItemWriter<DailyVisitors> ScheduleItemWriter(){
        JpaItemWriter<DailyVisitors> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }



    private Map<String, Object> getParameters(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date today = getTodayWithTime();
        Date start = getStartOfWeek(today);
        sdf.format(start);
        Date end = getEndOfWeek(today);
        sdf.format(end);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fromDate", start);
        parameters.put("toDate", end);

        return parameters;
    }

    private String getQueryString(){
        return "SELECT new com.hejula.server.batch.hejulaserverbatch.entity.DailyVisitors" +
                "  ( s.accommodationSeq, d.fullDay, d.dayOfWeek, sum(s.adult) + sum(s.children) as visitors )" +
                " FROM CustomDates d, Schedule s  " +
                " WHERE d.fullDay BETWEEN :fromDate AND :toDate " +
                "   AND (s.checkinDate BETWEEN :fromDate AND :toDate " +
                "      OR s.checkoutDate BETWEEN :fromDate AND :toDate)" +
                "   AND d.fullDay BETWEEN  s.checkinDate AND s.checkoutDate" +
                " group by d.fullDay, s.accommodationSeq, d.dayOfWeek";
       /* return "select s " +
                " from CustomDates d " +
                "  join Schedule s " +
                " where d.fullDay between '2021-08-16'  and '2021-08-22' " +
                " and (s.checkinDate between '2021-08-16' and '2021-08-22' " +
                "     or s.checkoutDate between '2021-08-16'  and '2021-08-22')" +
                " group by d.fullDay, s.accommodationSeq, d.dayOfWeek";*/
    }


    private Date getTodayWithTime(){
        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        //00:00:00으로설정
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        return cal.getTime();
    }


    private Date getStartOfWeek(Date date){ //월
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = dayOfWeek - Calendar.MONDAY;
        if(offset > 2) {
            cal.add(Calendar.DAY_OF_MONTH, -offset);
        }else if(offset == -1){
            cal.add(Calendar.DAY_OF_MONTH, -6);
        }

        //00:00:00으로설정
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTime();
    }

    private Date getEndOfWeek(Date date){ //일
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int offset = dayOfWeek - Calendar.SUNDAY;
        if(offset > 1) {
            cal.add(Calendar.DAY_OF_MONTH, (7 - offset) + 1); //정확한 end값 검사를 위해 +1 일
        }

        //00:00:00으로설정
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal.getTime();
    }
}
