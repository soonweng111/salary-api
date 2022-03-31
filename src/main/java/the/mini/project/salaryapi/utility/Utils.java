package the.mini.project.salaryapi.utility;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Utils {
    private Utils(){}

    private static final Gson gson = new Gson();

    public static <T> String  getJson(T data) {
        return gson.toJson(data);
    }


}
