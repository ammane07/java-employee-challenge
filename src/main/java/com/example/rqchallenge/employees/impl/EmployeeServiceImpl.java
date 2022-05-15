package com.example.rqchallenge.employees.impl;

import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.employees.model.DeleteResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.rqchallenge.employees.Constants.*;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    private static Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final WebClient webClient;

    public EmployeeServiceImpl(WebClient.Builder builder,
                               @Value("${api.serverUrl}") String serverUrl) {
        LOGGER.info("Server url is : " + serverUrl);
        webClient = builder.baseUrl(serverUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<Employee> getAllEmployees() {
        LOGGER.trace("getAllEmployees() method called");
        EmployeesResponse employeesResponse = webClient.get()
                .uri(GET_ALL_EMPLOYEES)
                .retrieve()
                .bodyToMono(EmployeesResponse.class)
                .block();

        if (!employeesResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to get employee list. Error : " + employeesResponse.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, employeesResponse.getMessage());
        }

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }
        return Arrays.asList(employeesResponse.getData());
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        LOGGER.trace("getEmployeesByNameSearch() method called with searchString : " + searchString);
        EmployeesResponse employeesResponse = webClient.get()
                .uri(GET_ALL_EMPLOYEES)
                .retrieve()
                .bodyToMono(EmployeesResponse.class)
                .block();

        if (!employeesResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to get employee list. Error : " + employeesResponse.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, employeesResponse.getMessage());
        }

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }

        List<Employee> employeeList = Arrays.asList(employeesResponse.getData());

        LOGGER.trace("Returning getEmployeesByNameSearch() method");
        return employeeList.stream().filter(employee -> employee.getEmployeeName().contains(searchString)).collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeById(String id) {
        LOGGER.trace("getEmployeeById() method called with id : " + id);
        String uri = String.format(GET_EMPLOYEE_BY_ID, id);

        EmployeeResponse employeeResponse = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .block();

        if (!employeeResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to get employee list.");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.trace("Returning getEmployeeById() method");
        return employeeResponse.getData();
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        LOGGER.trace("getHighestSalaryOfEmployees() method called");
        EmployeesResponse employeesResponse = webClient.get().
                uri(GET_ALL_EMPLOYEES)
                .retrieve()
                .bodyToMono(EmployeesResponse.class)
                .block();

        if (!employeesResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to get employee list. Error : " + employeesResponse.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, employeesResponse.getMessage());
        }

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }

        List<Employee> empList = List.of(employeesResponse.getData());
        Optional<Employee> employee = empList.stream()
                .sorted(Comparator.comparing(Employee::getEmployeeSalary)
                .reversed())
                .findFirst();

        if (employee.get() == null) {
            LOGGER.info("Didn't got employee object. Returning null");
            return null;
        }

        LOGGER.trace("Returning getHighestSalaryOfEmployees() method");
        return employee.get().getEmployeeSalary();
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        LOGGER.trace("getTopTenHighestEarningEmployeeNames() method called");
        EmployeesResponse employeesResponse = webClient.get()
                .uri(GET_ALL_EMPLOYEES)
                .retrieve()
                .bodyToMono(EmployeesResponse.class)
                .block();

        if (!employeesResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to get employee list. Error : " + employeesResponse.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, employeesResponse.getMessage());
        }

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }

        List<Employee> empList = List.of(employeesResponse.getData());
        List<String> topTenSalaryEmpNames = empList.stream()
                .sorted(Comparator.comparing(Employee::getEmployeeSalary)
                .reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());

        LOGGER.trace("Returning getTopTenHighestEarningEmployeeNames() method");
        return topTenSalaryEmpNames;
    }

    @Override
    public Employee createEmployee(Map<String, Object> employeeInput) {
        LOGGER.trace("createEmployee() method called");

        Employee employee = new Employee();
        employee.setEmployeeName(employeeInput.get("name").toString());
        employee.setEmployeeSalary(Integer.parseInt(employeeInput.get("salary").toString()));
        employee.setEmployeeAge(Integer.parseInt(employeeInput.get("age").toString()));

        EmployeeResponse employeeResponse = webClient.post()
                .uri(CREATE_EMPLOYEE)
                .body(Mono.just(employee), Employee.class)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .block();

        if (!employeeResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to create employee");
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        LOGGER.trace("Returning createEmployee() method");
        return employeeResponse.getData();
    }

    @Override
    public DeleteResponse deleteEmployeeById(String id) {
        LOGGER.trace("deleteEmployeeById() method called with id : " + id);
        String uri = String.format(DELETE_EMPLOYEE, id);

        DeleteResponse deleteResponse = webClient.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(DeleteResponse.class)
                .block();

        if (!deleteResponse.getStatus().equals(SUCCESS_STATUS)){
            LOGGER.error("Failed to delete employee with id : " + id);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, deleteResponse.getMessage());
        }

        LOGGER.trace("Returning deleteEmployeeById() method");
        return deleteResponse;
    }
}
