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
            employee.setEmployee_name("name_" + employee.getId());
            employee.setEmployee_salary(i*1000);

            employeeList.add(employee);
        }
        return employeeList.toArray(Employee[]::new);
    }
}
