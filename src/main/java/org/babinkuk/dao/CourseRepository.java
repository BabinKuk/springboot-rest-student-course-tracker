package org.babinkuk.dao;

import java.util.Optional;

import org.babinkuk.entity.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> {

	Optional<Course> findByTitle(String title);
	
}
