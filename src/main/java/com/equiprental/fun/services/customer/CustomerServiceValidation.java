package com.equiprental.fun.services.customer;

import com.equiprental.fun.exceptions.AlreadyExistException;
import com.equiprental.fun.exceptions.NotFoundException;
import com.equiprental.fun.models.dto.CustomerDTO;
import com.equiprental.fun.models.entity.Customer;
import com.equiprental.fun.models.dto.login.RegisterRequestDto;
import com.equiprental.fun.repositories.CustomerRepository;
import com.equiprental.fun.util.UserRole;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomerServiceValidation {

    private final CustomerRepository customerRepository;

    public CustomerServiceValidation(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void checkIfCustomerExists(CustomerDTO customerDTO) {
        Optional<Customer> cnpCustomer = customerRepository.findByCnp(customerDTO.getCnp());
        if (cnpCustomer.isPresent()) {
            throw new AlreadyExistException.CnpAlreadyExistException(customerDTO.getCnp(), UserRole.USER);
        }
        Optional<Customer> emailCustomer = customerRepository.findByEmail(customerDTO.getEmail());
        if (emailCustomer.isPresent()) {
            throw new AlreadyExistException.EmailAddressAlreadyExistException(customerDTO.getEmail(), UserRole.USER);
        }
        Optional<Customer> phoneNumberCustomer = customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber());
        if (phoneNumberCustomer.isPresent()) {
            throw new AlreadyExistException.PhoneNumberAlreadyExistException(customerDTO.getPhoneNumber(), UserRole.USER);
        }
    }

    public void validateCustomerNotAlreadyRegistered(RegisterRequestDto customer) throws AlreadyExistException.EmailAddressAlreadyExistException {
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            throw new AlreadyExistException.EmailAddressAlreadyExistException(customer.getEmail(), UserRole.USER);
        }
    }

    public void validateCustomerNotFound(Customer customer) throws NotFoundException.UserNotFoundException {
        if (customer == null) {
            throw new NotFoundException.UserNotFoundException(UserRole.USER);
        }
    }
}