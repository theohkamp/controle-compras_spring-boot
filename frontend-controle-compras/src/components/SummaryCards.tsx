import { moeda } from "../lib/utils";

type Props = {
  qtd: number;
  total: number;
};

export default function SummaryCards({ qtd, total }: Props) {
  return (
    <div className="grid gap-4 md:grid-cols-2">
      <div className="card p-5">
        <p className="text-sm text-slate-500">Quantidade de compras</p>
        <h2 className="mt-2 text-2xl font-semibold text-slate-900">{qtd}</h2>
      </div>

      <div className="card p-5">
        <p className="text-sm text-slate-500">Valor total</p>
        <h2 className="mt-2 text-2xl font-semibold text-slate-900">
          {moeda(total)}
        </h2>
      </div>
    </div>
  );
}