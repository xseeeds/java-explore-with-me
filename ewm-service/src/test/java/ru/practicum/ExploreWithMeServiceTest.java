package ru.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ExploreWithMeServiceTest {

    @Test
    void main() {
        assertDoesNotThrow(ExploreWithMeService::new);
        assertDoesNotThrow(() -> ExploreWithMeService.main(new String[]{}));
    }

}
