package com.example.courseselection.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.courseselection.models.Course;
import com.example.courseselection.models.dto.CourseDto;
import com.example.courseselection.models.dto.CourseTNDto;
import com.example.courseselection.service.CourseService;

import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api")
public class CourseController {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private CourseService courseService;

	public CourseController(CourseService courseService) {
		super();
		this.courseService = courseService;
	}
	
	@GetMapping(path = "/course/findAllCourses", produces="application/json")
	public ResponseEntity<List<Course>> findAllCourses() {
		List<Course> courses = courseService.findAllCourses();
		return new ResponseEntity<>(courses, HttpStatus.OK);
	}
	
	@GetMapping(path = "/course/findAllCourseDtos", produces="application/json")
	public ResponseEntity<List<CourseDto>> findAllCourseDtos() {
		List<CourseDto> courseDtos = courseService.findAllCourseDtos();
		return new ResponseEntity<>(courseDtos, HttpStatus.OK);
	}
	
	@GetMapping(path = "/course/findAllCourseTNDtos", produces="application/json")
	public ResponseEntity<List<CourseTNDto>> findAllCourseTNDtos() {
		List<CourseTNDto> courseTNDtos = courseService.findAllCourseTNDtos();
		return new ResponseEntity<>(courseTNDtos, HttpStatus.OK);
	}
	
	@PostMapping("/course/register/{courseName}")
	public HttpStatus registerCourse(@PathVariable @NotNull String courseName) {
		try {
			courseService.registerCourse(courseName);
			return HttpStatus.OK;
		} catch (Exception e) {
			logger.error("Something wrong with register course: {}", e.getMessage());
			return HttpStatus.BAD_REQUEST;
		}
	}
	
	@PostMapping("/course/add")
	public HttpStatus addCourse(@RequestBody @NotNull Course course) {
		try {
			courseService.addCourse(course);
			return HttpStatus.OK;
		} catch (Exception e) {
			logger.error("Something wrong with add course: {}", e.getMessage());
			return HttpStatus.BAD_REQUEST;
		}
	}
	
	@PutMapping("/course/update")
	public HttpStatus updateCourse(@RequestBody @NotNull CourseDto courseDto) {
		try {
			courseService.updateCourse(courseDto);
			return HttpStatus.OK;
		} catch (Exception e) {
			logger.error("Something wrong with update course: {}", e.getMessage());
			return HttpStatus.BAD_REQUEST;
		}
	}
	
	@DeleteMapping("/course/delete/{courseName}")
	public HttpStatus deleteCourse(@PathVariable @NotNull String courseName) {
		try {
			courseService.deleteCourse(courseName);
			return HttpStatus.OK;
		} catch (Exception e) {
			logger.error("Something wrong with delete course: {}", e.getMessage());
			return HttpStatus.BAD_REQUEST;
		}
	}

}
