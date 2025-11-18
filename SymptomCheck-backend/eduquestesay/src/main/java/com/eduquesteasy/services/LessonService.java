package com.eduquesteasy.services;

import com.eduquesteasy.models.Lesson;
import com.eduquesteasy.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private LessonRepository lessonRepository;

    // 🔹 Create or update a lesson
    public Lesson saveLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    // 🔹 Get all lessons
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    // 🔹 Get a lesson by ID
    public Optional<Lesson> getLessonById(Long id) {
        return lessonRepository.findById(id);
    }

    // 🔹 Delete a lesson
    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    // 🔹 Get all lessons for a specific course
    public List<Lesson> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId);
    }
}
