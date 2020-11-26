package com.laborator8.student;

import com.laborator8.utils.StudentDTO;
import org.springframework.web.bind.annotation.*;

import com.laborator8.exception.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public List<Student> getAll() {

        return studentService.getAll();
    }

    @GetMapping("/{id}")
    public StudentDTO getOne(@PathVariable int id) {
        Student student = studentService.getOne(id)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Could not find id " + id);
                });
        return new StudentDTO(student.getId(), student.getName());
    }

    @PostMapping
    public StudentDTO addStudent(@RequestBody Student student) {
        Student studentAdded = studentService.addStudent(new Student(student.getId(), student.getName(), student.getAge()));
        return new StudentDTO(studentAdded.getId(), studentAdded.getName());
    }

    @PutMapping("/{id}")
    public StudentDTO editStudent(@PathVariable int id, @RequestBody StudentDTO student) {
        Student editedStudent = studentService.editStudent(id, new Student(student.getId(), student.getName()));
        return new StudentDTO(editedStudent.getId(), editedStudent.getName());
    }

    @DeleteMapping("/{id}")
    public Student deleteStudent(@PathVariable int id) {
        return studentService.deleteStudentByID(id);
    }
}