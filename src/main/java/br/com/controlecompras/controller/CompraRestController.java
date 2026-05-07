package br.com.controlecompras.controller;

import br.com.controlecompras.dto.CompraRequest;
import br.com.controlecompras.dto.ExportarSelecionadasRequest;
import br.com.controlecompras.dto.ResumoResponse;
import br.com.controlecompras.model.Compra;
import br.com.controlecompras.service.CompraService;
import br.com.controlecompras.service.PdfService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraRestController {

    private final CompraService compraService;

    public CompraRestController(CompraService compraService) {
        this.compraService = compraService;
    }

    @GetMapping
    public List<Compra> listar(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,

            @RequestParam(required = false)
            BigDecimal valorMin,

            @RequestParam(required = false)
            BigDecimal valorMax,

            @RequestParam(required = false)
            String setor,

            @RequestParam(required = false)
            String solicitante
    ) {
        return compraService.listarFiltradas(
                dataInicio,
                dataFim,
                valorMin,
                valorMax,
                setor,
                solicitante
        );
    }

    @GetMapping("/{id}")
    public Compra buscarPorId(@PathVariable int id) {
        return compraService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Compra> criar(@Valid @RequestBody CompraRequest request) {
        Compra compra = compraService.criar(request);
        return ResponseEntity.ok(compra);
    }

    @PutMapping("/{id}")
    public Compra atualizar(@PathVariable int id, @Valid @RequestBody CompraRequest request) {
        return compraService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable int id) {
        compraService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumo")
    public ResumoResponse resumo(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,

            @RequestParam(required = false)
            BigDecimal valorMin,

            @RequestParam(required = false)
            BigDecimal valorMax,

            @RequestParam(required = false)
            String setor,

            @RequestParam(required = false)
            String solicitante
    ) {
        return compraService.gerarResumo(
                dataInicio,
                dataFim,
                valorMin,
                valorMax,
                setor,
                solicitante
        );
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicio,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFim,

            @RequestParam(required = false)
            BigDecimal valorMin,

            @RequestParam(required = false)
            BigDecimal valorMax,

            @RequestParam(required = false)
            String setor,

            @RequestParam(required = false)
            String solicitante
    ) throws Exception {
        List<Compra> compras = compraService.listarFiltradas(
                dataInicio,
                dataFim,
                valorMin,
                valorMax,
                setor,
                solicitante
        );

        return gerarRespostaPdf(compras, "relatorio-compras.pdf");
    }

    @PostMapping("/export/pdf/selecionadas")
    public ResponseEntity<byte[]> exportarPdfSelecionadas(
            @Valid @RequestBody ExportarSelecionadasRequest request
    ) throws Exception {
        List<Compra> compras = compraService.buscarPorIds(request.getIds());
        return gerarRespostaPdf(compras, "relatorio-compras-selecionadas.pdf");
    }

    private ResponseEntity<byte[]> gerarRespostaPdf(List<Compra> compras, String nomeArquivo) throws Exception {
        File temp = File.createTempFile("relatorio-compras-", ".pdf");

        try {
            PdfService.gerar(temp, compras);

            byte[] pdfBytes = Files.readAllBytes(temp.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.inline()
                            .filename(nomeArquivo)
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } finally {
            temp.delete();
        }
    }
}