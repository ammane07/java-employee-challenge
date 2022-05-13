package com.example.rqchallenge.impl;

import com.example.rqchallenge.TestUtils;
import com.example.rqchallenge.employees.impl.EmployeeServiceImpl;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmployeeServiceImplTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private EmployeeServiceImpl employeeServiceImpl;
    private EmployeesResponse expectedEmployeesResponse;
    private EmployeeResponse expectedEmployeeResponse;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.build()).thenReturn(webClient);
        when(builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(builder);
        employeeServiceImpl = new EmployeeServiceImpl(webClientBuilder, "http://test.com");


        Employee[] expectedEmployeeList = TestUtils.createEmployeesForTest(15);
        expectedEmployeesResponse = new EmployeesResponse();
        expectedEmployeesResponse.setStatus("success");
        expectedEmployeesResponse.setData(expectedEmployeeList);

        expectedEmployeeResponse = new EmployeeResponse();
        expectedEmployeeResponse.setData(expectedEmployeeList[0]);
        expectedEmployeeResponse.setStatus("success");
    }

    @Test
    public void getAllEmployeesTest() {
        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(expectedEmployeesResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        List<Employee> actualEmployeeList = employeeServiceImpl.getAllEmployees();
        Assert.assertArrayEquals(expectedEmployeesResponse.getData(), actualEmployeeList.toArray());
    }

    @Test
    public void getEmployeesByNameSearchTest() {
        String expectedEmployeeName = "name_5";
        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(expectedEmployeesResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        List<Employee> employeeList = employeeServiceImpl.getEmployeesByNameSearch(expectedEmployeeName);
        Assert.assertEquals(1, employeeList.size());
        Assert.assertEquals(expectedEmployeeName, employeeList.get(0).getEmployee_name());
    }

    @Test
    public void getHighestSalaryOfEmployeesTest() {
        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(expectedEmployeesResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        Integer highestSalaryOfEmployees = employeeServiceImpl.getHighestSalaryOfEmployees();
        Assert.assertEquals((Integer) 14000, highestSalaryOfEmployees);
    }

    @Test
    public void getTopTenHighestEarningEmployeeNamesTest() {

        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(expectedEmployeesResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        String[] expectedEmployeeNames = {"name_15", "name_14", "name_13", "name_12", "name_11", "name_10", "name_9", "name_8", "name_7", "name_6"};
        List<String> actualEmployeeNames = employeeServiceImpl.getTopTenHighestEarningEmployeeNames();

        String[] temp = actualEmployeeNames.toArray(String[]::new);

        Assert.assertArrayEquals(expectedEmployeeNames, temp);
    }

    @Test
    public void createEmployeeTest(){
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(expectedEmployeeResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", expectedEmployeeResponse.getData().getEmployee_name());
        employeeInput.put("salary", expectedEmployeeResponse.getData().getEmployee_salary());

        Employee actualEmployee = employeeServiceImpl.createEmployee(employeeInput);
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployee_name(), actualEmployee.getEmployee_name());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployee_salary(), actualEmployee.getEmployee_salary());
    }

    @Test
    public void deleteEmployeeTest(){
        String expectedResponse = "Employee deleted successfully";
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(expectedResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);

        String actualResponse = employeeServiceImpl.deleteEmployeeById("2");
        Assert.assertEquals(expectedResponse, actualResponse);
    }

}
