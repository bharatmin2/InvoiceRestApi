package com.InvoiceRestApi.springboot;

/**
 * Created by bbhatia on 9/9/17.
 */
import java.net.URI;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.invoice.springboot.model.Invoice;
import org.springframework.web.client.RestTemplate;


//Testing using Rest Template.

public class SpringBootRestTestClient {

    public static final String REST_SERVICE_URI = "http://localhost:8080/SpringBootRestApi/api";

    /* GET */
    private static void findById() {
        System.out.println("Testing findById API----------");
        RestTemplate restTemplate = new RestTemplate();
        Invoice invoice = restTemplate.getForObject(REST_SERVICE_URI + "/invoice/100", Invoice.class);
        System.out.println(invoice);
    }

    /* POST */
    private static void saveInvoice() {
        System.out.println("Testing create Invoice API----------");
        RestTemplate restTemplate = new RestTemplate();
        Invoice invoice = new Invoice("100", "Sarah", "sarah@yahoo.com", new Date(), "Test_Sarah", 5.5);
        URI uri = restTemplate.postForLocation(REST_SERVICE_URI + "/invoice/", invoice, Invoice.class);
        System.out.println("Location : " + uri.toASCIIString());
    }

    /* DELETE */
    private static void deleteInvoiceById() {
        System.out.println("Testing deleteInvoiceById API----------");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(REST_SERVICE_URI + "/invoice/100");
    }


    public static void main(String args[]) {
        saveInvoice();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        findById();
        deleteInvoiceById();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
