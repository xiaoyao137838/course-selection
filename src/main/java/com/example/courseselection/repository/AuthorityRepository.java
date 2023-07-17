package com.example.courseselection.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.courseselection.models.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String>  {
}
