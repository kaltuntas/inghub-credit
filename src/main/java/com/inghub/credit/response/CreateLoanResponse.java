package com.inghub.credit.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CreateLoanResponse(Long id,
                                 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime insertDate,
                                 long customerId,
                                 double loanAmount,
                                 int numberOfInstallment) {

}