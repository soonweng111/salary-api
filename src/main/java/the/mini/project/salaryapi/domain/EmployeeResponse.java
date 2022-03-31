package the.mini.project.salaryapi.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class EmployeeResponse {

    private final List<Employee> results;

    private EmployeeResponse(List<Employee> results) {
        this.results = results;
    }

    public static EmployeeResponse createPersonResponse(List<Employee> results){
        return new EmployeeResponse(results);
    }

}
