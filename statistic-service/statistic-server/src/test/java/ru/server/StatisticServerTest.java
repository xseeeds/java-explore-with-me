package ru.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class StatisticServerTest {

	@Test
	void main() {
		assertDoesNotThrow(StatisticServer::new);
		assertDoesNotThrow(() -> StatisticServer.main(new String[]{}));
	}
}
