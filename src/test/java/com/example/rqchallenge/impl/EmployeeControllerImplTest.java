package com.example.rqchallenge.impl;

import com.example.rqchallenge.TestUtils;
import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.employees.impl.EmployeeControllerImpl;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class EmployeeControllerImplTest {
    @Mock
    private IEmployeeService employeeService;

    private EmployeesResponse expectedEmployeesResponse;
    private EmployeeResponse expectedEmployeeResponse;

    private Employee[] expectedEmployeeList;

    @InjectMocks
    private EmployeeControllerImpl employeeControllerImpl;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        expectedEmployeeList = TestUtils.createEmployeesForTest(5);
        expectedEmployeesResponse = new EmployeesResponse();
        expectedEmployeesResponse.setStatus("success");
        expectedEmployeesResponse.setData(expectedEmployeeList);

        expectedEmployeeResponse = new EmployeeResponse();
        expectedEmployeeResponse.setData(expectedEmployeeList[0]);
        expectedEmployeeResponse.setStatus("success");
    }

    @Test
    public void getAllEmployeesTest() throws IOException {
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(expectedEmployeeList));

        ResponseEntity<List<Employee>> employeeResp = employeeControllerImpl.getAllEmployees();
        List<Employee> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.OK, employeeResp.getStatusCode());
        Assert.assertArrayEquals(expectedEmployeeList, actualEmployeeList.toArray());
    }

    @Test
    public void getAllEmployeesShouldReturnNoContentStatus() throws IOException {
        when(employeeService.getAllEmployees()).thenReturn(Arrays.asList(new Employee[]{}));

        ResponseEntity<List<Employee>> employeeResp = employeeControllerImpl.getAllEmployees();
        List<Employee> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.NO_CONTENT, employeeResp.getStatusCode());
        Assert.assertEquals(0, actualEmployeeList.size());
    }

    @Test
    public void getEmployeesByNameSearchTest(){
        String expectedEmployeeName = "name_5";
        List<Employee> filteredEmployeeList = Arrays.asList(expectedEmployeeList[4]);
        when(employeeService.getEmployeesByNameSearch(expectedEmployeeName)).thenReturn(filteredEmployeeList);

        ResponseEntity<List<Employee>> employeeResp = employeeControllerImpl.getEmployeesByNameSearch(expectedEmployeeName);
        List<Employee> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.OK, employeeResp.getStatusCode());
        Assert.assertEquals(1, actualEmployeeList.size());
        Assert.assertEquals(expectedEmployeeName, actualEmployeeList.get(0).getEmployee_name());
    }

    @Test
    public void getEmployeesByNameSearchShouldReturnNotFoundStatus(){
        when(employeeService.getEmployeesByNameSearch(anyString())).thenReturn(Arrays.asList(new Employee[]{}));

        ResponseEntity<List<Employee>> employeeResp = employeeControllerImpl.getEmployeesByNameSearch("employee_1");
        List<Employee> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.NOT_FOUND, employeeResp.getStatusCode());
        Assert.assertEquals(0, actualEmployeeList.size());
    }
}
