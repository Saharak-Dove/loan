package com.digitalacademy.loan.controllers;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.digitalacademy.loan.controllers.LoanController;
import com.digitalacademy.loan.constants.LoanError;
import com.digitalacademy.loan.exceptions.LoanException;
import com.digitalacademy.loan.models.LoanInfo;
import com.digitalacademy.loan.services.LoanService;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class LoanControllerTest {

  @Mock
  LoanService loanServive;

  @InjectMocks
  LoanController loanController;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  MockMvc mvc;

  @DisplayName("Test get loan by id 1")
  @Test
  void testGetLoanInfoByCustomerIdEquals1() throws Exception {
    Long id = 1L;
    LoanInfo loanInfo = new LoanInfo();
    loanInfo.setId(id);
    loanInfo.setStatus("OK");
    loanInfo.setAccountPayable("102-444-6666");
    loanInfo.setAccountReceivable("102-333-6666");
    loanInfo.setPrincipalAmount(2000.00);

    when(loanServive.getloanInfoById(id)).thenReturn(loanInfo);

    MvcResult mvcResult = mvc.perform(get("/loan/info/{id}", id))
                             .andExpect(status().isOk())
                             .andReturn();
                             
    JSONObject resp = new JSONObject(mvcResult.getResponse().getContentAsString());
    JSONObject status = new JSONObject(resp.getString("status"));
    JSONObject data = new JSONObject(resp.getString("data"));

    assertEquals("0", status.get("code").toString());
    assertEquals("success", status.get("message").toString());

    assertEquals(1, data.get("id"));
    assertEquals("OK", data.get("status"));
    assertEquals("102-444-6666", data.get("account_payable"));
    assertEquals("102-333-6666", data.get("account_receivable"));
    assertEquals(2000, data.get("principal_amount"));

    verify(loanServive, times(1)).getloanInfoById(id);
  }

  @DisplayName("Test get loan by id 2 and throws error")
  @Test
  void testGetLoanInfoByCustomerIdEquals2() throws Exception {
    Long id = 2L;

    when(loanServive.getloanInfoById(id)).thenThrow(
      new LoanException(LoanError.GET_LOAN_NOT_FOUND, HttpStatus.BAD_REQUEST)
    );

    MvcResult mvcResult = mvc.perform(get("/loan/info/{id}", id))
                             .andExpect(status().isBadRequest())
                             .andReturn();

    JSONObject resp = new JSONObject(mvcResult.getResponse().getContentAsString());
    JSONObject status = new JSONObject(resp.getString("status"));

    assertEquals("LOAN4002", status.get("code").toString());
    assertEquals("Loan infomation not found", status.get("message").toString());
  }

  @DisplayName("Test get loan by id 3 and throws error")
  @Test
  void testGetLoanInfoByCustomerIdEquals3() throws Exception {
    Long id = 3L;

    when(loanServive.getloanInfoById(id)).thenThrow(
      new Exception("Error Exception")
    );

    mvc.perform(get("/loan/info/{id}", id))
       .andExpect(status().isInternalServerError())
       .andExpect(content().contentType("application/json"))
       .andExpect(jsonPath("$.status.code").value("LOAN4001"))
       .andExpect(jsonPath("$.status.message").value("Cannot get loan infomation"));

    // JSONObject resp = new JSONObject(mvcResult.getResponse().getContentAsString());
    // JSONObject status = new JSONObject(resp.getString("status"));

    // assertEquals("LOAN4001", status.get("code").toString());
    // assertEquals("Cannot get loan infomation", status.get("message").toString());
  }
}