package com.studio.repository;

import com.studio.entity.PostProductionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostProductionHistoryRepository extends JpaRepository<PostProductionHistory, Long> {
    List<PostProductionHistory> findByBookingIdOrderByUpdatedAtDesc(Long bookingId);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM PostProductionHistory p JOIN FETCH p.booking JOIN FETCH p.updatedBy")
    List<PostProductionHistory> findAllWithBookingAndUser();
}
