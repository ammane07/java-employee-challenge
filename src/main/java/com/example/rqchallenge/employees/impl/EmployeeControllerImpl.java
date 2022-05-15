package com.example.rqchallenge.employees.impl;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.employees.model.DeleteResponse;
import com.example.rqchallenge.employees.model.Employee;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class EmployeeControllerImpl implements IEmployeeController {
    private static Logger LOGGER = LoggerFactory.getLogger(EmployeeControllerImpl.class);

    @Autowired
    private IEmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        LOGGER.trace("getAllEmployees() method called");
        List<Employee> employeeList = employeeService.getAllEmployees();

        if (employeeList != null && employeeList.size() > 0) {
            return new ResponseEntity<>(employeeList, HttpStatus.OK);
        }

        LOGGER.trace("Returning getAllEmployees() method");
        return new ResponseEntity<>(employeeList, HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        LOGGER.trace("getEmployeesByNameSearch() method called with searchString : " + searchString);

        if (StringUtils.isBlank(searchString)){
            LOGGER.error("searchString cant be null or blank");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "searchString can't be null or bank");
        }

        List<Employee> employeeList = employeeService.getEmployeesByNameSearch(searchString);
        if (employeeList != null && employeeList.size() > 0) {
            return new ResponseEntity<>(employeeList, HttpStatus.FOUND);
        }

        LOGGER.info("Got blank employee list for searchString : " + searchString);
        LOGGER.trace("Returning getEmployeesByNameSearch() method");
        return new ResponseEntity<>(employeeList, HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        LOGGER.trace("getEmployeeById() method called with id : " + id);

        if (StringUtils.isBlank(id)){
            LOGGER.error("id cant be blank or null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id can't be blank or null");
        }

        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            LOGGER.info("Employee not found for the id : " + id);
            return ResponseEntity.notFound().build();
        }
        LOGGER.trace("Returning getEmployeeById() method");
        return new ResponseEntity<>(employee, HttpStatus.FOUND);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        LOGGER.trace("getHighestSalaryOfEmployees() method called");
        Integer highestSalary = employeeService.getHighestSalaryOfEmployees();

        if (highestSalary == null) {
            LOGGER.info("Couldn't not found employee with highest salary");
            return ResponseEntity.noContent().build();
        }

        LOGGER.trace("Returning getHighestSalaryOfEmployees() method");
        return new ResponseEntity<>(highestSalary, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        LOGGER.trace("getTopTenHighestEarningEmployeeNames() method called");
        List<String> topTenSalaryEmployeeNames = employeeService.getTopTenHighestEarningEmployeeNames();

        if (topTenSalaryEmployeeNames == null){
            LOGGER.info("Couldn't not found top ten highest earning employees");
            return ResponseEntity.noContent().build();
        }
        LOGGER.trace("Returning getTopTenHighestEarningEmployeeNames() method");
        return new ResponseEntity<>(topTenSalaryEmployeeNames, HttpStatus.OK);
    }

    @Override
    public ResponseEntity createEmployee(Map<String, Object> employeeInput) {
        LOGGER.trace("createEmployee() method called");

        if (employeeInput.containsKey("name") && employeeInput.containsKey("age")
                && employeeInput.containsKey("salary") ) {
            Employee employee = employeeService.createEmployee(employeeInput);

            LOGGER.trace("Returning createEmployee() method");
            return new ResponseEntity<>(employee, HttpStatus.CREATED);
        } else {
            LOGGER.error("Invalid request to create employee. Please enter name, age and salary fields for employee");
            return ResponseEntity.badRequest().body("Please enter name, age and salary fields for employee");
        }
    }

    @Override
    public ResponseEntity<DeleteResponse> deleteEmployeeById(String id) {
        LOGGER.trace("deleteEmployeeById() method called with id : " + id);
        DeleteResponse response = employeeService.deleteEmployeeById(id);

        LOGGER.trace("Returning deleteEmployeeById() method");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
