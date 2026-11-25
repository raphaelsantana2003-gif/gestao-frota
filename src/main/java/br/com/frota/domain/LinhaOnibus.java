package br.com.frota.domain;

import java.math.BigDecimal;

public record LinhaOnibus(
        String id,
        String codigo,
        String nome,
        String origem,
        String destino,
        BigDecimal tarifa
) {}