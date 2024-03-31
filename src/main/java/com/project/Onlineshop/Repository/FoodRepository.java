package com.project.Onlineshop.Repository;

import com.project.Onlineshop.Entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<Food, Long> {
}
