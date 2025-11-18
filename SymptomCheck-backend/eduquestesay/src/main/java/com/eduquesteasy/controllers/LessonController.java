package com.eduquesteasy.controllers;

import com.eduquesteasy.models.Lesson;
import com.eduquesteasy.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/lessons")
@CrossOrigin(origins = "*")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    // 🔹 Get all lessons
    @GetMapping
    public List<Lesson> getAllLessons() {
        return lessonService.getAllLessons();
    }

    // 🔹 Get lesson by ID
    @GetMapping("/{id}")
    public ResponseEntity<Lesson> getLessonById(@PathVariable Long id) {
        Optional<Lesson> lesson = lessonService.getLessonById(id);
        return lesson.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Create new lesson
    @PostMapping
    public Lesson createLesson(@RequestBody Lesson lesson) {
        return lessonService.saveLesson(lesson);
    }

    // 🔹 Update lesson
    @PutMapping("/{id}")
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long id, @RequestBody Lesson lessonDetails) {
        Optional<Lesson> existingLesson = lessonService.getLessonById(id);

        if (existingLesson.isPresent()) {
            Lesson lesson = existingLesson.get();
            lesson.setTitle(lessonDetails.getTitle());
            lesson.setContent(lessonDetails.getContent());
            lesson.setVideoUrl(lessonDetails.getVideoUrl());
            lesson.setPdfFile(lessonDetails.getPdfFile());
            lesson.setOrderIndex(lessonDetails.getOrderIndex());
            lesson.setCourse(lessonDetails.getCourse());

            Lesson updatedLesson = lessonService.saveLesson(lesson);
            return ResponseEntity.ok(updatedLesson);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Delete lesson
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable Long id) {
        if (lessonService.getLessonById(id).isPresent()) {
            lessonService.deleteLesson(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 🔹 Get lessons by course ID
    @GetMapping("/course/{courseId}")
    public List<Lesson> getLessonsByCourse(@PathVariable Long courseId) {
        return lessonService.getLessonsByCourse(courseId);
    }
}