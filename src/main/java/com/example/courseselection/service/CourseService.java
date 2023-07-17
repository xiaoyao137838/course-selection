package com.example.courseselection.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.courseselection.models.Course;
import com.example.courseselection.models.User;
import com.example.courseselection.models.UserCourse;
import com.example.courseselection.models.dto.CourseDto;
import com.example.courseselection.models.dto.CourseTNDto;
import com.example.courseselection.repository.CourseRepository;
import com.example.courseselection.repository.UserCourseRepository;
import com.example.courseselection.service.UserService;

@Service
@Transactional
public class CourseService {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private CourseRepository courseRepository;
	
	private UserCourseRepository userCourseRepository;
	
	private UserService userService;

	public CourseService(CourseRepository courseRepository, 
			UserService userService, UserCourseRepository userCourseRepository) {
		super();
		this.courseRepository = courseRepository;
		this.userService = userService;
		this.userCourseRepository = userCourseRepository;
	}


	public List<Course> findAllCourses() {
		return courseRepository.findAll();
	}


	public List<CourseDto> findAllCourseDtos() {
		return courseRepository.findAllDtos();
	}


	public List<CourseTNDto> findAllCourseTNDtos() {
		return courseRepository.findAllCourseWithTeacherName();
	}


	public void registerCourse(String courseName) throws Exception {
		Optional<User> currentUser = userService.getUserWithAuthorities();
		Optional<Course> currentCourse = courseRepository.findCourseByCourseName(courseName);
		
		if (currentUser.isPresent() && currentCourse.isPresent()) {
			Course course = currentCourse.get();
			User user = currentUser.get();
			List<UserCourse> existingUserCourses = userCourseRepository.findUserCourseByUser(user);
			
			boolean registeredBefore = existingUserCourses.stream()
					.anyMatch(userCourse -> userCourse.getCourse().equals(course));
			
			if (registeredBefore) {
				throw new Exception("You have registered this course before.");
			}
			
			UserCourse userCourse = UserCourse.builder()
					.course(currentCourse.get())
					.user(currentUser.get())
					.build();
			
			userCourseRepository.save(userCourse);
		} else {
			throw new Exception("The course cannot be registered.");
		}
	}


	public void addCourse(Course course) throws Exception {
		String name = course.getCourseName();
		Optional<Course> courseOptional = courseRepository.findCourseByCourseName(name);
		if (courseOptional.isPresent()) {
			throw new Exception("This course exists already.");
		}
		
		courseRepository.save(course);		
	}


	public void updateCourse(CourseDto courseDto) throws Exception {
		logger.info("CourseDto for update: {}", courseDto);
		
		String name = courseDto.name();
		Optional<Course> courseOptional = courseRepository.findCourseByCourseName(name);
		if (courseOptional.isEmpty()) {
			throw new Exception("This course does not exist for update.");
		}
		
		Course course = courseOptional.get();
		logger.info("Course for update: {}", course);
		
		course.setCourseLocation(courseDto.location());
		course.setTeacherId(courseDto.teacherId());
		courseRepository.saveAndFlush(course);
	}


	public void deleteCourse(String courseName) throws Exception {
		logger.info("Course to be deleted is {}", courseName);
		
		Optional<Course> courseOptional = courseRepository.findCourseByCourseName(courseName);
		if (courseOptional.isEmpty()) {
			throw new Exception("This course does not exist for delete.");
		}
		
		courseRepository.delete(courseOptional.get());
		
	}

}
