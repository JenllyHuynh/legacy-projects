package com.fpt.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "CoursePrerequisite")
public class CoursePrerequisite {
    @EmbeddedId
    private CoursePrerequisiteId id;

    @MapsId("courseId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @MapsId("prerequisiteCourseId")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "prerequisite_course_id", nullable = false)
    private Course prerequisiteCourse;

    public CoursePrerequisiteId getId() {
        return id;
    }

    public void setId(CoursePrerequisiteId id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Course getPrerequisiteCourse() {
        return prerequisiteCourse;
    }

    public void setPrerequisiteCourse(Course prerequisiteCourse) {
        this.prerequisiteCourse = prerequisiteCourse;
    }

}