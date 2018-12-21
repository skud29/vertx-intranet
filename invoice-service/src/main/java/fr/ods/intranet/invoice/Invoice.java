package fr.ods.intranet.invoice;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.util.List;

@DataObject(generateConverter=true)
public class Invoice {
    private String id;
    private String noFacture;
    private Date date;
    private Date datePaiement;
    private String noCommande;
    private String client;
    private String projet;
    private float sommeHT;
    private float sommeTTC;
    private float sommepayeeTTC;
    private List<File> files;

    public Invoice() {
    }

    public Invoice(JsonObject json) {
        // A converter is generated to easy the conversion from and to JSON.
        InvoiceConverter.fromJson(json, this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        InvoiceConverter.toJson(this, json);
        return json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNoFacture() {
        return noFacture;
    }

    public void setNoFacture(String noFacture) {
        this.noFacture = noFacture;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(Date datePaiement) {
        this.datePaiement = datePaiement;
    }

    public String getNoCommande() {
        return noCommande;
    }

    public void setNoCommande(String noCommande) {
        this.noCommande = noCommande;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getProjet() {
        return projet;
    }

    public void setProjet(String projet) {
        this.projet = projet;
    }

    public float getSommeHT() {
        return sommeHT;
    }

    public void setSommeHT(float sommeHT) {
        this.sommeHT = sommeHT;
    }

    public float getSommeTTC() {
        return sommeTTC;
    }

    public void setSommeTTC(float sommeTTC) {
        this.sommeTTC = sommeTTC;
    }

    public float getSommepayeeTTC() {
        return sommepayeeTTC;
    }

    public void setSommepayeeTTC(float sommepayeeTTC) {
        this.sommepayeeTTC = sommepayeeTTC;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invoice invoice = (Invoice) o;

        return id.equals(invoice.id);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return this.toJson().encodePrettily();
    }
}
