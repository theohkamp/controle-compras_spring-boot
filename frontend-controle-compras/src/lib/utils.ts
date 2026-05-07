export function moeda(v: number): string {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
  }).format(v);
}

export function formatarData(data: string): string {
  if (!data) return "";
  const [ano, mes, dia] = data.split("-");
  return `${dia}/${mes}/${ano}`;
}

export function formatarDataDigitacao(valor: string): string {
  const numeros = valor.replace(/\D/g, "").slice(0, 8);

  if (numeros.length <= 2) return numeros;
  if (numeros.length <= 4) return `${numeros.slice(0, 2)}/${numeros.slice(2)}`;
  return `${numeros.slice(0, 2)}/${numeros.slice(2, 4)}/${numeros.slice(4)}`;
}

export function dataBrParaIso(valor: string): string {
  const limpo = valor.trim();
  const match = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(limpo);

  if (!match) return "";

  const [, dia, mes, ano] = match;
  return `${ano}-${mes}-${dia}`;
}

export function dataIsoParaBr(valor: string): string {
  if (!valor) return "";
  const [ano, mes, dia] = valor.split("-");
  return `${dia}/${mes}/${ano}`;
}

export function baixarArquivo(blob: Blob, nome: string): void {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = nome;
  a.click();
  window.URL.revokeObjectURL(url);
}