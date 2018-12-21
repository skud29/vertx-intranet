package fr.ods.intranet.invoice;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

@DataObject(generateConverter=true)
public class Invoices {
    private List<Invoice> invoices = new ArrayList<>();

    public Invoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    public Invoices(JsonObject json) {
        // A converter is generated to easy the conversion from and to JSON.
        InvoicesConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        InvoicesConverter.toJson(this, json);
        return json;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
