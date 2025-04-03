package com.example.CompletableFutureDemo.service.impl;

import com.example.CompletableFutureDemo.entity.Employee;
import com.example.CompletableFutureDemo.repository.EmployeeRepository;
import com.example.CompletableFutureDemo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    Executor executor= Executors.newFixedThreadPool(10);

    @Override
    public ResponseEntity addEmployee(Employee employee) {
        Employee emp= employeeRepository.save(employee);
        if(emp != null && emp.getId() !=null){
            return new ResponseEntity(emp, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
    }

    @Override
    public ResponseEntity getAllEmployees() {

        List<Employee> empList=employeeRepository.findAll();
        System.out.println(empList);
        if (empList !=null && empList.size()>0)
            return new ResponseEntity(empList,HttpStatus.OK);

        return new ResponseEntity(null,HttpStatus.NOT_FOUND);
    }

    @Override
    @Async
    public ResponseEntity getAllEmployeesEmpletable() throws ExecutionException, InterruptedException {
        CompletableFuture<List<Employee>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
            return employeeRepository.findAll();
        },executor);
        return new ResponseEntity(listCompletableFuture.get(),HttpStatus.OK);
    }

    @Override
    @Async
    public ResponseEntity updateEmployeeById(Long empId,Employee emp) throws ExecutionException, InterruptedException {
//        CompletableFuture<Employee> empFuture=CompletableFuture.supplyAsync(()->{
//            return employeeRepository.findById(empId);
//        },executor).thenApply(name ->{
//            name.get().setName("Ganesh C");
//            return employeeRepository.save(name.get());
//        });
//        return new ResponseEntity(empFuture.get(),HttpStatus.OK);

//        CompletableFuture<CompletableFuture<Employee>> result=getEmployee(empId)
//                .thenApply(employee -> updateEmployee(employee));
//        return new ResponseEntity(result.get().get(),HttpStatus.OK);

        CompletableFuture<Employee> result=getEmployee(empId)
                .thenCompose(employee -> {
                    employee.setName(emp.getName());
                    //employee.setEmployeeId(emp.getEmployeeId());
                    return updateEmployee(employee);
                });
        if(result.get().getId()!= null)
            return new ResponseEntity(result.get(),HttpStatus.NOT_FOUND);
        return new ResponseEntity(result.get(),HttpStatus.OK);
    }


    @Async
    public CompletableFuture<Employee> getEmployee(Long empId){
        return CompletableFuture.supplyAsync(()->{
            return employeeRepository.findById(empId).get();
        }).handle((res,ex)->{
            if(ex != null){
                return new Employee();
            }
            return res;
        });
//                exceptionally(ex->{
//            return new Employee();
//        });
    }

    @Async
    public CompletableFuture<Employee> updateEmployee(Employee emp){
        return CompletableFuture.supplyAsync(()->{
            if(emp != null && emp.getId()!=null){
                //emp.setName("Ganesh C");
                return employeeRepository.save(emp);
            }else
                return new Employee();

        }).handle((res,ex)->{
            if(ex != null)
                return new Employee();
            return res;
        });
    }
}
