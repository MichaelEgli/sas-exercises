package org.example.booking;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Booking {

	public enum Service { FLIGHT, HOTEL }

	@Id
	@GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	private Service service;
	private String destination;
	private String customer;

	public Booking() {
	}

	public Booking(Service service, String destination, String customer) {
		this.service = service;
		this.destination = destination;
		this.customer = customer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}
}
