package com.invoice.springboot;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages={"com.invoice.springboot"})
public class InvoiceRestApi
{
    public InvoiceRestApi() {}

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(InvoiceRestApi.class, args);
    }
}
