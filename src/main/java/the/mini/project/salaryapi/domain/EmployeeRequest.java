package the.mini.project.salaryapi.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
public class EmployeeRequest {
    private double min;
    private double max;
    private int offset;
    private int limit;
    private String sort;

    private EmployeeRequest(Optional<Double> min, Optional<Double> max,
                            Optional<Integer> offset, Optional<Integer> limit,
                            Optional<String> sort
    ) {
        this.min = min.orElse(0.0);
        this.max = max.orElse(4000.0);
        if (offset.isPresent() && offset.get() >= 0) {
            this.offset = offset.get();
        } else {
            this.offset = 0;
        }

        if (limit.isPresent() && limit.get() >= 1) {
            this.limit = limit.get();
        } else {
            this.limit = 0;
        }

        if (sort.isPresent() && (sort.get().equalsIgnoreCase("name")
                             || sort.get().equalsIgnoreCase("salary") )) {
            this.sort = sort.get();
        } else {
            this.sort = "";
        }
    }

    public static EmployeeRequest createPersonRequest(Optional<Double> min, Optional<Double> max,
                                                      Optional<Integer> offset, Optional<Integer> limit,
                                                      Optional<String> sort
    ) {
        return new EmployeeRequest(min, max, offset, limit, sort);
    }
}
