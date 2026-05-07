import { useEffect, useState } from "react";
import type { Compra, CompraRequest } from "../types";
import {
  dataBrParaIso,
  dataIsoParaBr,
  formatarDataDigitacao
} from "../lib/utils";

type Props = {
  open: boolean;
  initialData?: Compra | null;
  onClose: () => void;
  onSubmit: (payload: CompraRequest) => Promise<void>;
};

type FormState = {
  item: string;
  valor: number;
  data: string;
  solicitadoPor: string;
  setor: string;
};

const initialForm: FormState = {
  item: "",
  valor: 0,
  data: "",
  solicitadoPor: "",
  setor: ""
};

export default function PurchaseFormModal({
  open,
  initialData,
  onClose,
  onSubmit
}: Props) {
  const [form, setForm] = useState<FormState>(initialForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!open) return;

    if (initialData) {
      setForm({
        item: initialData.item,
        valor: initialData.valor,
        data: dataIsoParaBr(initialData.data),
        solicitadoPor: initialData.solicitadoPor,
        setor: initialData.setor
      });
    } else {
      const hoje = new Date();
      const dia = String(hoje.getDate()).padStart(2, "0");
      const mes = String(hoje.getMonth() + 1).padStart(2, "0");
      const ano = hoje.getFullYear();

      setForm({
        ...initialForm,
        data: `${dia}/${mes}/${ano}`
      });
    }
  }, [open, initialData]);

  if (!open) return null;

  function update<K extends keyof FormState>(field: K, value: FormState[K]) {
    setForm((prev) => ({
      ...prev,
      [field]: value
    }));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();

    const dataIso = dataBrParaIso(form.data);
    if (!dataIso) {
      alert("Informe a data no formato dd/mm/aaaa.");
      return;
    }

    setSaving(true);
    try {
      await onSubmit({
        item: form.item,
        valor: Number(form.valor),
        data: dataIso,
        solicitadoPor: form.solicitadoPor,
        setor: form.setor
      });
      onClose();
    } finally {
      setSaving(false);
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 p-4">
      <div className="card w-full max-w-2xl p-6">
        <div className="mb-5">
          <h3 className="text-xl font-semibold text-slate-900">
            {initialData ? "Editar compra" : "Nova compra"}
          </h3>
        </div>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="grid gap-4 md:grid-cols-2">
            <div className="md:col-span-2">
              <label className="label">Item</label>
              <input
                className="input"
                value={form.item}
                onChange={(e) => update("item", e.target.value)}
                required
              />
            </div>

            <div>
              <label className="label">Valor</label>
              <input
                className="input"
                type="number"
                step="0.01"
                value={form.valor}
                onChange={(e) => update("valor", Number(e.target.value))}
                required
              />
            </div>

            <div>
              <label className="label">Data</label>
              <input
                className="input"
                type="text"
                inputMode="numeric"
                placeholder="dd/mm/aaaa"
                value={form.data}
                onChange={(e) => update("data", formatarDataDigitacao(e.target.value))}
                required
              />
            </div>

            <div>
              <label className="label">Solicitado por</label>
              <input
                className="input"
                value={form.solicitadoPor}
                onChange={(e) => update("solicitadoPor", e.target.value)}
                placeholder="Nome"
                required
              />
            </div>

            <div>
              <label className="label">Setor</label>
              <input
                className="input"
                value={form.setor}
                onChange={(e) => update("setor", e.target.value)}
                placeholder="Área"
                required
              />
            </div>
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button type="button" className="btn-secondary" onClick={onClose}>
              Cancelar
            </button>
            <button type="submit" className="btn-primary" disabled={saving}>
              {saving ? "Salvando..." : "Salvar"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}