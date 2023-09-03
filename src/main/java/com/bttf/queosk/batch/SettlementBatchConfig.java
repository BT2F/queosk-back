package com.bttf.queosk.batch;

import com.bttf.queosk.entity.Order;
import com.bttf.queosk.entity.Settlement;
import com.bttf.queosk.repository.OrderRepository;
import com.bttf.queosk.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.Collections;

import static com.bttf.queosk.enumerate.OrderStatus.DONE;

@Configuration
@RequiredArgsConstructor
public class SettlementBatchConfig {

    private OrderRepository orderRepository;

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private SettlementRepository settlementQueryRepository;

    private SettlementRepository settlementRepository;


    @Bean
    public Job settlementJob(Step settlementStep) {
        return jobBuilderFactory.get("settlementJob")
                .incrementer(new RunIdIncrementer())
                .start(settlementStep)
                .build();
    }

    @JobScope
    @Bean
    public Step settlementStep(ItemReader orderItemReader, ItemWriter orderWriter) {
        return stepBuilderFactory.get("settlementStep")
                .<Order, Settlement>chunk(100)
                .reader(orderJpaPagingItemReader())
                .processor(new SettlementItemProcessor())
                .writer(orderWriter)
                .build();
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Settlement> orderWriter() {
        return new RepositoryItemWriterBuilder<Settlement>()
                .repository(settlementRepository)
                .methodName("save")
                .build();
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<Order> orderJpaPagingItemReader() {

        return new JpaPagingItemReaderBuilder<Order>()
                .queryString("SELECT o.restaurant.id AS restaurantId, SUM(m.price) AS totalMenuPrice" +
                        "FROM Order o" +
                        "JOIN o.menu m" +
                        "JOIN o.restaurant r" +
                        "WHERE FUNCTION('DATE', CURRENT_TIMESTAMP) = FUNCTION('DATE', o.createdAt) and" +
                        "o.status = 'DONE' " +
                        "GROUP BY r.id")
                .build();
    }

    @Bean
    @StepScope
    public RepositoryItemReader<Order> orderItemReader() {

        return new RepositoryItemReaderBuilder<Order>()
                .name("orderItemReader")
                .repository(orderRepository)
                .methodName("findByOrderStatus")
                .pageSize(5)
                .arguments(DONE)
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }
}
