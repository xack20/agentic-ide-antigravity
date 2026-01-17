package com.usermanagement.domain.valueobject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.usermanagement.domain.exception.InvalidPhoneException;

import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Value object representing a validated and E.164 normalized phone number.
 */
public final class Phone {
    private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();

    // Bangladesh operator prefixes (configurable in real application)
    private static final Set<String> BD_VALID_PREFIXES = Set.of(
            "13", "14", "15", "16", "17", "18", "19");
    private static final Pattern BD_PREFIX_PATTERN = Pattern.compile("^\\+880(1[3-9])\\d{8}$");

    private final String value; // E.164 format
    private final String countryCode;

    private Phone(String value, String countryCode) {
        this.value = value;
        this.countryCode = countryCode;
    }

    /**
     * Creates a Phone value object with validation and E.164 normalization.
     *
     * @param phone          the raw phone input
     * @param defaultCountry default country code (e.g., "BD" for Bangladesh)
     * @return validated and normalized Phone
     * @throws InvalidPhoneException if phone is invalid
     */
    public static Phone of(String phone, String defaultCountry) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Phone number cannot be empty");
        }

        String cleaned = phone.replaceAll("[\\s\\-()]", "");

        try {
            Phonenumber.PhoneNumber parsed = PHONE_UTIL.parse(cleaned, defaultCountry);

            if (!PHONE_UTIL.isValidNumber(parsed)) {
                throw new InvalidPhoneException("Phone number is not valid for the region");
            }

            String regionCode = PHONE_UTIL.getRegionCodeForNumber(parsed);
            String e164 = PHONE_UTIL.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);

            // Bangladesh-specific validation
            if ("BD".equals(regionCode)) {
                validateBangladeshNumber(e164);
            }

            return new Phone(e164, regionCode);

        } catch (NumberParseException e) {
            throw new InvalidPhoneException("Cannot parse phone number: " + e.getMessage());
        }
    }

    /**
     * Creates a Phone with Bangladesh as default country.
     */
    public static Phone of(String phone) {
        return of(phone, "BD");
    }

    private static void validateBangladeshNumber(String e164) {
        if (!BD_PREFIX_PATTERN.matcher(e164).matches()) {
            throw new InvalidPhoneException(
                    "Invalid Bangladesh phone number. Must start with valid operator prefix (013-019)");
        }
    }

    /**
     * Returns the E.164 normalized value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Returns the country code.
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Returns a masked version of the phone for logging/display.
     */
    public String getMasked() {
        if (value.length() < 6) {
            return "***";
        }
        return value.substring(0, 4) + "****" + value.substring(value.length() - 2);
    }

    /**
     * Returns a formatted local display version.
     */
    public String getFormatted() {
        try {
            Phonenumber.PhoneNumber parsed = PHONE_UTIL.parse(value, countryCode);
            return PHONE_UTIL.format(parsed, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            return value;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Phone phone = (Phone) o;
        return Objects.equals(value, phone.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
