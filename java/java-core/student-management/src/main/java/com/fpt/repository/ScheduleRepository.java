package com.fpt.repository;

import com.fpt.entity.Schedule;
import com.fpt.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ScheduleRepository {

    public List<Schedule> findByClassSectionId(Integer classSectionId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select s from Schedule s where s.classSection.id = :classSectionId",
                            Schedule.class
                    )
                    .setParameter("classSectionId", classSectionId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    // LỆNH MỚI: Xóa toàn bộ lịch cũ của lớp và chèn lịch mới vào DB
    public void updateClassSchedules(Integer classSectionId, List<Schedule> newSchedules) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 1. Xóa lịch cũ của lớp này đi
            em.createQuery("delete from Schedule s where s.classSection.id = :classId")
                    .setParameter("classId", classSectionId)
                    .executeUpdate();

            // 2. Lưu từng buổi học mới vào
            for (Schedule s : newSchedules) {
                em.persist(s);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}