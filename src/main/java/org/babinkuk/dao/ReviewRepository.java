package org.babinkuk.dao;

import org.babinkuk.entity.Review;
import org.springframework.data.repository.CrudRepository;

public interface ReviewRepository extends CrudRepository<Review, Integer> {
	
}
