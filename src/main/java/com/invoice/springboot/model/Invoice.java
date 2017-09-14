package com.invoice.springboot.model;

import java.util.Date;
public class Invoice
{
    private String name;
    private String email;
    private Date date;
    private String id;
    private String description;
    private double amount;
    private String item_id;

    public Invoice()
    {
        id = "0";
    }

    public Invoice(String id, String name, String email, Date date, String description, double amount) {
        this.name = name;
        this.email = email;
        this.date = date;
        this.id = id;
        this.description = description;
        this.amount = amount;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }


    public int hashCode()
    {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + id.hashCode();
        result = 31 * result + item_id.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        long temp = Double.doubleToLongBits(amount);
        result = 31 * result + (int)(temp ^ temp >>> 32);
        return result;
    }


    public boolean equals(Object o)
    {
        if (this == o) return true;
        if ((o == null) || (getClass() != o.getClass())) { return false;
        }
        Invoice invoice = (Invoice)o;

        return id == id;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", date=" + date +
                ", id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", item_id='" + item_id + '\'' +
                '}';
    }
}
