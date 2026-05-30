package module2_homework.spring_web_mvc.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import module2_homework.spring_web_mvc.annotations.PasswordValidation;
import module2_homework.spring_web_mvc.annotations.PrimeNumberValidation;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import java.util.Date;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {
//    @Null, @NotNull, @AssertTrue, @AssertFalse, @Min, @Max, @DecimalMin,
//    @DecimalMax, @Negative, @NegativeOrZero, @Positive, @PositiveOrZero,
//    @Size, @Digits, @Past, @PastOrPresent, @Future, @FutureOrPresent,
//    @Pattern, @Email, @NotEmpty, @NotBlank, @Length, @Range,
//    @CreditCardNumber, @URL

    @NotBlank
    @Length(min=1, max=10)
    @Min(value = 5)
    @Max(value = 10)
    private String id;
    @NotBlank
    private String title;
    @NotNull
    private Boolean isActive;

    @CreditCardNumber
    private String creditCardNumber;

    @URL
    private String url;

    @Email
    private String email;

    @Digits(integer = 8, fraction = 2)
    private Integer digit;

    @Range(min = 1L, max = 1000L)
    private Double amount;

    @Null
    private Long tempId;

    @PastOrPresent
    @NotNull
    private Date doj;

    @Past
    @NotNull
    private Date dob;

    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String mobileNumber;

    @Future
    private LocalDate contractEndDate;

    @FutureOrPresent
    private LocalDate joiningDate;

    @AssertFalse
    private Boolean terminated;

    @PrimeNumberValidation
    private Integer primeNumber;

    @PasswordValidation
    private String password;
}
