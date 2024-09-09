package com.example.banking_api.account;


import com.example.banking_api.customer.Customer;
import com.example.banking_api.customer.CustomerRepository;
import com.example.banking_api.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public AccountServiceImpl(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }


    public Account createAccount(AccountDto accountDto) {
        // Load the existing customer from the database
        Customer customer = customerRepository.findById(accountDto.getCustomer_id())
                .orElseThrow(() -> new IllegalArgumentException("Customer with ID " + accountDto.getCustomer_id() + " not found"));

        // Create a new account
        Account account = new Account();
        account.setBranch_code(accountDto.getBranch_code());
        account.setBalance(accountDto.getBalance());
        account.setAccount_type(AccountType.valueOf(accountDto.getAccount_type().toUpperCase()));
        account.setCustomer(customer);

        return accountRepository.save(account);
    }

    @Override
    public Double getBalance(Long accountId) {
        return accountRepository.findById(accountId)
                .map(Account::getBalance)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
    }
}
