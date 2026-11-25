package br.com.frota.domain;

import java.util.regex.Pattern;

public record Placa(String valor) {
    private static final Pattern PADRAO_BRASIL = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    public Placa {
        if (valor == null) {
            throw new IllegalArgumentException("A placa é obrigatória.");
        }
        String placaNormalizada = valor.trim().toUpperCase().replace("-", "");
        if (!PADRAO_BRASIL.matcher(placaNormalizada).matches()) {
            throw new IllegalArgumentException("Placa inválida (use padrão antigo ou Mercosul): " + valor);
        }
        valor = placaNormalizada;
    }
}