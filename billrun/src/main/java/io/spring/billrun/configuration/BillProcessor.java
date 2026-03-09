package io.spring.billrun.configuration;

import io.spring.billrun.model.Bill;
import io.spring.billrun.model.Usage;
import org.springframework.batch.item.ItemProcessor;

public class BillProcessor implements ItemProcessor<Usage, Bill> {

    @Override
    public Bill process(Usage usage) {
        Double billAmount = usage.dataUsage() * .001 + usage.minutes() * .01;
        return new Bill(usage.id(), usage.firstName(), usage.lastName(),
                usage.dataUsage(), usage.minutes(), billAmount);
    }
}
