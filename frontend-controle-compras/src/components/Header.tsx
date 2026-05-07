type Props = {
  onNew: () => void;
};

export default function Header({ onNew }: Props) {
  return (
    <div className="card flex items-center justify-between p-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">
          Controle de Compras
        </h1>
      </div>

      <button onClick={onNew} className="btn-primary">
        Nova Compra
      </button>
    </div>
  );
}