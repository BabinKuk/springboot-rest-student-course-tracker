package org.babinkuk.dao;

import java.util.Optional;

import org.babinkuk.entity.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Integer> {
	
	// optional
	public Optional<Student> findByEmail(String email);
}
