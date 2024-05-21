package com.batch.example.processor;

import com.batch.example.domain.Trade;
import org.springframework.batch.item.ItemProcessor;

public class TradeProcessor implements ItemProcessor<Trade, Trade> {
    @Override
    public Trade process(Trade item){
        if(item.getReportingCurrency()!=null && !item.getReportingCurrency().equalsIgnoreCase("USD")){
            item.setPrice(item.getPrice().multiply(item.getFxRate()));
        }
        return item;
    }
}
