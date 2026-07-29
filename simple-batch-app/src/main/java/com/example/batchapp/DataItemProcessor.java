package com.example.batchapp;

import com.example.batchapp.model.Person;
import com.example.batchapp.model.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class DataItemProcessor implements ItemProcessor<Person, Results> {

    private static final Logger log = LoggerFactory.getLogger(DataItemProcessor.class);

    @Override
    public Results process(Person item) throws Exception {
        Results results = new Results(item.firstName(), item.lastName());
        log.info("Converting ({}) into ({})", item, results);
        return results;
    }
}
