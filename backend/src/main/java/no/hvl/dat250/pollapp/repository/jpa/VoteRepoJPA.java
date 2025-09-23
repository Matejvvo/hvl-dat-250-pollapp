package no.hvl.dat250.pollapp.repository.jpa;

import no.hvl.dat250.pollapp.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepoJPA extends JpaRepository<Vote, String> {
}
