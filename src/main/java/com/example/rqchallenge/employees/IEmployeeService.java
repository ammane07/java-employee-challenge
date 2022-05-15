package com.example.rqchallenge.employees;

import com.example.rqchallenge.employees.model.DeleteResponse;
import com.example.rqchallenge.employees.model.Employee;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


public interface IEmployeeService {
    List<Employee> getAllEmployees();
    List<Employee> getEmployeesByNameSearch(String searchString);
    Employee getEmployeeById(String id);
    Integer getHighestSalaryOfEmployees();
    List<String> getTopTenHighestEarningEmployeeNames();
    Employee createEmployee(@RequestBody Map<String, Object> employeeInput);
    DeleteResponse deleteEmployeeById(String id);
}
