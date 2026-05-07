package br.com.controlecompras.service;

import br.com.controlecompras.dao.CompraDAO;
import br.com.controlecompras.dto.CompraRequest;
import br.com.controlecompras.dto.ResumoResponse;
import br.com.controlecompras.model.Compra;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CompraService {

    private final CompraDAO compraDAO;

    public CompraService(CompraDAO compraDAO) {
        this.compraDAO = compraDAO;
    }

    public Compra criar(CompraRequest request) {
        Compra compra = new Compra(
                null,
                request.getItem(),
                request.getValor(),
                request.getData(),
                request.getSolicitadoPor(),
                request.getSetor()
        );

        return compraDAO.inserir(compra);
    }

    public Compra atualizar(int id, CompraRequest request) {
        buscarPorId(id);

        Compra compra = new Compra(
                id,
                request.getItem(),
                request.getValor(),
                request.getData(),
                request.getSolicitadoPor(),
                request.getSetor()
        );

        return compraDAO.atualizar(compra);
    }

    public void excluir(int id) {
        buscarPorId(id);
        compraDAO.excluir(id);
    }

    public Compra buscarPorId(int id) {
        return compraDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Compra não encontrada."));
    }

    public List<Compra> buscarPorIds(List<Integer> ids) {
        List<Compra> compras = compraDAO.buscarPorIds(ids);

        if (compras.isEmpty()) {
            throw new IllegalArgumentException("Nenhuma compra encontrada para os IDs informados.");
        }

        return compras;
    }

    public List<Compra> listarFiltradas(
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorMin,
            BigDecimal valorMax,
            String setor,
            String solicitante
    ) {
        return compraDAO.listarTodas().stream()
                .filter(c -> dataInicio == null || !c.getData().isBefore(dataInicio))
                .filter(c -> dataFim == null || !c.getData().isAfter(dataFim))
                .filter(c -> valorMin == null || c.getValor().compareTo(valorMin) >= 0)
                .filter(c -> valorMax == null || c.getValor().compareTo(valorMax) <= 0)
                .filter(c -> setor == null || setor.isBlank() || c.getSetor().toLowerCase().contains(setor.toLowerCase()))
                .filter(c -> solicitante == null || solicitante.isBlank() || c.getSolicitadoPor().toLowerCase().contains(solicitante.toLowerCase()))
                .toList();
    }

    public ResumoResponse gerarResumo(
            LocalDate dataInicio,
            LocalDate dataFim,
            BigDecimal valorMin,
            BigDecimal valorMax,
            String setor,
            String solicitante
    ) {
        List<Compra> compras = listarFiltradas(dataInicio, dataFim, valorMin, valorMax, setor, solicitante);

        BigDecimal total = compras.stream()
                .map(Compra::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ResumoResponse(compras.size(), total);
    }
}