package com.example.banking_api.unit.transaction;

import com.example.banking_api.account.Account;
import com.example.banking_api.account.AccountRepository;
import com.example.banking_api.account.AccountType;
import com.example.banking_api.transaction.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private Account sourceAccount;
    private Account targetAccount;
    @BeforeEach
    public void setUp() {
        sourceAccount = new Account();
        sourceAccount.setAccount_id(1L);
        sourceAccount.setBranch_code("00101");
        sourceAccount.setBalance(1000.00);
        sourceAccount.setAccount_type(AccountType.SAVINGS);

        targetAccount = new Account();
        targetAccount.setAccount_id(2L);
        targetAccount.setBranch_code("00201");
        targetAccount.setBalance(2000.00);
        targetAccount.setAccount_type(AccountType.CHECKING);

        // Mock repository behavior
        when(accountRepository.findById(sourceAccount.getAccount_id())).thenReturn(java.util.Optional.of(sourceAccount));
        when(accountRepository.findById(targetAccount.getAccount_id())).thenReturn(java.util.Optional.of(targetAccount));

    }
    @Test
    public void testCreateTransaction() throws Exception {
        // Create and initialize mock data
        TransactionRequestDTO transactionRequest = new TransactionRequestDTO();
        transactionRequest.setSource_account_id(sourceAccount.getAccount_id());
        transactionRequest.setTarget_account_id(targetAccount.getAccount_id());
        transactionRequest.setAmount(100.0);

        Transaction createdTransaction = new Transaction();
        createdTransaction.setSourceAccount(sourceAccount);
        createdTransaction.setTargetAccount(targetAccount);
        createdTransaction.setAmount(10000.0);

        // Mock the service call
        given(transactionService.createTransaction(
                transactionRequest.getSource_account_id(),
                transactionRequest.getTarget_account_id(),
                transactionRequest.getAmount()
        )).willReturn(createdTransaction);

        // Perform the POST request and validate the response
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transactionRequest)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", is("Transaction created successfully")));
    }

    @Test
    public void testGetTransferHistory() throws Exception {
        List<Transaction> transactions = Arrays.asList(
                new Transaction(this.sourceAccount, this.targetAccount, 2000.0, TransactionType.TRANSFER),
                new Transaction(this.sourceAccount, this.targetAccount, 300.0, TransactionType.TRANSFER)
        );

        // Mock the service call
        given(transactionService.getTransferHistory(this.targetAccount.getAccount_id())).willReturn(transactions);

        // Perform the GET request and validate the response
        String url = String.format("/api/v1/transactions/history/%d", this.targetAccount.getAccount_id());

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Transaction history fetched successfully"));

    }
}
