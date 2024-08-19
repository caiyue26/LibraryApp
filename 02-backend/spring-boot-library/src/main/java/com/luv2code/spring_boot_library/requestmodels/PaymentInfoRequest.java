// reviewed on 2024.07.15

package com.luv2code.spring_boot_library.requestmodels;

import lombok.Data;

@Data
public class PaymentInfoRequest {

    private int amount;
    private String currency;
    private String receiptEmail;

}
