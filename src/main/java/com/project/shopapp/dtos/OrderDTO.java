package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User's ID must be > 0")
    private Long userId;

    @NotEmpty(message = "Name is required!")
    @JsonProperty("full_name")
    private String fullName;

    @NotEmpty(message = "Email is required!")
    @Email(regexp = ".+@.+\\..+", message = "Please provide a valid email address!")
    private String email;

    @JsonProperty("phone_number")
    @NotEmpty(message = "Phone number is required!")
    @Size(min = 5, message = "Phone number must be at least 5 characters!")
    private String phoneNumber;

    @NotEmpty(message = "Address is required!")
    private String address;

    private String note;

    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private Float totalMoney;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("payment_method")
    @NotEmpty(message = "Payment method is required!")
    private String paymentMethod;
}


