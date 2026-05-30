// CardFidelitateRepository.java
package com.example.cafenea.repository;
import com.example.cafenea.model.CardFidelitate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardFidelitateRepository extends JpaRepository<CardFidelitate, Long> {
}