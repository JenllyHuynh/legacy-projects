package org.example.Reponsitory;

import org.example.Entities.orchid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface orchidReponsitory extends JpaRepository<orchid, Integer> {
    void deleteByName(String name);


}
