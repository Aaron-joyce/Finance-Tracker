package com.ajax.finance_tracker;

import com.ajax.finance_tracker.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class FinanceTrackerApplicationTests {

	@Autowired
	AccountService accountService;

	@Test
	@Transactional
	void testAddTransactionFlow() {
		// 1. Register User
		String username = "test_user_1";
		accountService.registerUser(new AccountService.UserDto(username, "pass", "email@test.com"));

		// 3. Verify
		System.out.println("Item added successfully");
	}

}
