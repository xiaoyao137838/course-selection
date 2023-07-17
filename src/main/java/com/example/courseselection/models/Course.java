package com.example.courseselection.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(unique=true, nullable=false, columnDefinition="bigint")
	private long id;
	
	@Column(nullable=false, length=100, columnDefinition="nvarchar(100)")
	private String courseName;
	
	@Column(nullable=false, length=50, columnDefinition="nvarchar(50)")
	private String courseLocation;
	
	@Column(nullable=false, length=200)
	private String courseContent;
	
	@Column(nullable=false)
	private long teacherId;

}
