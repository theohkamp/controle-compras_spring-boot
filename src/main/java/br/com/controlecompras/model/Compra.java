package br.com.controlecompras.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Compra {

    private Integer id;
    private String item;
    private BigDecimal valor;
    private LocalDate data;
    private String solicitadoPor;
    private String setor;

    public Compra() {
    }

    public Compra(Integer id, String item, BigDecimal valor, LocalDate data, String solicitadoPor, String setor) {
        this.id = id;
        this.item = item;
        this.valor = valor;
        this.data = data;
        this.solicitadoPor = solicitadoPor;
        this.setor = setor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getSolicitadoPor() {
        return solicitadoPor;
    }

    public void setSolicitadoPor(String solicitadoPor) {
        this.solicitadoPor = solicitadoPor;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }
}