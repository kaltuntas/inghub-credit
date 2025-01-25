package com.inghub.credit.service;

import com.inghub.credit.domain.LoanInstallment;
import com.inghub.credit.exception.ResourceNotFoundException;
import com.inghub.credit.repository.LoanInstallmentRepository;
import com.inghub.credit.response.ListLoanInstallmentsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoanInstallmentServiceTest {

    @InjectMocks
    private LoanInstallmentService loanInstallmentService;

    @Mock
    private LoanInstallmentRepository loanInstallmentRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById_Success() {
        LoanInstallment loanInstallment = createDummyLoanInstallment(1L);
        when(loanInstallmentRepository.findById(1L)).thenReturn(Optional.of(loanInstallment));

        LoanInstallment result = loanInstallmentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(loanInstallmentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(loanInstallmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> loanInstallmentService.findById(1L));
        verify(loanInstallmentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPaginatedLoanInstallmentsByLoanId() {
        List<LoanInstallment> installments = Arrays.asList(
                createDummyLoanInstallment(1L),
                createDummyLoanInstallment(2L)
        );
        Page<LoanInstallment> page = new PageImpl<>(installments);
        when(loanInstallmentRepository.findByLoanId(eq(1L), any(PageRequest.class))).thenReturn(page);

        Page<LoanInstallment> result = loanInstallmentService.getPaginatedLoanInstallmentsByLoanId(1L, PageRequest.of(0, 10));

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(loanInstallmentRepository, times(1)).findByLoanId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testFindLoanInstallmentsByLoanIdAndIsPaid() {
        List<LoanInstallment> installments = Arrays.asList(
                createDummyLoanInstallment(1L),
                createDummyLoanInstallment(2L)
        );
        when(loanInstallmentRepository.findByLoanIdAndPaidOrderByDueDate(1L, true)).thenReturn(installments);

        List<LoanInstallment> result = loanInstallmentService.findLoanInstallmentsByLoanIdAndIsPaid(1L, true);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(loanInstallmentRepository, times(1)).findByLoanIdAndPaidOrderByDueDate(1L, true);
    }

    @Test
    void testSearchLoanInstallmentsByLoanId() {
        List<LoanInstallment> installments = Arrays.asList(
                createDummyLoanInstallment(1L),
                createDummyLoanInstallment(2L)
        );
        Page<LoanInstallment> page = new PageImpl<>(installments);
        when(loanInstallmentRepository.findByLoanId(eq(1L), any(PageRequest.class))).thenReturn(page);

        ListLoanInstallmentsResponse response = loanInstallmentService.searchLoanInstallmentsByLoanId(1L, 10, 1, "dueDate");

        assertNotNull(response);
        assertEquals(2, response.loanInstallments().size());
        verify(loanInstallmentRepository, times(1)).findByLoanId(eq(1L), any(PageRequest.class));
    }

    @Test
    void testCreateInstallmentDatesByInstallmentCount() {
        List<LocalDate> dates = LoanInstallmentService.createInstallmentDatesByInstallmentCount(3);

        assertEquals(3, dates.size());
        assertTrue(dates.get(0).isAfter(LocalDate.now()));
    }

    @Test
    void testCalculateInstallmentAmount() {
        BigDecimal loanAmount = BigDecimal.valueOf(1000);
        int numberOfInstallments = 5;
        BigDecimal interestRate = BigDecimal.valueOf(0.1);

        BigDecimal result = LoanInstallmentService.calculateInstallmentAmount(loanAmount, numberOfInstallments, interestRate);

        assertNotNull(result);
        assertEquals(0, result.compareTo(BigDecimal.valueOf(220.00)));
    }

    @Test
    void testPayMultipleLoanInstallments() {
        LoanInstallment installment = createDummyLoanInstallment(1L);
        installment.setPaid(false);
        when(loanInstallmentRepository.findById(1L)).thenReturn(Optional.of(installment));

        loanInstallmentService.payMultipleLoanInstallments(Arrays.asList(1L));

        assertTrue(installment.isPaid());
        assertEquals(installment.getAmount(), installment.getPaidAmount());
        verify(loanInstallmentRepository, times(1)).save(installment);
    }

    private LoanInstallment createDummyLoanInstallment(Long id) {
        LoanInstallment installment = new LoanInstallment();
        installment.setId(id);
        installment.setAmount(BigDecimal.valueOf(100));
        installment.setPaidAmount(BigDecimal.ZERO);
        installment.setDueDate(LocalDate.now().plusDays(30));
        installment.setPaymentDate(null);
        installment.setPaid(false);
        return installment;
    }
}