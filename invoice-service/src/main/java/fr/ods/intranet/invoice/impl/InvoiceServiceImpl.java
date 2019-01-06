package fr.ods.intranet.invoice.impl;

import fr.ods.intranet.invoice.File;
import fr.ods.intranet.invoice.Invoice;
import fr.ods.intranet.invoice.InvoiceService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {

    private Vertx vertx;

    public InvoiceServiceImpl(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void getAll(Handler<AsyncResult<JsonArray>> resultHandler) {
        System.out.println("getAll");
        Invoice invoice = new Invoice();
        invoice.setClient("Cmm");
        invoice.setId("1");
        invoice.setProjet("Extranet Client");
        invoice.setNoFacture("31122018001");
        invoice.setSommeHT(6000);
        invoice.setSommeTTC(7200);
        invoice.setSommepayeeTTC(7200);
        invoice.setDate(new Date());
        invoice.setDatePaiement(new Date());
        List<File> files = new ArrayList<>();
        File file = new File();
        file.setFilename("31122018001.pdf");
        file.setPath("c:/temp/31122018001.pdf");
        file.setSize(54652);
        files.add(file);
        invoice.setFiles(files);
        List<JsonObject> invoices = new ArrayList();
        invoices.add(invoice.toJson());
        JsonArray json = new JsonArray(invoices);
        resultHandler.handle(Future.succeededFuture(json));

    }
}
