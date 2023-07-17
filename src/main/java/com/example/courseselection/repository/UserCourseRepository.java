package com.example.courseselection.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseselection.models.User;
import com.example.courseselection.models.UserCourse;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long>  {

	List<UserCourse> findUserCourseByUser(User user);

}
