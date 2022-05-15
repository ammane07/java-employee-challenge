package com.example.rqchallenge.employees.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeesResponse {
    private String status;
    private Employee[] data;
    private String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Employee[] getData() {
        return data;
    }

    public void setData(Employee[] data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "EmployeesResponse{" +
                "status='" + status + '\'' +
                ", data=" + Arrays.toString(data) +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeesResponse that = (EmployeesResponse) o;
        return status.equals(that.status) &&
                Arrays.equals(data, that.data) &&
                message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(status, message);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
