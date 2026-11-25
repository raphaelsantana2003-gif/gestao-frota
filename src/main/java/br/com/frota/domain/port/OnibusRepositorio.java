package br.com.frota.domain.port;

import br.com.frota.domain.Onibus;
import java.util.List;
import java.util.Optional;

public interface OnibusRepositorio {
    Onibus salvar(Onibus onibus);
    Optional<Onibus> buscarPorId(String id);
    List<Onibus> listarTodos();
    List<Onibus> listarPorLinhaId(String linhaId);
    long contar();
}