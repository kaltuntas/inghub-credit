package com.inghub.credit.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inghub.credit.response.dto.LoanInstallmentDTO;

import java.util.List;


public record ListLoanInstallmentsResponse(long loanId,
                                           List<LoanInstallmentDTO> loanInstallments,
                                           @JsonProperty("paging") ApiModelPage apiModelPage) {

}