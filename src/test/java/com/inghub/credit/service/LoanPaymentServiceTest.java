package com.inghub.credit.service;

import com.inghub.credit.domain.Customer;
import com.inghub.credit.domain.Loan;
import com.inghub.credit.domain.LoanInstallment;
import com.inghub.credit.exception.CreditException;
import com.inghub.credit.exception.ResourceNotFoundException;
import com.inghub.credit.response.PayLoanResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoanPaymentServiceTest {

    @InjectMocks
    private LoanPaymentService loanPaymentService;

    @Mock
    private LoanService loanService;

    @Mock
    private LoanInstallmentService loanInstallmentService;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPayLoan_AllInstallmentsPaid() {
        // Create dummy Customer
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.valueOf(5000));

        // Create dummy Loan
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setCustomer(customer);

        // Create dummy LoanInstallments
        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setLoan(loan);
        installment1.setAmount(BigDecimal.valueOf(500));
        installment1.setPaid(false);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setLoan(loan);
        installment2.setAmount(BigDecimal.valueOf(500));
        installment2.setPaid(false);

        List<LoanInstallment> unpaidInstallments = Arrays.asList(installment1, installment2);

        when(loanInstallmentService.findLoanInstallmentsByLoanIdAndIsPaid(1L, false)).thenReturn(unpaidInstallments);
        when(loanInstallmentService.findEligibleInstallments(unpaidInstallments, BigDecimal.valueOf(1000)))
                .thenReturn(unpaidInstallments);

        PayLoanResponse response = loanPaymentService.payLoan(1L, BigDecimal.valueOf(1000));

        assertNotNull(response);
        assertEquals(1L, response.loanId());
        assertEquals(2, response.paidInstallmentCount());
        assertEquals(1000.0, response.totalAmountSpent());
        assertTrue(response.loanPaidCompletely());

        verify(loanInstallmentService, times(1)).payMultipleLoanInstallments(Arrays.asList(1L, 2L));
        verify(loanService, times(1)).updateLoanIsPaidStatus(1L, true);
        verify(customerService, times(1)).decreaseCustomerUsedCreditLimit(1L, BigDecimal.valueOf(1000));
    }

    @Test
    void testPayLoan_PartialInstallmentsPaid() {
        // Create dummy Customer
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");
        customer.setSurname("Doe");
        customer.setCreditLimit(BigDecimal.valueOf(10000));
        customer.setUsedCreditLimit(BigDecimal.valueOf(5000));

        Long loanId = 1L;
        BigDecimal paidAmount = BigDecimal.valueOf(100);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setCustomer(customer);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setAmount(BigDecimal.valueOf(100));
        installment1.setLoan(loan);
        installment1.setPaid(false);

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setId(2L);
        installment2.setAmount(BigDecimal.valueOf(100));
        installment2.setLoan(loan);
        installment2.setPaid(false);

        List<LoanInstallment> unpaidInstallments = Arrays.asList(installment1, installment2);

        when(loanInstallmentService.findLoanInstallmentsByLoanIdAndIsPaid(loanId, false)).thenReturn(unpaidInstallments);
        when(loanInstallmentService.findEligibleInstallments(unpaidInstallments, paidAmount)).thenReturn(List.of(installment1));

        PayLoanResponse response = loanPaymentService.payLoan(loanId, paidAmount);

        assertNotNull(response);
        assertEquals(loanId, response.loanId());
        assertEquals(1, response.paidInstallmentCount());
        assertEquals(100.0, response.totalAmountSpent());
        assertFalse(response.loanPaidCompletely());

        verify(loanInstallmentService, times(1)).payMultipleLoanInstallments(List.of(1L));
        verify(loanService, never()).updateLoanIsPaidStatus(loanId, true);
        verify(customerService, times(1)).decreaseCustomerUsedCreditLimit(anyLong(), eq(BigDecimal.valueOf(100)));
    }

    @Test
    void testPayLoan_NoUnpaidInstallments() {
        Long loanId = 1L;
        BigDecimal paidAmount = BigDecimal.valueOf(100);

        when(loanInstallmentService.findLoanInstallmentsByLoanIdAndIsPaid(loanId, false)).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> loanPaymentService.payLoan(loanId, paidAmount));

        verify(loanInstallmentService, never()).findEligibleInstallments(anyList(), any());
        verify(loanService, never()).updateLoanIsPaidStatus(anyLong(), anyBoolean());
        verify(customerService, never()).decreaseCustomerUsedCreditLimit(anyLong(), any());
    }

    @Test
    void testPayLoan_InvalidPaymentAmount() {
        Long loanId = 1L;
        BigDecimal paidAmount = BigDecimal.valueOf(50);

        Loan loan = new Loan();
        loan.setId(loanId);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setId(1L);
        installment1.setAmount(BigDecimal.valueOf(100));
        installment1.setLoan(loan);
        installment1.setPaid(false);

        List<LoanInstallment> unpaidInstallments = List.of(installment1);

        when(loanInstallmentService.findLoanInstallmentsByLoanIdAndIsPaid(loanId, false)).thenReturn(unpaidInstallments);

        assertThrows(CreditException.class, () -> loanPaymentService.payLoan(loanId, paidAmount));

        verify(loanInstallmentService, never()).findEligibleInstallments(anyList(), any());
        verify(loanService, never()).updateLoanIsPaidStatus(anyLong(), anyBoolean());
        verify(customerService, never()).decreaseCustomerUsedCreditLimit(anyLong(), any());
    }
}
