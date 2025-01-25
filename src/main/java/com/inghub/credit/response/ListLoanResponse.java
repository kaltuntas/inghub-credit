package com.inghub.credit.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inghub.credit.response.dto.LoanDTO;

import java.util.List;


public record ListLoanResponse(List<LoanDTO> loans,
                               @JsonProperty("paging") ApiModelPage apiModelPage) {

}