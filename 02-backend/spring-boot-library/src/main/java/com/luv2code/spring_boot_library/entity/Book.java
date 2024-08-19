// added notes on 2024.07.11

package com.luv2code.spring_boot_library.entity;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "book")
@Data
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "description")
    private String description;

    @Column(name = "copies")
    private int copies;

    @Column(name = "copies_available")
    private int copiesAvailable;

    @Column(name = "category")
    private String category;

    @Column(name = "img")
    private String img;

}

/*
* Imports
*   lombok.Data: generates boilerplate code: getters, setters, toString(), equals(), hashCode()
*   imports JPA annotations used for mapping the class to a database table
*
* Class Declaration
*   @Entity: Specifies that the class is an entity and is mapped to a database table.
*   @Table: Specifies the name of the database table to be used for mapping.
*   @Data: Lombok annotation.
*
* Fields
*   @Id: indicates that this field is the primary key.
*   @GeneratedValue(strategy = ...):
*       Specifies the primary key generation strategy.
*       IDENTITY: the database will generate the primary key value.
*   @Column: Maps the field to the corresponding column in the table.
*
* */
