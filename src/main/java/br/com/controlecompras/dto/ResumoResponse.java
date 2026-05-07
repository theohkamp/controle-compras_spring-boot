package br.com.controlecompras.dto;

import java.math.BigDecimal;

public class ResumoResponse {

    private long quantidade;
    private BigDecimal total;

    public ResumoResponse() {
    }

    public ResumoResponse(long quantidade, BigDecimal total) {
        this.quantidade = quantidade;
        this.total = total;
    }

    public long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(long quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}