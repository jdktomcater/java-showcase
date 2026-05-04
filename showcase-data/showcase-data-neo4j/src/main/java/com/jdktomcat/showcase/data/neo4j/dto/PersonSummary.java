package com.jdktomcat.showcase.data.neo4j.dto;

import com.jdktomcat.showcase.data.neo4j.domain.Person;

public class PersonSummary {

    private final String userId;

    private final String name;

    private final String city;

    private final String bio;

    public PersonSummary(String userId, String name, String city, String bio) {
        this.userId = userId;
        this.name = name;
        this.city = city;
        this.bio = bio;
    }

    public static PersonSummary from(Person person) {
        return new PersonSummary(person.getUserId(), person.getName(), person.getCity(), person.getBio());
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getBio() {
        return bio;
    }
}
