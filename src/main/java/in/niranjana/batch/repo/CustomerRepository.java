package in.niranjana.batch.repo;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import in.niranjana.batch.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Serializable> {

}
