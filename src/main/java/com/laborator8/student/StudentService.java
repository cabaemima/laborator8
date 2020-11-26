package com.laborator8.student;

import com.laborator8.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public Optional<Student> getOne(int id) {
        return studentRepository.getStudentById(id);
    }

    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student editStudent(int id, Student student) {
        studentRepository.getStudentById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Could not find id " + id);
                });
        studentRepository.save(student);
        return student;
    }

    public Student deleteStudentByID(int id) {
        Student student = studentRepository.getStudentById(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Could not find id " + id);
                });
        studentRepository.delete(student);
        return student;
    }
}
