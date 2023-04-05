package org.babinkuk.dao;

import java.util.Optional;

import org.babinkuk.entity.Instructor;
import org.springframework.data.repository.CrudRepository;

public interface InstructorRepository extends CrudRepository<Instructor, Integer> {
	
	// optional
	public Optional<Instructor> findByEmail(String email);
}
