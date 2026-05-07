import { useEffect, useMemo, useState } from "react";
import Header from "./components/Header";
import SummaryCards from "./components/SummaryCards";
import FiltersBar from "./components/FiltersBar";
import PurchaseTable from "./components/PurchaseTable";
import PurchaseFormModal from "./components/PurchaseFormModal";
import ConfirmDialog from "./components/ConfirmDialog";
import { api } from "./lib/api";
import { baixarArquivo, dataBrParaIso } from "./lib/utils";
import type { Compra, CompraRequest, Filters } from "./types";

const initialFilters: Filters = {
  dataInicio: "",
  dataFim: "",
  valorMin: "",
  valorMax: "",
  setor: "",
  solicitante: ""
};

export default function App() {
  const [compras, setCompras] = useState<Compra[]>([]);
  const [quantidade, setQuantidade] = useState(0);
  const [total, setTotal] = useState(0);
  const [filters, setFilters] = useState<Filters>(initialFilters);

  const [modalOpen, setModalOpen] = useState(false);
  const [editingCompra, setEditingCompra] = useState<Compra | null>(null);
  const [deleteTarget, setDeleteTarget] = useState<Compra | null>(null);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  function filtrosParaApi(currentFilters: Filters) {
    return {
      ...currentFilters,
      dataInicio: dataBrParaIso(currentFilters.dataInicio),
      dataFim: dataBrParaIso(currentFilters.dataFim)
    };
  }

  async function carregarDados(currentFilters: Filters = filters) {
    const filtrosConvertidos = filtrosParaApi(currentFilters);

    const [lista, resumo] = await Promise.all([
      api.listar(filtrosConvertidos),
      api.resumo(filtrosConvertidos)
    ]);

    setCompras(lista);
    setQuantidade(resumo.quantidade);
    setTotal(resumo.total);
    setSelectedIds([]);
  }

  useEffect(() => {
    void carregarDados();
  }, []);

  async function handleSave(payload: CompraRequest) {
    if (editingCompra) {
      await api.atualizar(editingCompra.id, payload);
    } else {
      await api.criar(payload);
    }

    setEditingCompra(null);
    await carregarDados();
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    await api.excluir(deleteTarget.id);
    setDeleteTarget(null);
    await carregarDados();
  }

  const comprasSelecionadas = useMemo(
    () => compras.filter((c) => selectedIds.includes(c.id)),
    [compras, selectedIds]
  );

  async function handleExportPdf() {
    if (comprasSelecionadas.length === 0) {
      const blob = await api.pdf(filtrosParaApi(filters));
      baixarArquivo(blob, "relatorio-compras.pdf");
      return;
    }

    const blob = await api.pdfSelecionadas(selectedIds);
    baixarArquivo(blob, "relatorio-compras-selecionadas.pdf");
  }

  function toggleSelect(id: number) {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  }

  function toggleSelectAll() {
    if (compras.length > 0 && compras.every((c) => selectedIds.includes(c.id))) {
      setSelectedIds([]);
      return;
    }

    setSelectedIds(compras.map((c) => c.id));
  }

  return (
    <div className="min-h-screen bg-slate-50">
      <div className="mx-auto max-w-7xl space-y-6 p-4 md:p-6">
        <Header
          onNew={() => {
            setEditingCompra(null);
            setModalOpen(true);
          }}
        />

        <SummaryCards qtd={quantidade} total={total} />

        <FiltersBar
          filters={filters}
          setFilters={setFilters}
          selectedCount={selectedIds.length}
          onApply={() => {
            void carregarDados(filters);
          }}
          onClear={() => {
            setFilters(initialFilters);
            void carregarDados(initialFilters);
          }}
          onExportPdf={() => {
            void handleExportPdf();
          }}
        />

        <PurchaseTable
          data={compras}
          selectedIds={selectedIds}
          onToggleSelect={toggleSelect}
          onToggleSelectAll={toggleSelectAll}
          onEdit={(compra) => {
            setEditingCompra(compra);
            setModalOpen(true);
          }}
          onDelete={(compra) => {
            setDeleteTarget(compra);
          }}
        />
      </div>

      <PurchaseFormModal
        open={modalOpen}
        initialData={editingCompra}
        onClose={() => {
          setModalOpen(false);
          setEditingCompra(null);
        }}
        onSubmit={handleSave}
      />

      <ConfirmDialog
        open={Boolean(deleteTarget)}
        title="Excluir compra"
        description={
          deleteTarget
            ? `Deseja realmente excluir "${deleteTarget.item}"?`
            : ""
        }
        onCancel={() => setDeleteTarget(null)}
        onConfirm={handleDelete}
      />
    </div>
  );
}