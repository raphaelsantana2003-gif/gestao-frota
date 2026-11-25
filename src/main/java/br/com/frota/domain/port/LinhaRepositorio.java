package br.com.frota.domain.port;

import br.com.frota.domain.LinhaOnibus;
import java.util.List;
import java.util.Optional;

public interface LinhaRepositorio {
    LinhaOnibus salvar(LinhaOnibus linha);
    Optional<LinhaOnibus> buscarPorId(String id);
    List<LinhaOnibus> listarTodas();
    long contar();
}