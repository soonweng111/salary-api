package the.mini.project.salaryapi.domain;

import lombok.Getter;

@Getter
public class EmployeeException extends Exception {
    private final String error;

    public EmployeeException(String errorMessage){
        this.error = errorMessage;
    }

}
