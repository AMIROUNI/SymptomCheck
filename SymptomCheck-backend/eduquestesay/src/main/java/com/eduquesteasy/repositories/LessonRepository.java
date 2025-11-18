package com.eduquesteasy.repositories;

import com.eduquesteasy.models.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // 🔹 Find lessons belonging to a course
    List<Lesson> findByCourseId(Long courseId);

    // 🔹 Find lessons ordered by their index within a course
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);
    List<Lesson>  findByCourseIdAndTitle(Long Id , String title);
}
