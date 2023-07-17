package com.example.courseselection.models;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractAuditingEntity implements Serializable {
	
	@CreatedBy
	@JsonIgnore
	@Column(length=50, updatable=false)
	private String createdBy;
	
	@CreatedDate
	@JsonIgnore
	@Column(updatable=false)
	private Instant createdDate = Instant.now();
	
	@LastModifiedBy
	@JsonIgnore
	@Column(length=50)
	private String lastModifiedBy;
	
	@LastModifiedDate
	@JsonIgnore
	private Instant lastModifiedDate = Instant.now();

}
