package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Comment;
import vn.edu.fpt.model.entity.Customer;
import vn.edu.fpt.model.entity.Event;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Integer> {
    @Query("""
            select count(c) > 0
            from Comment c
            where c.customer.id = :customerId
            and c.event.id = :eventId
            """)
    boolean hasCommented(Integer customerId, Integer eventId);

    @Query("""
            SELECT COALESCE(SUM(c.rating) * 1.0 / COUNT(c.id), 0) 
            FROM Comment c 
            WHERE c.event.id = :eventId
            """)
    Double getAverageRating(@Param("eventId") int eventId);

    // Tổng số comment của event
    @Query("SELECT COUNT(c.id) FROM Comment c WHERE c.event.id = :eventId")
    Long getTotalComments(@Param("eventId") int eventId);

    // Số comment = rating
    @Query("SELECT COUNT(c.id) FROM Comment c WHERE c.event.id = :eventId AND c.rating = :start")
    Long getCountOfStart(@Param("eventId") int eventId, @Param("start") int start);
}

