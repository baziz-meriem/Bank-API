-- -- Insert initial data for Customer
INSERT INTO customer (name, street, city, state, postal_code, phone_number) VALUES
                                                                                ('John Doe', '123 Main St', 'Anytown', 'Anystate', '12345', '1546897523'),
                                                                                ('Jane Smith', '456 Elm St', 'Othertown', 'Otherstate', '67890', '5555678895');

-- -- Insert initial data for Account
INSERT INTO account (branch_code, balance, account_type, customer_id) VALUES
                                                                          ('D0156', 1000, 'CHECKING', 1),
                                                                          ('F6902', 2000, 'SAVINGS', 2);

-- Insert initial data for Transaction
INSERT INTO transaction (source_account_id, target_account_id, amount, type) VALUES
    (1, 2, 500, 'TRANSFER');


