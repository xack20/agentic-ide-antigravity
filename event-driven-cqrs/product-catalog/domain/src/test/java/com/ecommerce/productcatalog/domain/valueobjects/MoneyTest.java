package com.ecommerce.productcatalog.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Money value object.
 */
class MoneyTest {

    @Test
    void usd_shouldCreateMoneyWithUSDCurrency() {
        Money money = Money.usd(100.0);

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
        assertThat(money.getCurrencyCode()).isEqualTo("USD");
    }

    @Test
    void add_shouldSumAmounts() {
        Money m1 = Money.usd(100.0);
        Money m2 = Money.usd(50.0);

        Money result = m1.add(m2);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(150.0));
    }

    @Test
    void add_withDifferentCurrencies_shouldThrowException() {
        Money usd = Money.usd(100.0);
        Money eur = Money.of(BigDecimal.valueOf(50.0), "EUR");

        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("different currencies");
    }

    @Test
    void subtract_shouldSubtractAmounts() {
        Money m1 = Money.usd(100.0);
        Money m2 = Money.usd(30.0);

        Money result = m1.subtract(m2);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(70.0));
    }

    @Test
    void subtract_resultingInNegative_shouldThrowException() {
        Money m1 = Money.usd(30.0);
        Money m2 = Money.usd(100.0);

        assertThatThrownBy(() -> m1.subtract(m2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    void multiply_shouldMultiplyByQuantity() {
        Money money = Money.usd(25.0);

        Money result = money.multiply(4);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.0));
    }

    @ParameterizedTest
    @ValueSource(doubles = { -1.0, -0.01 })
    void create_withNegativeAmount_shouldThrowException(double amount) {
        assertThatThrownBy(() -> Money.usd(amount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }

    @Test
    void equals_shouldReturnTrueForSameAmountAndCurrency() {
        Money m1 = Money.usd(100.0);
        Money m2 = Money.usd(100.0);

        assertThat(m1).isEqualTo(m2);
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }
}
