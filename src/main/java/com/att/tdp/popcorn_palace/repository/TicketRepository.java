package com.att.tdp.popcorn_palace.repository;

import com.att.tdp.popcorn_palace.model.Ticket;
import com.att.tdp.popcorn_palace.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByShowtimeAndSeatNumber(Showtime showtime, Integer seatNumber);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.showtime.id = :showtimeId")
    int countBookedSeatsByShowtime(Long showtimeId);
}
