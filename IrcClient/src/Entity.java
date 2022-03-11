public class Entity {
    String name;
    State rank;
    boolean gotKey;


    public Entity(String name , State rank , boolean gotKey) {
        this.name = name;
        this.rank = rank;
        this.gotKey = gotKey;
    }

    enum State {
        ADMIN, USER
    }

    public String getName() {
        return name;
    }

    public State getRank() {
        return rank;
    }

    public boolean hasKey() {
        return gotKey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRank(State rank) {
        this.rank = rank;
    }

    public void setGotKey(boolean gotKey) {
        this.gotKey = gotKey;
    }
}
