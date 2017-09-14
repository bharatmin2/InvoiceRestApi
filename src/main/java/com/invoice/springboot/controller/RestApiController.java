
/**
 * Created by bbhatia on 9/9/17.
 */
package com.invoice.springboot.controller;

        import com.invoice.springboot.model.Invoice;
        import com.invoice.springboot.service.InvoiceService;
        import com.invoice.springboot.util.CustomErrorType;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import org.apache.logging.log4j.LogManager;
        import org.apache.logging.log4j.Logger;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.HttpHeaders;
        import org.springframework.http.HttpStatus;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.web.util.UriComponentsBuilder;

//Cross origin - should be finally handled using Cross Origin Filters
@CrossOrigin(origins = {"*"}, maxAge = 4800, allowCredentials = "false")
@RestController
@RequestMapping({"/api"})
@SuppressWarnings("unchecked")
public class RestApiController
{
    private static Logger logger = LogManager.getLogger(RestApiController.class);
    @Autowired
    InvoiceService invoiceService;

    public RestApiController() {}

    //Get an invoice. Not required for frontend sample app for now, but good to have this for extensibility
    @RequestMapping(value={"/invoice/{id}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<?> getInvoice(@PathVariable("id") String id)
    {
        Map<String, Object> result = new HashMap();
        logger.info("Fetching Invoice with id {}", id);

            List<Invoice> invoice = invoiceService.findInvoice(id);
            if (invoice.size()<1) {
                logger.error("Invoice with id {} not found.", id);
                return new ResponseEntity(new CustomErrorType("Invoice with id " + id + " not found"), HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity(invoice, HttpStatus.OK);
    }

    //Post a new invoice with basic validation like invoice existence and ensuring not an empty array to begin iwth
    @RequestMapping(value={"/invoice/"}, method={org.springframework.web.bind.annotation.RequestMethod.POST})
    public ResponseEntity<?> createInvoice(@RequestBody List<Invoice> invoice, UriComponentsBuilder ucBuilder)
    {
        logger.info("Creating Invoice : {}", invoice);
        Map<String, Object> result = new HashMap();

            if(invoice.size()<1) return new ResponseEntity(new CustomErrorType("Unable to create " +
                    "the invoice, " + "nothing to do " + invoice.get(0).getId()), HttpStatus.BAD_REQUEST);
            Invoice id = invoice.get(0);
            if (invoiceService.isInvoiceExist(id)) {
                logger.error("Unable to create. A Invoice with name {} already exist", invoice);
                return new ResponseEntity(new CustomErrorType("Unable to create. A Invoice  " +
                        invoice.get(0).getId()
                         + " already exist."), HttpStatus.CONFLICT);
            }

            if(invoiceService.saveInvoice(invoice)) {

                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(ucBuilder.path("/api/invoice/{id}").buildAndExpand(new Object[]{invoice.get(0)
                        .getId()}).toUri());
                return new ResponseEntity(result, headers, HttpStatus.CREATED);
            }
            else return new ResponseEntity(new CustomErrorType("Unable to create the invoice "),
                    HttpStatus.INTERNAL_SERVER_ERROR);
    }
    // Delete method for invoice. In real world to implemented with proper authorization - token based
    @RequestMapping(value={"/invoice/{id}"}, method={org.springframework.web.bind.annotation.RequestMethod.DELETE})
    public ResponseEntity<?> deleteInvoice(@PathVariable("id") String id)
    {
        logger.info("Fetching & Deleting Invoice with id {}", id);
        Map<String, Object> result = new HashMap();

            List<Invoice> invoice = invoiceService.findInvoice(id);
            if (invoice == null) {
                logger.error("Unable to delete. Invoice with id {} not found.", id);
                return new ResponseEntity(new CustomErrorType("Unable to delete. Invoice with id " + id + " not found."), HttpStatus.NOT_FOUND);
            }

            if (invoiceService.deleteInvoice(id)) {
                return new ResponseEntity(HttpStatus.NO_CONTENT);
            }
            else {
                return new ResponseEntity(new CustomErrorType("Unable to delete. Invoice with id " + id), HttpStatus.INTERNAL_SERVER_ERROR);
            }
    }
    //Not required for this scope but helpful in getting total via API and not just frontend app
    @RequestMapping(value={"/total/{id}"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ResponseEntity<?> gettotal(@PathVariable("id") String id)
    {
        Map<String, Object> result = new HashMap();
        Double total = invoiceService.returnTotal(id);

        if(total!=null) {
            result.put("total", total);
            return new ResponseEntity(result, HttpStatus.OK);
        }
        return new ResponseEntity(new CustomErrorType("Unable to calculate total for Invoice with id " + id), HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
