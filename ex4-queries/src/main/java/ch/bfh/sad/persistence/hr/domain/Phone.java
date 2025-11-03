package ch.bfh.sad.persistence.hr.domain;

import jakarta.persistence.*;

@Entity
public class Phone extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_seq")
    @SequenceGenerator(name = "phone_seq", sequenceName = "phone_seq")
    private Integer id;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private PhoneType type;

    @ManyToOne
    private Employee employee;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public PhoneType getType() {
        return type;
    }

    public void setType(PhoneType type) {
        this.type = type;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

}
