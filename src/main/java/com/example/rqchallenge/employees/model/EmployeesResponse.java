package com.example.rqchallenge.employees.model;

import java.util.Arrays;

public class EmployeesResponse {
    private String status;
    private Employee[] data;

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

    @Override
    public String toString() {
        return "Employees{" +
                "status='" + status + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
