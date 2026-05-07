export type Compra = {
    id: number;
    item: string;
    valor: number;
    data: string;
    solicitadoPor: string;
    setor: string;
  };
  
  export type CompraRequest = {
    item: string;
    valor: number;
    data: string;
    solicitadoPor: string;
    setor: string;
  };
  
  export type ResumoResponse = {
    quantidade: number;
    total: number;
  };
  
  export type Filters = {
    dataInicio: string;
    dataFim: string;
    valorMin: string;
    valorMax: string;
    setor: string;
    solicitante: string;
  };