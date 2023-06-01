package com.bank.neoris.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="client")
public class Person {

    @Id
    @Column(name = "identification", unique = true)
    private Long identification;

    private String name;
    private String gender;
    private int age;
    private String address;
    private Long phone;

}
