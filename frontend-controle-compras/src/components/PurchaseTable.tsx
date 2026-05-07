import type { Compra } from "../types";
import { formatarData, moeda } from "../lib/utils";

type Props = {
  data: Compra[];
  selectedIds: number[];
  onToggleSelect: (id: number) => void;
  onToggleSelectAll: () => void;
  onEdit: (compra: Compra) => void;
  onDelete: (compra: Compra) => void;
};

export default function PurchaseTable({
  data,
  selectedIds,
  onToggleSelect,
  onToggleSelectAll,
  onEdit,
  onDelete
}: Props) {
  const allSelected = data.length > 0 && data.every((c) => selectedIds.includes(c.id));

  return (
    <div className="card overflow-hidden">
      <div className="border-b border-slate-200 px-5 py-4">
        <h3 className="text-base font-semibold text-slate-900">Compras</h3>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full border-collapse">
          <thead>
            <tr className="bg-slate-50">
              <th className="px-5 py-3 text-left text-sm font-semibold text-slate-600">
                <input
                  type="checkbox"
                  checked={allSelected}
                  onChange={onToggleSelectAll}
                />
              </th>
              <th className="px-5 py-3 text-left text-sm font-semibold text-slate-600">ID</th>
              <th className="px-5 py-3 text-left text-sm font-semibold text-slate-600">Item</th>
              <th className="px-5 py-3 text-left text-sm font-semibold text-slate-600">Solicitante</th>
              <th className="px-5 py-3 text-left text-sm font-semibold text-slate-600">Setor</th>
              <th className="px-5 py-3 text-left text-sm font-semibold text-slate-600">Data</th>
              <th className="px-5 py-3 text-right text-sm font-semibold text-slate-600">Valor</th>
              <th className="px-5 py-3 text-right text-sm font-semibold text-slate-600">Ações</th>
            </tr>
          </thead>

          <tbody>
            {data.length === 0 ? (
              <tr>
                <td colSpan={8} className="px-5 py-10 text-center text-sm text-slate-500">
                  Nenhuma compra encontrada.
                </td>
              </tr>
            ) : (
              data.map((c) => (
                <tr key={c.id} className="border-t border-slate-100">
                  <td className="table-cell">
                    <input
                      type="checkbox"
                      checked={selectedIds.includes(c.id)}
                      onChange={() => onToggleSelect(c.id)}
                    />
                  </td>
                  <td className="table-cell">{c.id}</td>
                  <td className="table-cell font-medium text-slate-900">{c.item}</td>
                  <td className="table-cell">{c.solicitadoPor}</td>
                  <td className="table-cell">{c.setor}</td>
                  <td className="table-cell">{formatarData(c.data)}</td>
                  <td className="table-cell text-right">{moeda(c.valor)}</td>
                  <td className="table-cell">
                    <div className="flex justify-end gap-2">
                      <button
                        className="btn-secondary px-3 py-2"
                        onClick={() => onEdit(c)}
                      >
                        Editar
                      </button>

                      <button
                        className="btn-danger px-3 py-2"
                        onClick={() => onDelete(c)}
                      >
                        Excluir
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}