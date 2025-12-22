package com.bfh.domi.order.customer.converter;

import com.bfh.domi.order.customer.dto.RegisterCustomerDto;
import com.bfh.domi.order.customer.model.Customer;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class RegisterCustomerDtoConverter implements Converter<RegisterCustomerDto, Customer> {
    @Override
    public @Nullable Customer convert(RegisterCustomerDto registerCustomerDto) {
        Customer customer = new Customer();
        customer.setFirstName(registerCustomerDto.firstName());
        customer.setLastName(registerCustomerDto.lastName());
        customer.setEmail(registerCustomerDto.email());
        customer.setAddress(registerCustomerDto.address());
        customer.setCreditCard(registerCustomerDto.creditCard());
        return customer;
    }
}
