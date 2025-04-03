package com.example.CompletableFutureDemo.service;

import com.example.CompletableFutureDemo.entity.Employee;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.ExecutionException;

public interface EmployeeService {
    ResponseEntity addEmployee(Employee employee);

    ResponseEntity getAllEmployees();

    ResponseEntity getAllEmployeesEmpletable() throws ExecutionException, InterruptedException;

    ResponseEntity updateEmployeeById(Long empId,Employee emp) throws ExecutionException, InterruptedException;
}
