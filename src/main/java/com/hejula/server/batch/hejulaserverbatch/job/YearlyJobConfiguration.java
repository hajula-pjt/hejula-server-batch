package com.hejula.server.batch.hejulaserverbatch.job;

import com.hejula.server.batch.hejulaserverbatch.dto.*;
import com.hejula.server.batch.hejulaserverbatch.entity.*;
import com.hejula.server.batch.hejulaserverbatch.repository.AccommodationRepository;
import com.hejula.server.batch.hejulaserverbatch.repository.AccommodationStatisticsTagRepository;
import com.hejula.server.batch.hejulaserverbatch.repository.ReviewRepository;
import com.hejula.server.batch.hejulaserverbatch.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.*;

/**
 * 연 통계를 위한 job
 *   평균금액, 숙소 기간별(연령대, 태그, 평점)
 *
 * @author jooyeon
 * @since 2021.07.17
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class YearlyJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final AccommodationRepository accommodationRepository;
    private final StatisticsRepository statisticsRepository;
    private final AccommodationStatisticsTagRepository accommodationStatisticsTagRepository;
    private final ReviewRepository reviewRepository;

    private int chunkSize = 100;

    @Bean
    public Job yearlyJob(){
        return jobBuilderFactory.get("yearlyJob" + new Date().toString())
                .start(updateVisitorsZeroStep())
                .next(ageStep())
                .next(updateVisitorrsPercentageStep())
                .next(averageAmountStep())
                .next(monthlyVisitorsStep())
                .next(tagStep())
                .next(reviewStep())
                .build();
    }


    /* 숙소별 평균 숙박 금액 */
    @Bean
    public Step averageAmountStep(){
        return stepBuilderFactory.get("averageAmountStep")
                .<PriceDto, Accommodation>chunk(chunkSize)
                .reader(priceItemReader())
                .processor(priceAndAccommodationItemProcessor())
                .writer(accommodationItemWriter())
                .build();
    }

    /* 숙소별 연령대 */
    @Bean
    public Step ageStep(){
        return stepBuilderFactory.get("ageStep")
                .<AgeDto, Statistics>chunk(chunkSize)
                .reader(scheduleAndCustomerItemReader())
                .processor(countByAgeProcessor())
                .writer(statisticsWriter())
                .build();
    }

    /* 숙소별 태그 */
    @Bean
    public Step tagStep(){
        return stepBuilderFactory.get("tagStep")
                .<TagDto, AccommodationStatisticsTag>chunk(chunkSize)
                .reader(customerTagItemReader())
                .processor(tagScoreByAgeProcessor())
                .writer(accommodationStatisticsTagWriter())
                .build();
    }

    /* 최저, 최고, 평균 리뷰 */
    @Bean
    public Step reviewStep(){
        return stepBuilderFactory.get("reviewStep")
                .<ReviewDto, Statistics>chunk(chunkSize)
                .reader(reviewItemReader())
                .processor(reviewProcessor())
                .writer(statisticsWriter())
                .build();
    }

    /* 월별 숙소 방문객 수 */
    @Bean
    public Step monthlyVisitorsStep(){
        return stepBuilderFactory.get("monthlyVisitorsStep")
                .<VisitorDto, Statistics>chunk(chunkSize)
                .reader(scheduleVisitorReader())
                .processor(scheduleVisitorProcessor())
                .writer(statisticsWriter())
                .build();
    }

    /* 방문객 0으로 초기화 */
    @Bean
    public Step updateVisitorsZeroStep(){
        return stepBuilderFactory.get("updateVisitorsZeroStep")
            .tasklet((stepContribution, chunkContext) -> {
                List<Statistics> allList = statisticsRepository.findAll();
                allList.forEach(s -> {
                    s.setTeens(0);
                    s.setTwenties(0);
                    s.setThirties(0);
                    s.setFourties(0);
                    s.setFifties(0);
                });
                statisticsRepository.saveAll(allList);
                return RepeatStatus.FINISHED;
        }).build();
    }

    /* 퍼센테이지로 변경 */
    @Bean
    public Step updateVisitorrsPercentageStep() {
        return stepBuilderFactory.get("updateVisitorrsPercentageStep")
                .tasklet((stepBuilderFactory, chunkSize) -> {
                    List<Statistics> allList = statisticsRepository.findAll();
                    allList.forEach(s -> {
                        long sum = s.getJanVisitors() + s.getFebVisitors() + s.getMarVisitors() + s.getAprVisitors() + s.getMayVisitors() + s.getJunVisitors() + s.getJulVisitors() + s.getAugVisitors()
                                + s.getSepVisitors() + s.getOctVisitors() + s.getNovVisitors() + s.getDecVisitors();
                        s.setTeens((long)(((double)s.getTeens()/(double)sum) * 100));
                        s.setTwenties((long)(((double)s.getTwenties()/(double)sum) * 100));
                        s.setThirties((long)(((double)s.getThirties()/(double)sum) * 100));
                        s.setFifties((long)(((double)s.getFifties()/(double)sum) * 100));
                        s.setFifties((long)(((double)s.getFifties()/(double)sum) * 100));
                    });
                    statisticsRepository.saveAll(allList);
                    return RepeatStatus.FINISHED;
                }).build();
    }


    @Bean
    public JpaPagingItemReader<PriceDto> priceItemReader(){

        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("year", cal.get(Calendar.YEAR));

        return new JpaPagingItemReaderBuilder<PriceDto>()
                .name("accommodationItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT new com.hejula.server.batch.hejulaserverbatch.dto.PriceDto(p.accommodationSeq, avg(p.price))" +
                        " FROM Price p WHERE year = :year" +
                        " GROUP BY p.accommodationSeq ")
                .parameterValues(params)
                .build();
    }

    @Bean
    public JpaPagingItemReader<AgeDto> scheduleAndCustomerItemReader() {
        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("year", cal.get(Calendar.YEAR));

        return new JpaPagingItemReaderBuilder<AgeDto>()
                .name("scheduleAndCustomerItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT new com.hejula.server.batch.hejulaserverbatch.dto.AgeDto (s.accommodationSeq," +
                        "       case when (c.age >= 0 and c.age < 20) then 'teens'" +
                        "           when (c.age>=20 and c.age <30) then 'twenties'" +
                        "           when (c.age>=30 and c.age <40) then 'thirties'" +
                        "           when (c.age>=40 and c.age <50) then 'fourties'" +
                        "           else 'fifties'" +
                        "       end as age, " +
                        "       count(c.age)) " +
                        "   FROM Schedule s " +
                        "       INNER JOIN Customer c on s.customerSeq = c.customerSeq " +
                        "   WHERE YEAR(s.checkinDate) = :year" +
                        " GROUP BY s.accommodationSeq, c.age"
                )
                .parameterValues(params)
                .build();
    }

    @Bean
    public JpaPagingItemReader<TagDto> customerTagItemReader(){
        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("year", cal.get(Calendar.YEAR));

        return new JpaPagingItemReaderBuilder<TagDto>()
                .name("customerTagItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString("SELECT new com.hejula.server.batch.hejulaserverbatch.dto.TagDto(s.accommodationSeq, t.name, count(t.name))" +
                        " FROM Schedule s" +
                        "   INNER JOIN Customer c  on s.customerSeq = c.customerSeq" +
                        "   INNER JOIN CustomerTag t on c.customerSeq = t.customerSeq" +
                        "   WHERE YEAR(s.checkinDate) = :year" +
                        " group by s.accommodationSeq, t.name"
                )
                .parameterValues(params)
                .build();
    }

    @Bean
    public JpaPagingItemReader<ReviewDto> reviewItemReader(){
        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("year", cal.get(Calendar.YEAR));

        return new JpaPagingItemReaderBuilder<ReviewDto>()
                .name("reviewItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(" SELECT new com.hejula.server.batch.hejulaserverbatch.dto.ReviewDto" +
                                " (r.accommodationSeq, min(r.stars), max(r.stars), avg(r.stars)) " +
                        " FROM Review r " +
                        "   INNER JOIN Schedule s on r.scheduleSeq = s.scheduleSeq " +
                        " WHERE YEAR(s.checkinDate) = :year" +
                        " GROUP BY r.accommodationSeq"
                )
                .parameterValues(params)
                .build();
    }

    @Bean
    public JpaPagingItemReader<VisitorDto> scheduleVisitorReader(){
        Calendar cal = Calendar.getInstance();
        Map<String, Object> params = new HashMap<>();
        params.put("year", cal.get(Calendar.YEAR));

        return new JpaPagingItemReaderBuilder<VisitorDto>()
                .name("scheduleVisitorReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(chunkSize)
                .queryString(" SELECT new com.hejula.server.batch.hejulaserverbatch.dto.VisitorDto" +
                        " (s.accommodationSeq, month(s.checkinDate), sum(s.adult) + sum(s.children)) " +
                        " FROM Schedule s " +
                        " WHERE YEAR(s.checkinDate) = :year" +
                        " GROUP BY s.accommodationSeq, month(s.checkinDate)"
                )
                .parameterValues(params)
                .build();
    }


    @Bean
    public ItemProcessor<PriceDto, Accommodation> priceAndAccommodationItemProcessor(){
        return priceDto -> {
            Accommodation a = accommodationRepository.findById(priceDto.getAccommodationSeq()).orElseThrow(IllegalArgumentException::new);
            a.setYearAveragePrice(priceDto.getTotal());
            return a;
        };
    }

    @Bean
    public ItemProcessor<AgeDto, Statistics> countByAgeProcessor() {
        Calendar cal = Calendar.getInstance();

        //processor : 숙소별 10대-50대 count
        return ageDto -> {
            Statistics s = statisticsRepository.findByAccommodationSeqAndYear(ageDto.getAccommodationSeq(), String.valueOf(cal.get(Calendar.YEAR)));
            if(s == null){
                s = new Statistics();
                s.setAccommodationSeq(ageDto.getAccommodationSeq());
            }

            long count = ageDto.getCountAge();

            switch (ageDto.getAge()){
                case "teens":
                    if(s!=null){count += s.getTeens();}
                    s.setTeens(count); // 퍼센테이지로 저장
                    break;
                case "twenties":
                    if(s!=null){count += s.getTwenties();}
                    s.setTwenties(count);
                    break;
                case "thirties":
                    if(s!=null){count += s.getThirties();}
                    s.setThirties(count);
                    break;
                case "fourties":
                    if(s!=null){count += s.getFourties();}
                    s.setFourties(count);
                    break;
                default:
                    if(s!=null){count += s.getFifties();}
                    s.setFifties(count);
                    break;
            }
            return s;
        };
    }

    @Bean
    public ItemProcessor<TagDto, AccommodationStatisticsTag> tagScoreByAgeProcessor(){
        return tagDto -> {
            AccommodationStatisticsTag tag =
                    accommodationStatisticsTagRepository.findByAccommodationSeqAndName(tagDto.getAccommodationSeq(), tagDto.getTagName());

            //기존에 존재하면 +1
            if(tag != null){
                tag.setScore(tag.getScore() + (int)tagDto.getCount());
            }else{
                tag = new AccommodationStatisticsTag();
                tag.setName(tagDto.getTagName());
                tag.setAccommodationSeq(tagDto.getAccommodationSeq());
                tag.setScore(1);
            }

            return tag;
        };
    }

    @Bean
    public ItemProcessor<ReviewDto, Statistics> reviewProcessor(){
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));

        return reviewDto -> {
            Statistics s = statisticsRepository.findByAccommodationSeqAndYear(reviewDto.getAccommodationSeq(), year);

            //최저
            Review review = reviewRepository.findTop1ByAccommodationSeqAndStars(reviewDto.getAccommodationSeq(), reviewDto.getMinStars());
            s.setMinReviewSeq(review.getReviewSeq());
            s.setWorstRating(review.getStars());

            //최대
            review = reviewRepository.findTop1ByAccommodationSeqAndStars(reviewDto.getAccommodationSeq(), reviewDto.getMaxStars());
            s.setMaxReviewSeq(review.getReviewSeq());
            s.setBestRating(review.getStars());

            //평균
            s.setAverageRating(reviewDto.getAvgStars());

            return s;
        };
    }

    @Bean
    public ItemProcessor<VisitorDto, Statistics> scheduleVisitorProcessor(){
        Calendar cal = Calendar.getInstance();
        String year = String.valueOf(cal.get(Calendar.YEAR));

        return visitorDto -> {
            Statistics s = statisticsRepository.findByAccommodationSeqAndYear(visitorDto.getAccommodationSeq(), year);
            long visitors = visitorDto.getVisitors();
            switch (visitorDto.getMonth()){
                case 1:
                    s.setJanVisitors(visitors);
                    break;
                case 2:
                    s.setFebVisitors(visitors);
                    break;
                case 3:
                    s.setMarVisitors(visitors);
                    break;
                case 4:
                    s.setAprVisitors(visitors);
                    break;
                case 5:
                    s.setMayVisitors(visitors);
                    break;
                case 6:
                    s.setJunVisitors(visitors);
                    break;
                case 7:
                    s.setJulVisitors(visitors);
                    break;
                case 8:
                    s.setAugVisitors(visitors);
                    break;
                case 9:
                    s.setSepVisitors(visitors);
                    break;
                case 10:
                    s.setOctVisitors(visitors);
                    break;
                case 11:
                    s.setNovVisitors(visitors);
                    break;
                case 12:
                    s.setDecVisitors(visitors);
                    break;
            }

            return s;
        };
    }

    @Bean
    public JpaItemWriter<Accommodation> accommodationItemWriter(){
        JpaItemWriter<Accommodation> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<Statistics> statisticsWriter() {
        JpaItemWriter<Statistics> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public JpaItemWriter<AccommodationStatisticsTag> accommodationStatisticsTagWriter() {
        JpaItemWriter<AccommodationStatisticsTag> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }


}
