package com.laborator8.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
interface StudentRepository extends JpaRepository<Student, Integer> {
    Optional<Student> getStudentById(int id);
}

