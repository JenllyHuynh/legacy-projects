package vn.edu.fpt.repository;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.DiscountCode;
import vn.edu.fpt.model.entity.Venue;

import java.util.List;

@Repository
public interface VenueRepo extends JpaRepository<Venue, Integer> {
    @Query("""
                select distinct v.city
                from Venue v
                /*where v.isActive = true (có nên hiện các địa điểm đã active = 0 không, để người dùng có thể tìm kếm các sự kiện cũ của địa điểm cu, tạm thời để đây nếu sau này có thay đổi thì xóa sau)*/
                order by v.city
            """)
    List<String> getAllCities();

    Venue findById(int venueId);

    @Override
    Page<Venue> findAll(@NonNull Pageable pageable);

    @Query(value = """
        SELECT v
        FROM Venue v
        WHERE v.name LIKE CONCAT('%', :name, '%')
    """)
    Page<Venue> findVenueByName(@Param("name") String name, Pageable pageable);

    @Query(value = """
        SELECT v
        FROM Venue v
        WHERE v.isActive = true
    """)
    Page<Venue> findVenueByIsActive(@NonNull Pageable pageable);

    @Query(value = """
        SELECT v
        FROM Venue v
        WHERE v.isActive = true
        AND v.name LIKE CONCAT('%', :name, '%')
    """)
    Page<Venue> findVenueByIsActiveAndName(@Param("name") String name, @NonNull Pageable pageable);

    @Query(value = """
        SELECT v
        FROM Venue v
        WHERE v.isActive = false
    """)
    Page<Venue> findVenueByInActive(@NonNull Pageable pageable);

    @Query(value = """
        SELECT v
        FROM Venue v
        WHERE v.isActive = false
        AND v.name LIKE CONCAT('%', :name, '%')
    """)
    Page<Venue> findVenueByInActiveAndName(@Param("name") String name, @NonNull Pageable pageable);

//    Điếm số lượng
    @Query("""
        SELECT COUNT(v)
        FROM Venue v
    """)
    int countTotalVenue();
}
