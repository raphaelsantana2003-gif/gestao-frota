package br.com.frota.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FrotaAlocada {
    private final List<Onibus> veiculos = new ArrayList<>();

    public void adicionar(Onibus onibus) {
        if (onibus == null) {
            throw new IllegalArgumentException("Ônibus não pode ser nulo.");
        }
        veiculos.add(onibus);
    }

    public long contarEmOperacao() {
        return veiculos.stream().filter(Onibus::estaEmOperacao).count();
    }

    public List<Onibus> listar() {
        return Collections.unmodifiableList(veiculos);
    }
}