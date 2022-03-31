package the.mini.project.salaryapi.domain;

import lombok.Getter;

@Getter
public class BatchCreateResponse {
    private final int success;

    private BatchCreateResponse(int success) {
        this.success = success;
    }

    public static BatchCreateResponse createResponse(int success) {
        return new BatchCreateResponse(success);
    }
}
