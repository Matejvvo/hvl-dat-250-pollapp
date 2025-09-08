package no.hvl.dat250.pollapp.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {
    // --- Attributes --
    private UUID id;
    private String username;
    private String email;

    // Associations
    @JsonManagedReference(value = "poll-user")
    private Set<Poll> polls = new HashSet<>();
    @JsonManagedReference(value = "vote-user")
    private Set<Vote> votes = new HashSet<>();

    // --- Public Bean Constructor ---
    public User() {
    }

    // --- Getters & Setters ---
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Poll> getPolls() {
        return polls;
    }

    public void setPolls(Set<Poll> polls) {
        this.polls = polls;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    // --- Overrides ---
    @Override
    public String toString() {
        return "User{"  + "username='" + username
                        + ", email='" + email
                        + ", createdPolls=" + polls.size()
                        + ", votes=" + votes.size()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        User that = (User) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
