package com.eduquesteasy.services;

import com.eduquesteasy.models.Course;
import com.eduquesteasy.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // Create or update a course
    public Course saveCourse(Course course) {
        return courseRepository.save(course);
    }

    //  Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    //  Get a course by ID
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    //  Delete a course
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    //  Find courses by category
    public List<Course> getCoursesByCategory(String category) {
        return courseRepository.findByCategory(category);
    }

    //  Find courses by level
    public List<Course> getCoursesByLevel(String level) {
        return courseRepository.findByLevel(level);
    }

    //  Find courses by teacher email
    public List<Course> getCoursesByTeacherEmail(String teacherEmail) {
        return courseRepository.findByTeacherEmail(teacherEmail);
    }

    //  Search courses by title (contains keyword)
    public List<Course> searchCoursesByTitle(String title) {
        return courseRepository.findByTitleContainingIgnoreCase(title);
    }
}
