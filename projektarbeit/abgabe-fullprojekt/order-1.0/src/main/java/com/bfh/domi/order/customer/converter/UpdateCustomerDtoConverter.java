package com.bfh.domi.order.customer.converter;

import com.bfh.domi.order.customer.dto.UpdateCustomerDto;
import com.bfh.domi.order.customer.model.Customer;
import org.jspecify.annotations.Nullable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UpdateCustomerDtoConverter implements Converter<UpdateCustomerDto, Customer> {
    @Override
    public @Nullable Customer convert(UpdateCustomerDto updateCustomerDto) {
        Customer customer = new Customer();
        customer.setId(updateCustomerDto.id());
        customer.setFirstName(updateCustomerDto.firstName());
        customer.setLastName(updateCustomerDto.lastName());
        customer.setEmail(updateCustomerDto.email());
        customer.setAddress(updateCustomerDto.address());
        customer.setCreditCard(updateCustomerDto.creditCard());
        return customer;
    }
}
