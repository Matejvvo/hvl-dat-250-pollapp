package no.hvl.dat250.pollapp.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    // --- Attributes --
    private UUID id;
    private String username;
    private String password;
    private String email;

    // Associations
    private Set<Poll> createdPolls = new HashSet<>();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Poll> getCreatedPolls() {
        return createdPolls;
    }

    public void setCreatedPolls(Set<Poll> createdPolls) {
        this.createdPolls = createdPolls;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    // --- Printer ---
    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username='" + username + '\'' + ", email='" + email + '\'' + ", password='" + password + '\'' + ", createdPolls=" + createdPolls.size() + ", votes=" + votes.size() + '}';
    }
}
