package com.example.CompletableFutureDemo.controller;

import com.example.CompletableFutureDemo.entity.Employee;
import com.example.CompletableFutureDemo.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
public class EmployeeController {

    @Autowired
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/add")
    public ResponseEntity addEmployee(@RequestBody Employee employee){
        return employeeService.addEmployee(employee);
    }

    ObjectMapper mapper=new ObjectMapper();
    @GetMapping("/getAll/employees")
    public ResponseEntity getAllEmployees(){
        //System.out.println("Inside get all");
        return employeeService.getAllEmployees();
    }

    @GetMapping("/getAll/completable")
    @Async
    public CompletableFuture<ResponseEntity> getAllByCompletableFuture(){
        return CompletableFuture.supplyAsync(()->{
            try {
                return employeeService.getAllEmployeesEmpletable();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @PostMapping("/update/emp/byId")
    public CompletableFuture<ResponseEntity> updateEmployee(@RequestParam Long empId,@RequestBody Employee emp){
        return CompletableFuture.supplyAsync(()->{
            try {
                ResponseEntity response = employeeService.updateEmployeeById(empId,emp);
               // System.out.println(response);
                if (response != null && response.hasBody()){
                    Object body = response.getBody();
                    Employee employee = mapper.convertValue(body, Employee.class);
                    //Class<?> emp=body.getClass();
                    if(employee.getId()==null)
                        return new ResponseEntity("Employee not found with given id: "+empId,HttpStatus.NOT_FOUND);
                    return response;
                }
                return response;
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
