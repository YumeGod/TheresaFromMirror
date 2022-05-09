package dev.xix.person;

public final class Person {
    private final String username;
    private String alias;

    private Status status;

    public Person(final String username, final String alias, final Status status) {
        this.username = username;
        this.alias = alias;
        this.status = status;
    }
    
    public Person(final String username, final String alias) {
        this(username, alias, Status.FRIEND);
    }

    public Person(final String username) {
        this(username, username, Status.FRIEND);
    }

    public String getUsername() {
        return username;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        FRIEND,
        ENEMY
    }
}
