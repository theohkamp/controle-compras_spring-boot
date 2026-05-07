import type { Filters } from "../types";
import { formatarDataDigitacao } from "../lib/utils";

type Props = {
  filters: Filters;
  setFilters: (filters: Filters) => void;
  onApply: () => void;
  onClear: () => void;
  onExportPdf: () => void;
  selectedCount: number;
};

export default function FiltersBar({
  filters,
  setFilters,
  onApply,
  onClear,
  onExportPdf,
  selectedCount
}: Props) {
  function update<K extends keyof Filters>(field: K, value: Filters[K]) {
    setFilters({
      ...filters,
      [field]: value
    });
  }

  return (
    <div className="card p-5">
      <div className="mb-4">
        <h3 className="text-base font-semibold text-slate-900">Filtros</h3>
      </div>

      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-6">
        <div>
          <label className="label">Data inicial</label>
          <input
            className="input"
            type="text"
            inputMode="numeric"
            placeholder="dd/mm/aaaa"
            value={filters.dataInicio}
            onChange={(e) => update("dataInicio", formatarDataDigitacao(e.target.value))}
          />
        </div>

        <div>
          <label className="label">Data final</label>
          <input
            className="input"
            type="text"
            inputMode="numeric"
            placeholder="dd/mm/aaaa"
            value={filters.dataFim}
            onChange={(e) => update("dataFim", formatarDataDigitacao(e.target.value))}
          />
        </div>

        <div>
          <label className="label">Valor mínimo</label>
          <input
            className="input"
            type="number"
            step="0.01"
            value={filters.valorMin}
            onChange={(e) => update("valorMin", e.target.value)}
            placeholder="0,00"
          />
        </div>

        <div>
          <label className="label">Valor máximo</label>
          <input
            className="input"
            type="number"
            step="0.01"
            value={filters.valorMax}
            onChange={(e) => update("valorMax", e.target.value)}
            placeholder="0,00"
          />
        </div>

        <div>
          <label className="label">Setor</label>
          <input
            className="input"
            value={filters.setor}
            onChange={(e) => update("setor", e.target.value)}
            placeholder="Área"
          />
        </div>

        <div>
          <label className="label">Solicitante</label>
          <input
            className="input"
            value={filters.solicitante}
            onChange={(e) => update("solicitante", e.target.value)}
            placeholder="Nome"
          />
        </div>
      </div>

      <div className="mt-5 flex flex-wrap items-center gap-3">
        <button className="btn-primary" onClick={onApply}>
          Filtrar
        </button>

        <button className="btn-secondary" onClick={onClear}>
          Limpar
        </button>

        <button className="btn-secondary" onClick={onExportPdf}>
          {selectedCount > 0
            ? `Exportar selecionadas (${selectedCount})`
            : "Exportar filtradas"}
        </button>
      </div>
    </div>
  );
}