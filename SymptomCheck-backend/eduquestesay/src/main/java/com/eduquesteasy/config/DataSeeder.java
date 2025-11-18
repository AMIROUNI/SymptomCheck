package com.eduquesteasy.config;

import com.eduquesteasy.models.Course;
import com.eduquesteasy.models.Lesson;
import com.eduquesteasy.repositories.CourseRepository;
import com.eduquesteasy.repositories.LessonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (isDatabaseEmpty()) {
            System.out.println("🔄 Seeding database with initial data...");

            // Create and save courses with lessons
            createWebDevelopmentCourse();
            createMobileAppCourse();
            createDataScienceCourse();
            createUIUXCourse();
            createBackendCourse();

            System.out.println("✅ Database seeded successfully with sample data!");
        } else {
            System.out.println("ℹ️ Database already contains data. Skipping seeding.");
        }
    }

    private boolean isDatabaseEmpty() {
        return courseRepository.count() == 0;
    }

    private void createWebDevelopmentCourse() {
        // Check if course already exists
        List<Course> existingCourses = courseRepository.findByTitleContainingIgnoreCase("Complete Web Development Bootcamp");
        if (!existingCourses.isEmpty()) {
            return;
        }

        Course course = new Course();
        course.setTitle("Complete Web Development Bootcamp");
        course.setDescription("Learn web development from scratch. HTML, CSS, JavaScript, React, Node.js and more!");
        course.setCategory("Web Development");
        course.setImageUrl("https://images.unsplash.com/photo-1627398242454-45a1465c2479?w=500&h=300&fit=crop");
        course.setLevel("Beginner");
        course.setRating(4.7);
        course.setDuration(45);
        course.setTeacherEmail("john.developer@eduquest.com");

        Course savedCourse = courseRepository.save(course);

        // Add lessons
        Lesson lesson1 = createLesson("Introduction to HTML",
                "Learn the basics of HTML structure and tags",
                "https://example.com/videos/html-intro.mp4",
                "https://www.w3.org/TR/html52/introduction.html",
                1, savedCourse);

        Lesson lesson2 = createLesson("CSS Fundamentals",
                "Master CSS styling and layout techniques",
                "https://example.com/videos/css-fundamentals.mp4",
                "https://developer.mozilla.org/en-US/docs/Web/CSS",
                2, savedCourse);

        Lesson lesson3 = createLesson("JavaScript Basics",
                "Learn JavaScript programming fundamentals",
                "https://example.com/videos/js-basics.mp4",
                "https://javascript.info/js.pdf",
                3, savedCourse);

        lessonRepository.saveAll(Arrays.asList(lesson1, lesson2, lesson3));
    }

    private void createMobileAppCourse() {
        // Check if course already exists
        List<Course> existingCourses = courseRepository.findByTitleContainingIgnoreCase("Flutter & Dart");
        if (!existingCourses.isEmpty()) {
            return;
        }

        Course course = new Course();
        course.setTitle("Flutter & Dart - Complete Development Guide");
        course.setDescription("Build beautiful native apps for iOS and Android with Flutter");
        course.setCategory("Mobile Development");
        course.setImageUrl("https://images.unsplash.com/photo-1551650975-87deedd944c3?w=500&h=300&fit=crop");
        course.setLevel("Intermediate");
        course.setRating(4.8);
        course.setDuration(38);
        course.setTeacherEmail("sarah.flutter@eduquest.com");

        Course savedCourse = courseRepository.save(course);

        Lesson lesson1 = createLesson("Flutter Setup & First App",
                "Set up Flutter environment and create your first app",
                "https://example.com/videos/flutter-setup.mp4",
                "https://flutter.dev/docs/get-started/install",
                1, savedCourse);

        Lesson lesson2 = createLesson("Widgets & Layouts",
                "Understand Flutter widgets and layout system",
                "https://example.com/videos/flutter-widgets.mp4",
                "https://flutter.dev/docs/development/ui/widgets",
                2, savedCourse);

        Lesson lesson3 = createLesson("State Management",
                "Learn state management with Provider and Bloc",
                "https://example.com/videos/state-management.mp4",
                "https://flutter.dev/docs/development/data-and-backend/state-mgmt",
                3, savedCourse);

        lessonRepository.saveAll(Arrays.asList(lesson1, lesson2, lesson3));
    }

    private void createDataScienceCourse() {
        // Check if course already exists
        List<Course> existingCourses = courseRepository.findByTitleContainingIgnoreCase("Data Science with Python");
        if (!existingCourses.isEmpty()) {
            return;
        }

        Course course = new Course();
        course.setTitle("Data Science with Python");
        course.setDescription("Master data analysis, visualization, and machine learning with Python");
        course.setCategory("Data Science");
        course.setImageUrl("https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=500&h=300&fit=crop");
        course.setLevel("Advanced");
        course.setRating(4.6);
        course.setDuration(52);
        course.setTeacherEmail("mike.datascience@eduquest.com");

        Course savedCourse = courseRepository.save(course);

        Lesson lesson1 = createLesson("Python for Data Analysis",
                "Learn Python libraries for data manipulation",
                "https://example.com/videos/python-data-analysis.mp4",
                "https://pandas.pydata.org/pandas-docs/stable/user_guide/dsintro.html",
                1, savedCourse);

        Lesson lesson2 = createLesson("Data Visualization",
                "Create stunning visualizations with Matplotlib and Seaborn",
                "https://example.com/videos/data-visualization.mp4",
                "https://matplotlib.org/stable/contents.html",
                2, savedCourse);

        Lesson lesson3 = createLesson("Machine Learning Basics",
                "Introduction to machine learning algorithms",
                "https://example.com/videos/ml-basics.mp4",
                "https://scikit-learn.org/stable/tutorial/basic/tutorial.html",
                3, savedCourse);

        lessonRepository.saveAll(Arrays.asList(lesson1, lesson2, lesson3));
    }

    private void createUIUXCourse() {
        // Check if course already exists
        List<Course> existingCourses = courseRepository.findByTitleContainingIgnoreCase("UI/UX Design Masterclass");
        if (!existingCourses.isEmpty()) {
            return;
        }

        Course course = new Course();
        course.setTitle("UI/UX Design Masterclass");
        course.setDescription("Learn user interface and user experience design principles");
        course.setCategory("Design");
        course.setImageUrl("https://images.unsplash.com/photo-1561070791-2526d30994b5?w=500&h=300&fit=crop");
        course.setLevel("Beginner");
        course.setRating(4.9);
        course.setDuration(28);
        course.setTeacherEmail("lisa.design@eduquest.com");

        Course savedCourse = courseRepository.save(course);

        Lesson lesson1 = createLesson("Design Principles",
                "Understand fundamental UI/UX design principles",
                "https://example.com/videos/design-principles.mp4",
                "https://www.nngroup.com/articles/ten-usability-heuristics/",
                1, savedCourse);

        Lesson lesson2 = createLesson("Wireframing & Prototyping",
                "Create wireframes and interactive prototypes",
                "https://example.com/videos/wireframing.mp4",
                "https://www.uxpin.com/studio/blog/wireframing/",
                2, savedCourse);

        Lesson lesson3 = createLesson("User Research Methods",
                "Learn various user research techniques",
                "https://example.com/videos/user-research.mp4",
                "https://www.interaction-design.org/literature/topics/user-research",
                3, savedCourse);

        lessonRepository.saveAll(Arrays.asList(lesson1, lesson2, lesson3));
    }

    private void createBackendCourse() {
        // Check if course already exists
        List<Course> existingCourses = courseRepository.findByTitleContainingIgnoreCase("Spring Boot & REST API");
        if (!existingCourses.isEmpty()) {
            return;
        }

        Course course = new Course();
        course.setTitle("Spring Boot & REST API Development");
        course.setDescription("Build robust backend systems with Spring Boot and REST APIs");
        course.setCategory("Backend Development");
        course.setImageUrl("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=500&h=300&fit=crop");
        course.setLevel("Intermediate");
        course.setRating(4.5);
        course.setDuration(35);
        course.setTeacherEmail("david.backend@eduquest.com");

        Course savedCourse = courseRepository.save(course);

        Lesson lesson1 = createLesson("Spring Boot Fundamentals",
                "Get started with Spring Boot framework",
                "https://example.com/videos/spring-boot-intro.mp4",
                "https://spring.io/guides/gs/spring-boot/",
                1, savedCourse);

        Lesson lesson2 = createLesson("REST API Design",
                "Design and implement RESTful APIs",
                "https://example.com/videos/rest-api-design.mp4",
                "https://restfulapi.net/",
                2, savedCourse);

        Lesson lesson3 = createLesson("Database Integration",
                "Connect Spring Boot with databases",
                "https://example.com/videos/database-integration.mp4",
                "https://spring.io/guides/gs/accessing-data-mysql/",
                3, savedCourse);

        lessonRepository.saveAll(Arrays.asList(lesson1, lesson2, lesson3));
    }

    private Lesson createLesson(String title, String content, String videoUrl, String pdfFile, int orderIndex, Course course) {
        // Check if lesson already exists for this course
        List<Lesson> existingLessons = lessonRepository.findByCourseIdAndTitle(course.getId(), title);
        if (!existingLessons.isEmpty()) {
            return existingLessons.get(0); // Return existing lesson
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setVideoUrl(videoUrl);
        lesson.setPdfFile(pdfFile);
        lesson.setOrderIndex(orderIndex);
        lesson.setCourse(course);
        return lesson;
    }
}