package com.example.rqchallenge.employees.impl;

import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }
        List<Employee> employeeList = Arrays.asList(employeesResponse.getData());

        LOGGER.trace("Returning getEmployeesByNameSearch() method");
        return employeeList.stream().filter(employee -> employee.getEmployee_name().contains(searchString)).collect(Collectors.toList());
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

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }

        List<Employee> empList = List.of(employeesResponse.getData());
        Optional<Employee> employee = empList.stream()
                .sorted(Comparator.comparing(Employee::getEmployee_salary)
                .reversed())
                .findFirst();

        if (employee.get() == null) {
            LOGGER.info("Didn't got employee object. Returning null");
            return null;
        }

        LOGGER.trace("Returning getHighestSalaryOfEmployees() method");
        return employee.get().getEmployee_salary();
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        LOGGER.trace("getTopTenHighestEarningEmployeeNames() method called");
        EmployeesResponse employeesResponse = webClient.get()
                .uri(GET_ALL_EMPLOYEES)
                .retrieve()
                .bodyToMono(EmployeesResponse.class)
                .block();

        if (employeesResponse.getData() == null) {
            LOGGER.info("Didn't got employees from server. Returning null");
            return null;
        }

        List<Employee> empList = List.of(employeesResponse.getData());
        List<String> topTenSalaryEmpNames = empList.stream()
                .sorted(Comparator.comparing(Employee::getEmployee_salary)
                        .reversed())
                .limit(10)
                .map(Employee::getEmployee_name)
                .collect(Collectors.toList());

        LOGGER.trace("Returning getTopTenHighestEarningEmployeeNames() method");
        return topTenSalaryEmpNames;
    }

    @Override
    public Employee createEmployee(Map<String, Object> employeeInput) {
        LOGGER.trace("createEmployee() method called");

        Employee employee = new Employee();
        employee.setEmployee_name(employeeInput.get("name").toString());
        employee.setEmployee_salary(Integer.parseInt(employeeInput.get("salary").toString()));
        employee.setEmployee_age(Integer.parseInt(employeeInput.get("age").toString()));

        EmployeeResponse employeeResponse = webClient.post()
                .uri(CREATE_EMPLOYEE)
                .body(Mono.just(employee), Employee.class)
                .retrieve()
                .bodyToMono(EmployeeResponse.class)
                .block();

        LOGGER.trace("Returning createEmployee() method");
        return employeeResponse.getData();
    }

    @Override
    public String deleteEmployeeById(String id) {
        LOGGER.trace("deleteEmployeeById() method called with id : " + id);
        String uri = String.format(DELETE_EMPLOYEE, id);

        String employeeResponse = webClient.delete()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        LOGGER.trace("Returning deleteEmployeeById() method");
        return employeeResponse;
    }
}
