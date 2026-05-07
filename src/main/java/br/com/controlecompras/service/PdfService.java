package br.com.controlecompras.service;

import br.com.controlecompras.model.Compra;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class PdfService {

    private static final float MARGIN = 40f;
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float CONTENT_WIDTH = PAGE_WIDTH - (MARGIN * 2);

    private static final float FOOTER_Y = 24f;
    private static final float TABLE_HEADER_HEIGHT = 22f;
    private static final float ROW_HEIGHT = 20f;

    private static final PDType1Font FONT_REGULAR =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDType1Font FONT_BOLD =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final DateTimeFormatter DATA_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final NumberFormat MOEDA =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static final float COL_ID = 40f;
    private static final float COL_DATA = 70f;
    private static final float COL_ITEM = 190f;
    private static final float COL_SOLICITANTE = 110f;
    private static final float COL_SETOR = 45f;
    private static final float COL_VALOR = 60f;

    public static void gerar(File arquivo, List<Compra> compras) throws IOException {
        try (PDDocument document = new PDDocument()) {
            List<Compra> listaOrdenada = compras.stream()
                    .sorted(Comparator.comparing(Compra::getData)
                            .thenComparing(c -> c.getId() == null ? 0 : c.getId()))
                    .toList();

            BigDecimal totalGeral = listaOrdenada.stream()
                    .map(Compra::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            LocalDate dataMin = listaOrdenada.stream()
                    .map(Compra::getData)
                    .min(LocalDate::compareTo)
                    .orElse(null);

            LocalDate dataMax = listaOrdenada.stream()
                    .map(Compra::getData)
                    .max(LocalDate::compareTo)
                    .orElse(null);

            int paginaAtual = 1;

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);
            float y = desenharCabecalho(content, paginaAtual, listaOrdenada.size(), totalGeral, dataMin, dataMax);
            y = desenharCabecalhoTabela(content, y);

            for (Compra compra : listaOrdenada) {
                if (y < 90) {
                    desenharRodape(content, paginaAtual);
                    content.close();

                    paginaAtual++;
                    page = new PDPage(PDRectangle.A4);
                    document.addPage(page);
                    content = new PDPageContentStream(document, page);

                    y = desenharCabecalho(content, paginaAtual, listaOrdenada.size(), totalGeral, dataMin, dataMax);
                    y = desenharCabecalhoTabela(content, y);
                }

                y = desenharLinhaCompra(content, compra, y);
            }

            y -= 14;
            desenharTotalFinal(content, totalGeral, y);
            desenharRodape(content, paginaAtual);

            content.close();
            document.save(arquivo);
        }
    }

    private static float desenharCabecalho(PDPageContentStream content,
                                           int pagina,
                                           int quantidadeRegistros,
                                           BigDecimal totalGeral,
                                           LocalDate dataMin,
                                           LocalDate dataMax) throws IOException {

        float y = PAGE_HEIGHT - 40;

        escreverTexto(content, FONT_BOLD, 18, MARGIN, y, "Relatório de Compras");
        y -= 18;
        escreverTexto(content, FONT_REGULAR, 10, MARGIN, y, "Controle de compras exportado pela API");
        y -= 26;

        desenharLinha(content, MARGIN, y, PAGE_WIDTH - MARGIN, y);
        y -= 24;

        String periodo = (dataMin != null && dataMax != null)
                ? "Período dos registros: " + dataMin.format(DATA_FORMATTER) + " até " + dataMax.format(DATA_FORMATTER)
                : "Período dos registros: -";

        escreverTexto(content, FONT_REGULAR, 10, MARGIN, y, periodo);
        escreverTextoDireita(content, FONT_REGULAR, 10, PAGE_WIDTH - MARGIN, y, "Página " + pagina);
        y -= 26;

        float boxHeight = 42f;
        content.addRect(MARGIN, y - boxHeight, CONTENT_WIDTH, boxHeight);
        content.stroke();

        escreverTexto(content, FONT_BOLD, 10, MARGIN + 10, y - 14, "Resumo");
        escreverTexto(content, FONT_REGULAR, 10, MARGIN + 10, y - 29,
                "Quantidade de compras: " + quantidadeRegistros);
        escreverTexto(content, FONT_REGULAR, 10, MARGIN + 220, y - 29,
                "Valor total: " + MOEDA.format(totalGeral));

        return y - boxHeight - 24;
    }

    private static float desenharCabecalhoTabela(PDPageContentStream content, float y) throws IOException {
        float x = MARGIN;

        content.addRect(MARGIN, y - TABLE_HEADER_HEIGHT, CONTENT_WIDTH, TABLE_HEADER_HEIGHT);
        content.stroke();

        float x1 = x + COL_ID;
        float x2 = x1 + COL_DATA;
        float x3 = x2 + COL_ITEM;
        float x4 = x3 + COL_SOLICITANTE;
        float x5 = x4 + COL_SETOR;

        desenharLinhaVertical(content, x1, y - TABLE_HEADER_HEIGHT, y);
        desenharLinhaVertical(content, x2, y - TABLE_HEADER_HEIGHT, y);
        desenharLinhaVertical(content, x3, y - TABLE_HEADER_HEIGHT, y);
        desenharLinhaVertical(content, x4, y - TABLE_HEADER_HEIGHT, y);
        desenharLinhaVertical(content, x5, y - TABLE_HEADER_HEIGHT, y);

        float textY = y - 15;

        escreverTexto(content, FONT_BOLD, 10, x + 4, textY, "ID");
        escreverTexto(content, FONT_BOLD, 10, x1 + 4, textY, "Data");
        escreverTexto(content, FONT_BOLD, 10, x2 + 4, textY, "Item");
        escreverTexto(content, FONT_BOLD, 10, x3 + 4, textY, "Solicitante");
        escreverTexto(content, FONT_BOLD, 10, x4 + 4, textY, "Setor");
        escreverTextoDireita(content, FONT_BOLD, 10, MARGIN + CONTENT_WIDTH - 6, textY, "Valor");

        return y - TABLE_HEADER_HEIGHT;
    }

    private static float desenharLinhaCompra(PDPageContentStream content, Compra compra, float y) throws IOException {
        float x = MARGIN;

        content.addRect(MARGIN, y - ROW_HEIGHT, CONTENT_WIDTH, ROW_HEIGHT);
        content.stroke();

        float x1 = x + COL_ID;
        float x2 = x1 + COL_DATA;
        float x3 = x2 + COL_ITEM;
        float x4 = x3 + COL_SOLICITANTE;
        float x5 = x4 + COL_SETOR;

        desenharLinhaVertical(content, x1, y - ROW_HEIGHT, y);
        desenharLinhaVertical(content, x2, y - ROW_HEIGHT, y);
        desenharLinhaVertical(content, x3, y - ROW_HEIGHT, y);
        desenharLinhaVertical(content, x4, y - ROW_HEIGHT, y);
        desenharLinhaVertical(content, x5, y - ROW_HEIGHT, y);

        float textY = y - 14;

        escreverTexto(content, FONT_REGULAR, 9, x + 4, textY,
                String.valueOf(compra.getId() == null ? 0 : compra.getId()));

        escreverTexto(content, FONT_REGULAR, 9, x1 + 4, textY,
                compra.getData().format(DATA_FORMATTER));

        escreverTexto(content, FONT_REGULAR, 9, x2 + 4, textY,
                cortarTexto(compra.getItem(), 34));

        escreverTexto(content, FONT_REGULAR, 9, x3 + 4, textY,
                cortarTexto(compra.getSolicitadoPor(), 18));

        escreverTexto(content, FONT_REGULAR, 9, x4 + 4, textY,
                cortarTexto(compra.getSetor(), 8));

        escreverTextoDireita(content, FONT_REGULAR, 9, MARGIN + CONTENT_WIDTH - 6, textY,
                MOEDA.format(compra.getValor()));

        return y - ROW_HEIGHT;
    }

    private static void desenharTotalFinal(PDPageContentStream content, BigDecimal totalGeral, float y) throws IOException {
        float boxWidth = 220f;
        float boxHeight = 28f;
        float boxX = PAGE_WIDTH - MARGIN - boxWidth;

        content.addRect(boxX, y - boxHeight, boxWidth, boxHeight);
        content.stroke();

        escreverTexto(content, FONT_BOLD, 10, boxX + 10, y - 17, "Total geral:");
        escreverTextoDireita(content, FONT_BOLD, 10, boxX + boxWidth - 10, y - 17, MOEDA.format(totalGeral));
    }

    private static void desenharRodape(PDPageContentStream content, int pagina) throws IOException {
        desenharLinha(content, MARGIN, FOOTER_Y + 12, PAGE_WIDTH - MARGIN, FOOTER_Y + 12);
        escreverTexto(content, FONT_REGULAR, 8, MARGIN, FOOTER_Y,
                "Relatório gerado pela API de Controle de Compras");
        escreverTextoDireita(content, FONT_REGULAR, 8, PAGE_WIDTH - MARGIN, FOOTER_Y,
                "Página " + pagina);
    }

    private static void escreverTexto(PDPageContentStream content,
                                      PDType1Font font,
                                      float fontSize,
                                      float x,
                                      float y,
                                      String texto) throws IOException {
        content.beginText();
        content.setFont(font, fontSize);
        content.newLineAtOffset(x, y);
        content.showText(texto == null ? "" : texto);
        content.endText();
    }

    private static void escreverTextoDireita(PDPageContentStream content,
                                             PDType1Font font,
                                             float fontSize,
                                             float xDireita,
                                             float y,
                                             String texto) throws IOException {
        String valor = texto == null ? "" : texto;
        float largura = font.getStringWidth(valor) / 1000f * fontSize;
        escreverTexto(content, font, fontSize, xDireita - largura, y, valor);
    }

    private static void desenharLinha(PDPageContentStream content,
                                      float x1,
                                      float y1,
                                      float x2,
                                      float y2) throws IOException {
        content.moveTo(x1, y1);
        content.lineTo(x2, y2);
        content.stroke();
    }

    private static void desenharLinhaVertical(PDPageContentStream content,
                                              float x,
                                              float y1,
                                              float y2) throws IOException {
        content.moveTo(x, y1);
        content.lineTo(x, y2);
        content.stroke();
    }

    private static String cortarTexto(String texto, int limite) {
        if (texto == null) return "";
        if (texto.length() <= limite) return texto;
        return texto.substring(0, limite - 3) + "...";
    }
}