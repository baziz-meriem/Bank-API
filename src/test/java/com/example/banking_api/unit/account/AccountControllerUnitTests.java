package com.example.banking_api.unit.account;

import com.example.banking_api.account.Account;
import com.example.banking_api.account.AccountController;
import com.example.banking_api.account.AccountDto;
import com.example.banking_api.account.AccountService;
import com.example.banking_api.account.AccountType;
import com.example.banking_api.customer.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerUnitTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateAccount() throws Exception {
        // Create and initialize mock data
        AccountDto accountDto = new AccountDto();
        accountDto.setBranch_code("123ABC");
        accountDto.setBalance(1000.0);
        accountDto.setAccount_type("SAVINGS");
        accountDto.setCustomer_id(1L);

        Account createdAccount = new Account();
        createdAccount.setAccount_id(1L);
        createdAccount.setBranch_code("123ABC");
        createdAccount.setBalance(1000.0);
        createdAccount.setAccount_type(AccountType.SAVINGS);
        createdAccount.setCustomer(new Customer());

        // Mock the service call
        given(accountService.createAccount(accountDto)).willReturn(createdAccount);

        // Perform the POST request and validate the response
        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

    }

    @Test
    public void testGetBalance() throws Exception {

        Long accountId = 1L;
        Double expectedBalance = 1000.0;


        given(accountService.getBalance(accountId)).willReturn(expectedBalance);


        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/{accountId}/balance", accountId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(expectedBalance.toString()));
    }
}