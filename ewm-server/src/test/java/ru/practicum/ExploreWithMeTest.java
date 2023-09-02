package ru.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ExploreWithMeTest {

    @Test
    void main() {
        assertDoesNotThrow(ExploreWithMe::new);
        assertDoesNotThrow(() -> ExploreWithMe.main(new String[]{}));
    }

}
