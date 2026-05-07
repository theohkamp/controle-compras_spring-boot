import type {
  Compra,
  CompraRequest,
  Filters,
  ResumoResponse
} from "../types";

const API = "http://localhost:8080";

function buildQuery(filters: Record<string, string | number | undefined | null>): string {
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value !== undefined && value !== null && String(value).trim() !== "") {
      params.append(key, String(value));
    }
  });

  const query = params.toString();
  return query ? `?${query}` : "";
}

async function requestJson<T>(url: string, init?: RequestInit): Promise<T> {
  const response = await fetch(url, init);

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Erro ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export const api = {
  listar(filters: Partial<Filters> | Record<string, string>): Promise<Compra[]> {
    return requestJson<Compra[]>(`${API}/api/compras${buildQuery(filters)}`);
  },

  resumo(filters: Partial<Filters> | Record<string, string>): Promise<ResumoResponse> {
    return requestJson<ResumoResponse>(`${API}/api/compras/resumo${buildQuery(filters)}`);
  },

  criar(compra: CompraRequest): Promise<Compra> {
    return requestJson<Compra>(`${API}/api/compras`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(compra)
    });
  },

  atualizar(id: number, compra: CompraRequest): Promise<Compra> {
    return requestJson<Compra>(`${API}/api/compras/${id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(compra)
    });
  },

  async excluir(id: number): Promise<void> {
    const response = await fetch(`${API}/api/compras/${id}`, {
      method: "DELETE"
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || `Erro ${response.status}`);
    }
  },

  async pdf(filters: Partial<Filters> | Record<string, string>): Promise<Blob> {
    const response = await fetch(`${API}/api/compras/export/pdf${buildQuery(filters)}`);

    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || `Erro ${response.status}`);
    }

    return response.blob();
  },

  async pdfSelecionadas(ids: number[]): Promise<Blob> {
    const response = await fetch(`${API}/api/compras/export/pdf/selecionadas`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ ids })
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || `Erro ${response.status}`);
    }

    return response.blob();
  }
};