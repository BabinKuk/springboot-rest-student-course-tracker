package org.babinkuk.dao;

import org.babinkuk.entity.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository extends CrudRepository<Course, Integer> {
	
}
