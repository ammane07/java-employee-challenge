package com.example.rqchallenge.impl;

import com.example.rqchallenge.TestUtils;
import com.example.rqchallenge.employees.impl.EmployeeServiceImpl;
import com.example.rqchallenge.employees.model.DeleteResponse;
import com.example.rqchallenge.employees.model.Employee;
import com.example.rqchallenge.employees.model.EmployeeResponse;
import com.example.rqchallenge.employees.model.EmployeesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;

import static com.example.rqchallenge.employees.Constants.SUCCESS_STATUS;
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private EmployeeServiceImpl employeeServiceImpl;
    private EmployeesResponse expectedEmployeesResponse;
    private EmployeeResponse expectedEmployeeResponse;
    private EmployeesResponse employeesResponseWithError;
    private EmployeeResponse employeeResponseWithError;

    @Before
    public void setup(){
        MockitoAnnotations.openMocks(this);
        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.build()).thenReturn(webClient);
        when(builder.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)).thenReturn(builder);
        when(webClientBuilder.baseUrl(anyString())).thenReturn(builder);
        employeeServiceImpl = new EmployeeServiceImpl(webClientBuilder, "http://test.com");


        Employee[] expectedEmployeeList = TestUtils.createEmployeesForTest(15);
        expectedEmployeesResponse = new EmployeesResponse();
        expectedEmployeesResponse.setStatus(SUCCESS_STATUS);
        expectedEmployeesResponse.setData(expectedEmployeeList);

        expectedEmployeeResponse = new EmployeeResponse();
        expectedEmployeeResponse.setData(expectedEmployeeList[0]);
        expectedEmployeeResponse.setStatus(SUCCESS_STATUS);

        employeeResponseWithError = new EmployeeResponse();
        employeeResponseWithError.setStatus("error");

        employeesResponseWithError = new EmployeesResponse();
        employeesResponseWithError.setStatus("error");
        employeesResponseWithError.setMessage("Internal error");
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
    public void getAllEmployeesTestShouldThrowInternalServerError() {
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));
        expectedException.expect(Matchers.hasProperty("reason", CoreMatchers.is("Internal error")));

        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(employeesResponseWithError));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        employeeServiceImpl.getAllEmployees();
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
        Assert.assertEquals(expectedEmployeeName, employeeList.get(0).getEmployeeName());
    }

    @Test
    public void getEmployeesByNameSearchShouldThrowInternalServerError() {
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));
        expectedException.expect(Matchers.hasProperty("reason", CoreMatchers.is("Internal error")));

        String expectedEmployeeName = "xyz";
        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(employeesResponseWithError));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        employeeServiceImpl.getEmployeesByNameSearch(expectedEmployeeName);
    }

    @Test
    public void getEmployeeByIdTest(){
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(expectedEmployeeResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        Employee actualEmployee = employeeServiceImpl.getEmployeeById("0");

        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeName(), actualEmployee.getEmployeeName());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeSalary(), actualEmployee.getEmployeeSalary());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeAge(), actualEmployee.getEmployeeAge());
    }

    @Test
    public void getEmployeeByIdShouldThrowInternalServerError() {
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));
        //expectedException.expect(Matchers.hasProperty("reason", CoreMatchers.is("Internal error")));

        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(employeeResponseWithError));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        employeeServiceImpl.getEmployeeById("0");
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
    public void getHighestSalaryOfEmployeesShouldThrowInternalServerError() {
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));
        expectedException.expect(Matchers.hasProperty("reason", CoreMatchers.is("Internal error")));

        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(employeesResponseWithError));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        employeeServiceImpl.getHighestSalaryOfEmployees();
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
    public void getTopTenHighestEarningEmployeeNamesShouldThrowInternalServerError() {
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));
        expectedException.expect(Matchers.hasProperty("reason", CoreMatchers.is("Internal error")));

        when(responseSpec.bodyToMono(EmployeesResponse.class)).thenReturn(Mono.just(employeesResponseWithError));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.get()).thenReturn(requestHeadersUriSpec);

        employeeServiceImpl.getTopTenHighestEarningEmployeeNames();
    }

    @Test
    public void createEmployeeTest(){
        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(expectedEmployeeResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", expectedEmployeeResponse.getData().getEmployeeName());
        employeeInput.put("salary", expectedEmployeeResponse.getData().getEmployeeSalary());
        employeeInput.put("age", expectedEmployeeResponse.getData().getEmployeeAge());

        Employee actualEmployee = employeeServiceImpl.createEmployee(employeeInput);
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeName(), actualEmployee.getEmployeeName());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeSalary(), actualEmployee.getEmployeeSalary());
        Assert.assertEquals(expectedEmployeeResponse.getData().getEmployeeAge(), actualEmployee.getEmployeeAge());
    }

    @Test
    public void createEmployeeShouldThrowInternalServerError(){
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));

        when(responseSpec.bodyToMono(EmployeeResponse.class)).thenReturn(Mono.just(employeeResponseWithError));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(webClient.post()).thenReturn(requestBodyUriSpec);

        Map<String, Object> employeeInput = new HashMap<>();
        employeeInput.put("name", expectedEmployeeResponse.getData().getEmployeeName());
        employeeInput.put("salary", expectedEmployeeResponse.getData().getEmployeeSalary());
        employeeInput.put("age", expectedEmployeeResponse.getData().getEmployeeAge());

        employeeServiceImpl.createEmployee(employeeInput);
    }

    @Test
    public void deleteEmployeeTest(){
        DeleteResponse expectedResponse = new DeleteResponse();
        expectedResponse.setMessage("Employee deleted successfully");
        expectedResponse.setStatus(SUCCESS_STATUS);

        when(responseSpec.bodyToMono(DeleteResponse.class)).thenReturn(Mono.just(expectedResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);

        DeleteResponse actualResponse = employeeServiceImpl.deleteEmployeeById("2");
        Assert.assertEquals(expectedResponse.getMessage(), actualResponse.getMessage());
        Assert.assertEquals(expectedResponse.getStatus(), actualResponse.getStatus());
    }

    @Test
    public void deleteEmployeeShouldThrowInternalServerError(){
        expectedException.expect(ResponseStatusException.class);
        expectedException.expect(Matchers.hasProperty("status", CoreMatchers.is(HttpStatus.INTERNAL_SERVER_ERROR)));

        DeleteResponse expectedResponse = new DeleteResponse();
        expectedResponse.setMessage("Employee deleted successfully");
        expectedResponse.setStatus("internal error");

        when(responseSpec.bodyToMono(DeleteResponse.class)).thenReturn(Mono.just(expectedResponse));
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);

        employeeServiceImpl.deleteEmployeeById("2");
    }
}
