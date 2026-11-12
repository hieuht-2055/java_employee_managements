package com.example.employeemanagement.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

// Unit test thuần: không cần Spring context, tự new như class Java bình thường (lợi ích của constructor injection).
class UtilityServiceTest {

    private final UtilityService utilityService = new UtilityService("EMP");

    @Test
    void generateEmployeeCode_padsSequenceToFourDigits() {
        assertThat(utilityService.generateEmployeeCode(7)).isEqualTo("EMP0007");
        assertThat(utilityService.generateEmployeeCode(12345)).isEqualTo("EMP12345");
    }

    @Test
    void generateEmployeeCode_rejectsNonPositiveSequence() {
        assertThatThrownBy(() -> utilityService.generateEmployeeCode(0))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void formatName_trimsCollapsesSpacesAndCapitalizes() {
        assertThat(utilityService.formatName("  nguyễn   văn AN ")).isEqualTo("Nguyễn Văn An");
    }

    @Test
    void normalizeEmail_trimsAndLowercases() {
        assertThat(utilityService.normalizeEmail(" An@Example.COM ")).isEqualTo("an@example.com");
    }
}
