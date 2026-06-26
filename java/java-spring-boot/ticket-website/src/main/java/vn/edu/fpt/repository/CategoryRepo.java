package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {

    // Check tên đã tồn tại chưa (dùng khi create)
    boolean existsByName(String name);

    // Check tên đã tồn tại chưa nhưng loại trừ chính nó (dùng khi update)
    boolean existsByNameAndIdNot(String name, Integer id);

        @Query("SELECT c.id, c.name, c.iconName, COUNT(e) " +
                "FROM Category c LEFT JOIN c.events e " +
                "GROUP BY c.id, c.name, c.iconName")
        List<Object[]> findAllWithEventCount();
    }
