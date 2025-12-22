package com.bfh.domi.order.common.testdata;

import com.bfh.domi.order.common.model.Address;
import com.bfh.domi.order.customer.model.CreditCard;
import com.bfh.domi.order.customer.model.CreditCardType;
import com.bfh.domi.order.customer.model.Customer;
import com.bfh.domi.order.order.model.Book;

import java.math.BigDecimal;

public class TestdataCreator {

    public static String getEmail() {
        return "anna.schmidt@example.com";
    }

    public static String getEmailNonExistent() {
        return "nonexistent@example.com";
    }

    public static String getEmailAlreadyUsed() {
        return "susi.mueller@example.com";
    }

    public static String getFirstname() {
        return "Anna";
    }

    public static String getFirstnameFragmentEmpty() {
        return "";
    }

    public static String getFirstnameFragmentNonExistent() {
        return "Nonexistent";
    }

    public static String getLastnameFragment() {
        return "LlEr";
    }

    public static String getLastnameFragmentNotMatching() {
        return "NameNotMatching";
    }

    public static String getFirstnameLastnameFragementMixed() {
        return "i";
    }

    public static Long getOrderId() {
        return 100000L;
    }

    public static Long getOrderIdNotExistent() {
        return 999999L;
    }

    public static Long getCustomerId() {
        return 10000L;
    }

    public static Long getCustomerIdNotExistent() {
        return 99999L;
    }

    public static String getCustomerNameNotExistent() {
        return "Nonexistent";
    }

    public static Long getInvalidOrderId() {
        return 999999L;
    }

    public static Long getShippedOrderId() {
        return 100100L;
    }

    public static Long getAcceptedOrderId() {
        return 101500L;
    }

    public static Customer getNewCustomer() {
        String FIRSTNAME = "Michael";
        String LASTNAME = "Rothenbühler";
        String CREDIT_CARD = "7575 7575 7575 7575";
        String ADDRESS_COUNTRY = "Switzerland";

        // Customer
        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(10);
        creditCard.setExpirationYear(2028);

        Customer customer = new Customer();
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail("michael.rothenbuehler@gmail.com");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        return customer;
    }

    public static Customer getUpdateCustomer() {
        String FIRSTNAME = "Anna";
        String LASTNAME = "Schmidt";
        String CREDIT_CARD = "4111 1111 1111 1111";
        String ADDRESS_COUNTRY = "Switzerland";

        // Customer
        Address address = new Address();
        address.setStreet("Grabmattweg 16");
        address.setStateProvince("BE");
        address.setPostalCode("3176");
        address.setCity("Neuenegg");
        address.setCountry(ADDRESS_COUNTRY);

        CreditCard creditCard = new CreditCard();
        creditCard.setType(CreditCardType.VISA);
        creditCard.setNumber(CREDIT_CARD);
        creditCard.setExpirationMonth(12);
        creditCard.setExpirationYear(2026);

        Customer customer = new Customer();
        customer.setId(TestdataCreator.getCustomerId());
        customer.setFirstName(FIRSTNAME);
        customer.setLastName(LASTNAME);
        customer.setEmail("anna.schmidt@example.com");
        customer.setAddress(address);
        customer.setCreditCard(creditCard);

        return customer;
    }

    public static Book getNewBook() {
        Book book = new Book();
        book.setIsbn("1234567891011");
        book.setTitle("Book Title");
        book.setAuthors("Author Name");
        book.setPublisher("Publisher");
        book.setPrice(BigDecimal.valueOf(88.50));

        return book;
    }

    public static Book getNewBookMinimal() {
        Book book = new Book();
        book.setIsbn("1234567891011");
        book.setTitle("Book Title");
        book.setPrice(BigDecimal.valueOf(88.50));

        return book;
    }

    public static Book getMultipleBooks1() {
        Book book1 = new Book();
        book1.setIsbn("1234567891011");
        book1.setTitle("Book Title 1");
        book1.setAuthors("Author Name 1");
        book1.setPublisher("Publisher");
        book1.setPrice(BigDecimal.valueOf(88.50));

        return book1;
    }

    public static Book getMultipleBooks2() {
        Book book2 = new Book();
        book2.setIsbn("9876543210987");
        book2.setTitle("Book Title 2");
        book2.setAuthors("Author Name 2");
        book2.setPublisher("Publisher");
        book2.setPrice(BigDecimal.valueOf(45.00));
        return book2;
    }
}
