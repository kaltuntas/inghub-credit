package com.inghub.credit.response;

public record PayLoanResponse(Long loanId,
                              int paidInstallmentCount,
                              double totalAmountSpent,
                              boolean loanPaidCompletely) {

}
