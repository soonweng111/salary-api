package the.mini.project.salaryapi.controller;

import org.assertj.core.util.Lists;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.CoreMatchers.is;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import the.mini.project.salaryapi.domain.Employee;
import the.mini.project.salaryapi.domain.EmployeeException;
import the.mini.project.salaryapi.domain.EmployeeRequest;
import the.mini.project.salaryapi.domain.EmployeeResponse;
import the.mini.project.salaryapi.service.serviceImpl.EmployeeServiceImpl;
import the.mini.project.salaryapi.utility.Utils;

import java.io.FileInputStream;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@FixMethodOrder
@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    EmployeeController controller;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeServiceImpl service;

    @Test
    public void GetPersonController_SuccessTest() throws Exception {
        Employee p1 = new Employee("testUser", 2000.00);
        Employee p2 = new Employee("testUser2", 1500.00);
        Employee p3 = new Employee("testUser3", 4000.00);

        List<Employee> listOfEmployees = Lists.list(p1,p2,p3);
        EmployeeResponse response = EmployeeResponse.createPersonResponse(listOfEmployees);
        String listOfPersonJson = Utils.getJson(response);

        when(service.getPersons(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.content().string(listOfPersonJson));
    }

    @Test
    public void GetPersonController_NoRecordsFound_FailTest() throws Exception {
        EmployeeException ee = new EmployeeException("No Records Found");

        when(service.getPersons(any(EmployeeRequest.class))).thenThrow(ee);

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.error", is("No Records Found")));

    }

    @Test
    public void GetPersonController_EmployeeException_FailTest() throws Exception {

        when(service.getPersons(any(EmployeeRequest.class))).thenThrow(new EmployeeException("Error Test"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }

    @Test
    public void GetPersonController_Fail_InternalErrorException_FailTest() throws Exception {

        when(service.getPersons(any(EmployeeRequest.class))).thenThrow(new NullPointerException());

        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }

    @Test
    public void UploadBatchController_SuccessTest() throws Exception{
        MockMultipartFile csv = new MockMultipartFile("salaryCsv.csv", "salaryCsv.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream("src/test/resources/data/salaryCsv.csv"));
        when(service.batchUpload(any())).thenReturn(1);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file("document", csv.getBytes()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void UploadBatchController_BadData_FailTest() throws Exception {
        MockMultipartFile csv = new MockMultipartFile("salaryCsv.csv", "salaryCsv.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream("src/test/resources/data/brokenSalaryCsv.csv"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file("document", csv.getBytes()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success", is(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void UploadBatchController_EmptyFile_FailTest() throws Exception {
        MockMultipartFile csv = new MockMultipartFile("salaryCsv.csv", "salaryCsv.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE, new FileInputStream("src/test/resources/data/emptyCsv.csv"));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file("document", csv.getBytes()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success", is(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void UploadBatchController_BadRequest_FailTest() throws Exception {
        MockMultipartFile csv = mock(MockMultipartFile.class);

        when(service.batchUpload(any())).thenThrow(new NumberFormatException());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/upload").file("document", csv.getBytes()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success", is(0)))
                .andExpect(status().isBadRequest());
    }
}
