package com.example.courseselection.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.courseselection.models.Course;
import com.example.courseselection.models.dto.CourseDto;
import com.example.courseselection.models.dto.CourseTNDto;


public interface CourseRepository extends JpaRepository<Course, Long>  {
	
	@Query("SELECT new com.example.courseselection.models.dto.CourseDto("
			+ "c.courseName, c.courseLocation, c.teacherId) from Course c")
	List<CourseDto> findAllDtos();
	
	@Query("""
			SELECT new com.example.courseselection.models.dto.CourseTNDto(
				c.courseName, c.courseLocation, t.username) from Course c
					left join User t on c.teacherId = t.id
			""")
	List<CourseTNDto> findAllCourseWithTeacherName();

	Optional<Course> findCourseByCourseName(String courseName);

}
