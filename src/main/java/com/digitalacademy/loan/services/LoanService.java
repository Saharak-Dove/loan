package com.digitalacademy.loan.service;

import java.util.HashMap;
import java.util.Map;

import com.digitalacademy.loan.constants.LoanError;
import com.digitalacademy.loan.constants.Response;
import com.digitalacademy.loan.exception.LoanException;
import com.digitalacademy.loan.model.LoanInfo;
import com.digitalacademy.loan.model.StatusModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class LoanService {
  private static final Logger log = LogManager.getLogger(LoanService.class.getName());

  public LoanInfo getloanInfoById(Long id) throws Exception {
    log.info("GET loan info by id {}", id);

    LoanInfo loanInfo = new LoanInfo();
    if (id.equals(1L)) {
      loanInfo.setId(id);
      loanInfo.setStatus("OK");
      loanInfo.setAccountPayable("102-444-6666");
      loanInfo.setAccountReceivable("102-333-6666");
      loanInfo.setPrincipalAmount(2000.00);
    } else if (id.equals(2L)) {
      log.info("Test Error 400 id {}", id);
      throw new LoanException(LoanError.GET_LOAN_NOT_FOUND, HttpStatus.BAD_REQUEST);
    } else {
      log.info("Test Error 404 id {}", id);
      throw new Exception("Error Exception");
    }

    return loanInfo;
  }
}