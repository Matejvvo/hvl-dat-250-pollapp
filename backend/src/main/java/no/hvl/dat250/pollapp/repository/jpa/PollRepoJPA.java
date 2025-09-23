package no.hvl.dat250.pollapp.repository.jpa;

import no.hvl.dat250.pollapp.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepoJPA extends JpaRepository<Poll, String> {
}
