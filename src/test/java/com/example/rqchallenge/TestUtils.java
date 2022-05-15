package com.example.rqchallenge;

import com.example.rqchallenge.employees.model.Employee;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static Employee[] createEmployeesForTest(int size) {
        List<Employee> employeeList = new ArrayList<>();

        for (int i = 0 ; i < size; i++){
            Employee employee = new Employee();
            employee.setId(i + 1);
            employee.setEmployeeName("name_" + employee.getId());
            employee.setEmployeeSalary(i*1000);
            employee.setEmployeeAge(i*10);

            employeeList.add(employee);
        }
        return employeeList.toArray(Employee[]::new);
    }
}
