package com.invoice.springboot.service;

import com.invoice.springboot.model.Invoice;
import java.util.List;

public abstract interface InvoiceService
{
    public abstract List<Invoice> findInvoice(String paramString);

    public abstract boolean saveInvoice(List<Invoice> paramInvoice);

    public abstract boolean deleteInvoice(String paramString);

    public abstract boolean isInvoiceExist(Invoice paramInvoice);

    public abstract Double returnTotal(String paramString);
}
