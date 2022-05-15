package com.example.rqchallenge.impl;

import com.example.rqchallenge.TestUtils;
import com.example.rqchallenge.employees.IEmployeeService;
import com.example.rqchallenge.employees.impl.EmployeeControllerImpl;
import com.example.rqchallenge.employees.model.DeleteResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Before
    public void setup() {
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
    public void getEmployeesByNameSearchTest() {
        String expectedEmployeeName = "name_5";
        List<Employee> filteredEmployeeList = Arrays.asList(expectedEmployeeList[4]);
        when(employeeService.getEmployeesByNameSearch(expectedEmployeeName)).thenReturn(filteredEmployeeList);

        ResponseEntity<List<Employee>> employeeResp = employeeControllerImpl.getEmployeesByNameSearch(expectedEmployeeName);
        List<Employee> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.FOUND, employeeResp.getStatusCode());
        Assert.assertEquals(1, actualEmployeeList.size());
        Assert.assertEquals(expectedEmployeeName, actualEmployeeList.get(0).getEmployeeName());
    }

    @Test
    public void getEmployeesByNameSearchShouldReturnNotFoundStatus() {
        when(employeeService.getEmployeesByNameSearch(anyString())).thenReturn(Arrays.asList(new Employee[]{}));

        ResponseEntity<List<Employee>> employeeResp = employeeControllerImpl.getEmployeesByNameSearch("employee_1");
        List<Employee> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.NOT_FOUND, employeeResp.getStatusCode());
        Assert.assertEquals(0, actualEmployeeList.size());
    }

    @Test
    public void getEmployeesByIdTest() {
        String expectedEmployeeId = "5";
        when(employeeService.getEmployeeById(expectedEmployeeId)).thenReturn(expectedEmployeeList[4]);

        ResponseEntity<Employee> employeeResp = employeeControllerImpl.getEmployeeById(expectedEmployeeId);
        Employee actualEmployee = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.FOUND, employeeResp.getStatusCode());
        Assert.assertEquals(expectedEmployeeId, actualEmployee.getId().toString());
    }

    @Test
    public void getEmployeesByIdShouldReturnNotFoundStatus() {
        String expectedEmployeeId = "5";
        when(employeeService.getEmployeeById(expectedEmployeeId)).thenReturn(null);

        ResponseEntity<Employee> employeeResp = employeeControllerImpl.getEmployeeById(expectedEmployeeId);
        Employee actualEmployee = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.NOT_FOUND, employeeResp.getStatusCode());
        Assert.assertEquals(null, actualEmployee);
    }

    @Test
    public void getHighestSalaryOfEmployeesTest() {
        Integer expectedSalary = 100000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(expectedSalary);
        ResponseEntity<Integer> actualSalaryResponse = employeeControllerImpl.getHighestSalaryOfEmployees();

        Assert.assertEquals(expectedSalary, actualSalaryResponse.getBody());
        Assert.assertEquals(HttpStatus.OK, actualSalaryResponse.getStatusCode());
    }

    @Test
    public void getHighestSalaryOfEmployeesShouldReturnNotFoundStatus() {
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(null);
        ResponseEntity<Integer> actualSalaryResponse = employeeControllerImpl.getHighestSalaryOfEmployees();

        Assert.assertEquals(null, actualSalaryResponse.getBody());
        Assert.assertEquals(HttpStatus.NO_CONTENT, actualSalaryResponse.getStatusCode());
    }

    @Test
    public void getTopTenHighestEarningEmployeeNamesTest() {
        String[] expectedHighestSalaryEmployees = new String[]{expectedEmployeeList[2].getEmployeeName(), expectedEmployeeList[3].getEmployeeName()};
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(Arrays.asList(expectedHighestSalaryEmployees));

        ResponseEntity<List<String>> employeeResp = employeeControllerImpl.getTopTenHighestEarningEmployeeNames();
        List<String> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.OK, employeeResp.getStatusCode());
        Assert.assertArrayEquals(expectedHighestSalaryEmployees, actualEmployeeList.toArray());
    }

    @Test
    public void getTopTenHighestEarningEmployeeShouldReturnNotFoundStatus() {
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(null);

        ResponseEntity<List<String>> employeeResp = employeeControllerImpl.getTopTenHighestEarningEmployeeNames();
        List<String> actualEmployeeList = employeeResp.getBody();

        Assert.assertEquals(HttpStatus.NO_CONTENT, employeeResp.getStatusCode());
        Assert.assertEquals(null, actualEmployeeList);
    }

    @Test
    public void createEmployeeTest() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", expectedEmployeeResponse.getData().getEmployeeName());
        employeeInput.put("salary", expectedEmployeeResponse.getData().getEmployeeSalary());
        employeeInput.put("age", expectedEmployeeResponse.getData().getEmployeeAge());

        when(employeeService.createEmployee(employeeInput)).thenReturn(expectedEmployeeResponse.getData());

        ResponseEntity<Employee> employeeResponse = employeeControllerImpl.createEmployee(employeeInput);

        Employee actualEmployee = employeeResponse.getBody();

        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeName(), actualEmployee.getEmployeeName());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeSalary(), actualEmployee.getEmployeeSalary());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeAge(), actualEmployee.getEmployeeAge());
    }

    @Test
    public void createEmployeeTestShouldReturnBadRequestStatus() {
        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", expectedEmployeeResponse.getData().getEmployeeName());
        employeeInput.put("salary", expectedEmployeeResponse.getData().getEmployeeSalary());

        when(employeeService.createEmployee(employeeInput)).thenReturn(expectedEmployeeResponse.getData());

        ResponseEntity<Employee> employeeResponse = employeeControllerImpl.createEmployee(employeeInput);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, employeeResponse.getStatusCode());
        Assert.assertEquals("Please enter name, age and salary fields for employee", employeeResponse.getBody());
    }

    @Test
    public void deleteEmployeeByIdTest() {
        DeleteResponse expectedDeleteResponse = new DeleteResponse();
        expectedDeleteResponse.setMessage("Employee deleted successfully");
        expectedDeleteResponse.setStatus("success");

        when(employeeService.deleteEmployeeById("2")).thenReturn(expectedDeleteResponse);
        ResponseEntity<DeleteResponse> deleteResponse = employeeControllerImpl.deleteEmployeeById("2");

        Assert.assertEquals(expectedDeleteResponse.getMessage(), deleteResponse.getBody().getMessage());
        Assert.assertEquals(expectedDeleteResponse.getStatus(), deleteResponse.getBody().getStatus());
    }
}
