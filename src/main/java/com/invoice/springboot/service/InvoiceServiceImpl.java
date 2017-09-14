package com.invoice.springboot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoice.springboot.model.Invoice;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.bulk.byscroll.BulkByScrollResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.stereotype.Service;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

@Service("invoiceService")
public class InvoiceServiceImpl
        implements InvoiceService
{

    private static Logger logger = LogManager.getLogger(InvoiceServiceImpl.class);
    static TransportClient client = null;

    private static List<Invoice> invoices;

    //Initialization of the ES client.
    static
    {
        Settings settings = Settings.builder().put("cluster.name", "invoice").build();
        try
        {
            client = new PreBuiltTransportClient(settings, new Class[0]).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    //Put in here to prepopulate some existing data in ES
    static{
        populateDummyUsers();
    }

    // ES search
    public List<Invoice> findInvoice(String id)
    {
        List<Invoice> invoices = new ArrayList();
        ObjectMapper mapper = new ObjectMapper();
        Invoice invoice = new Invoice();
        QueryBuilder qb = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("id", id));

        SearchResponse response;


        try {
            response = (SearchResponse)client.prepareSearch("invoices")
                    .setTypes("invoice")
                    .setQuery(qb)
                    .get();
        }
        catch (IndexNotFoundException e) {
            logger.info("Cant find because index doesn't exist", id);
            return null;
        }

        for (SearchHit hit : response.getHits().getHits()) {
                try {
                    invoice = (Invoice)mapper.readValue(hit.getSourceAsString(), Invoice.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                invoices.add(invoice);
            }

        return invoices;
    }

    //Ensuring Invoice Id doesnt already exist in ES
    public boolean isInvoiceExist(Invoice invoice) {
        return (findInvoice(invoice.getId()).size()>0);
    }

    //Saving the invoice in ES, parsing each invoice item and storing as seperate document, not a major overhead in ES even if flattened
    public boolean saveInvoice(List<Invoice> invoice) {
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        ObjectMapper mapper = new ObjectMapper();
        invoice.forEach((Consumer<? super Invoice>) item ->
                {
                    String jsonInString = null;
                    try {
                        jsonInString = mapper.writeValueAsString(item);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }

                    bulkRequest.add(client.prepareIndex("invoices", "invoice")
                            .setSource(jsonInString));
                }
            );
        BulkResponse bulkResponse = bulkRequest.get();
        if (bulkResponse.hasFailures()) {
            logger.info(bulkResponse.getItems());
            //Handle errors here
            return false;
        }
        return true;
    }

    //Delete the invoice from ES
     public boolean deleteInvoice(String id)
     {

        BulkByScrollResponse response =
                DeleteByQueryAction.INSTANCE.newRequestBuilder(client)
                        .filter(QueryBuilders.matchQuery("id", id))
                        .source("invoices")
                        .get();
            return true;
     }

    //Return total of the invoice items in a particular invoice id.
    public Double returnTotal(String id)
    {
        double total = 0.0D;

        try {
            SearchResponse sr = (SearchResponse) client.prepareSearch(new String[0]).setIndices(new String[]{"invoices"}).setTypes(new String[]{"invoice"}).setQuery(QueryBuilders.matchQuery("id", id ))
                    .addAggregation(AggregationBuilders.sum("agg").field("amount")).get();
            Sum agg = (Sum) sr.getAggregations().get("agg");
            total = BigDecimal.valueOf(agg.getValue()).setScale(3, RoundingMode.HALF_UP).doubleValue();
        }catch (IndexNotFoundException e) {
            logger.info("Could not calculate total", id);
            return null;
        }
        return total;
    }

    public InvoiceServiceImpl() {}

    //Just dummy data  not required in production ready app
    private static void populateDummyUsers(){

        try {
            XContentBuilder builder = jsonBuilder()
                    .startObject()
                    .field("user", "kimchy")
                    .field("postDate", new Date())
                    .field("message", "trying out Elasticsearch")
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "kimchy")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
                    )
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
