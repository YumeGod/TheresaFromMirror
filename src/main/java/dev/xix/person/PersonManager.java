package dev.xix.person;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class PersonManager {
    private final List<Person> people;

    public PersonManager() {
        this.people = new LinkedList<>();
    }
    
    public List<Person> getFriends() {
        return people.stream().filter(person -> person.getStatus() == Person.Status.FRIEND).collect(Collectors.toList());
    }

    public List<Person> getEnemies() {
        return people.stream().filter(person -> person.getStatus() == Person.Status.ENEMY).collect(Collectors.toList());
    }
    
    public void addPerson(final Person person) {
        this.people.add(person);
    }


    public List<Person> getPeople() {
        return people;
    }
}
