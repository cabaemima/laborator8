package com.laborator8.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laborator8.student.Student;
import com.laborator8.student.StudentController;
import com.laborator8.student.StudentService;
import com.laborator8.utils.StudentDTO;
import com.sun.istack.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StudentController.class)
@AutoConfigureMockMvc
public class StudentControllerTest {

    @Autowired
    MockMvc mvc;
    @MockBean
    StudentService studentService;

    List<Student> initialStudents = List.of(
            new Student(1, "Mike", 20),
            new Student(2, "Todd", 21),
            new Student(3, "Jim", 19)
    );


    private String toJson(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    private StudentDTO toDto(Student student) {
        return new StudentDTO(student.getId(), student.getName());
    }

    @Test
    @DisplayName("WHEN requesting all students THEN empty list is returned if no students")
    @WithMockUser(username = "user", roles = "user")
    void testGetAllStudentsEmptyList() throws Exception {
        mvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("WHEN requesting all students THEN all student are returned")
    @WithMockUser(username = "user", roles = "user")
    void testGetAllStudents() throws Exception {
        List<StudentDTO> initialStudentsDTO = initialStudents.stream()
                .map(student -> new StudentDTO(student.getId(), student.getName()))
                .collect(Collectors.toList());
        when(studentService.getAll()).thenReturn(initialStudents);

        mvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(content().json(toJson(initialStudentsDTO)));
    }

    @Test
    @DisplayName("WHEN requesting a student by id THEN the student with that id is returned")
    @WithMockUser(username = "user", roles = "user")
    void testGetOneStudent() throws Exception {
        when(studentService.getOne(1)).thenReturn(ofNullable(initialStudents.get(1)));

        mvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(content().json(toJson(toDto(initialStudents.get(1)))));
    }

    @Test
    @DisplayName("WHEN new student is added THEN the student is saved")
    @WithMockUser(username = "user", roles = "user")
    void testAddStudent() throws Exception {
        when(studentService.addStudent(any(Student.class)))
                .then(returnFirstArgumentWithId());

        mvc.perform(post("/students")
                .contentType(APPLICATION_JSON_VALUE)
                .content(toJson(new Student(4, "John", 20))))
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").exists());
    }

    @NotNull
    private Answer<Object> returnFirstArgumentWithId() {
        return invocation -> {
            Student argument = invocation.getArgument(0);
            argument.setId(1);
            return argument;
        };
    }


    @Test
    @DisplayName("WHEN deleting a student by id THEN the student will be removed")
    @WithMockUser(username = "user", roles = "user")
    void testDeleteStudent() throws Exception {
        Student student = new Student(1, "John", 20);

        when(studentService.deleteStudentByID(student.getId())).thenReturn(student);

        mvc.perform(delete("/students/" + student.getId())
                .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("WHEN editing a student THEN the student's values are updated")
    @WithMockUser(username = "user", roles = "user")
    void testEditStudent() throws Exception {
        Student newStudent = new Student(1, "John", 22);
        when(studentService.editStudent(1, newStudent)).thenReturn(newStudent);
        mvc.perform(put("/students/1")
                .content(toJson(newStudent))
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }
}
