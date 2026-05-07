package br.com.controlecompras.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ExportarSelecionadasRequest {

    @NotEmpty(message = "Informe ao menos um ID para exportação.")
    private List<Integer> ids;

    public ExportarSelecionadasRequest() {
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids;
    }
}