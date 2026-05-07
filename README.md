# Controle de Compras

Sistema de controle de compras desenvolvido em **Java (Spring Boot)** com **frontend moderno**, permitindo gerenciar registros de forma simples, rápida e eficiente.

---

## Funcionalidades

- Cadastro de compras
- Edição e exclusão
- Filtros por:
  - Data (dd/mm/aaaa)
  - Valor
  - Setor
  - Solicitante
- Totalização automática
- Seleção de múltiplas compras
- Exportação em PDF:
  - Todas filtradas
  - Apenas selecionadas
---

## Tecnologias utilizadas

### Backend
- Java 17+
- Spring Boot
- SQLite
- PDFBox

### Frontend
- React
- TypeScript
- Vite
- TailwindCSS

## Como Executar
### Raiz do Projeto

#### Back-End
- Com o Maven instalado:
- `mvn spring-boot:run`

#### Front-End
- `cd frontend-controle-compras`
- `npm install`
- `npm run dev`
