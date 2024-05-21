package com.batch.example.config;

import com.batch.example.domain.Trade;
import com.batch.example.listener.JobCompletionListener;
import com.batch.example.processor.TradeProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class BatchConfig {

    @Bean
    public Job tradeJob(JobRepository jobRepository,
                        Step loadTrades,
                        JobCompletionListener jobCompletionListener) {
        return new JobBuilder("EodTradeJob", jobRepository)
                .listener(jobCompletionListener)
                .start(loadTrades)
                .build();
    }

    @Bean
    public Step loadTrades(JobRepository jobRepository,
                           DataSourceTransactionManager transactionManager,
                           FlatFileItemReader<Trade> tradeReader,
                           JdbcBatchItemWriter<Trade> tradeWriter,
                           TradeProcessor tradeItemProcessor) {
        return new StepBuilder("trade-load-step", jobRepository)
                .<Trade, Trade>chunk(10, transactionManager)
                .reader(tradeReader)
                .processor(tradeItemProcessor)
                .writer(tradeWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<Trade> tradeReader(FieldSetMapper<Trade> tradeFieldSetMapper) {
        return new FlatFileItemReaderBuilder<Trade>()
                .name("tradeReader")
                .resource(new ClassPathResource("trades.csv"))
                .delimited().delimiter(",")
                .names("transactionId", "ticker", "securityDescription", "price", "quantity", "reportingCurrency",
                        "fxRate", "tradeType", "transactionDate")
                .linesToSkip(1)
                .fieldSetMapper(tradeFieldSetMapper)
                .build();
    }

    @Bean
    public FieldSetMapper<Trade> tradeFieldSetMapper(ConversionService defaultConversionWithDate) {
        BeanWrapperFieldSetMapper<Trade> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setConversionService(defaultConversionWithDate);
        mapper.setTargetType(Trade.class);
        return mapper;
    }

    @Bean
    public ConversionService defaultConversionWithDate() {
        DefaultConversionService defaultConversionService = new DefaultConversionService();
        DefaultConversionService.addDefaultConverters(defaultConversionService);
        defaultConversionService.addConverter(new Converter<String, LocalDate>() {
            @Override
            public LocalDate convert(String text) {
                return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
            }
        });

        return defaultConversionService;
    }

    @Bean
    public JdbcBatchItemWriter<Trade> tradeWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Trade>()
                .sql("INSERT INTO trades (transaction_id, ticker, security_description, price, quantity, reporting_currency, fx_rate, trade_type, transaction_date) " +
                        "values (:transactionId, :ticker, :securityDescription, :price, :quantity, :reportingCurrency, :fxRate, :tradeType, :transactionDate) ")
                .dataSource(dataSource)
                .beanMapped()
                .build();
    }

    @Bean
    public TradeProcessor tradeItemProcessor() {
        return new TradeProcessor();
    }

    @Bean
    public JobCompletionListener jobCompletionListener(JdbcTemplate jdbcTemplate) {
        return new JobCompletionListener(jdbcTemplate);
    }

}
