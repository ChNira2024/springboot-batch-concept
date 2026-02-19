package in.niranjana.batch.config;

import org.springframework.batch.item.ItemProcessor;

import in.niranjana.batch.entity.Customer;

public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		// logic

		return item;  // i did not written any logic
	}

}
