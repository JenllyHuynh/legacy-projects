package com.fpt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CoursePrerequisiteId implements Serializable {
    private static final long serialVersionUID = -8618927517618897397L;
    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "prerequisite_course_id", nullable = false)
    private Integer prerequisiteCourseId;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getPrerequisiteCourseId() {
        return prerequisiteCourseId;
    }

    public void setPrerequisiteCourseId(Integer prerequisiteCourseId) {
        this.prerequisiteCourseId = prerequisiteCourseId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoursePrerequisiteId entity = (CoursePrerequisiteId) o;
        return Objects.equals(this.courseId, entity.courseId) &&
                Objects.equals(this.prerequisiteCourseId, entity.prerequisiteCourseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, prerequisiteCourseId);
    }
}