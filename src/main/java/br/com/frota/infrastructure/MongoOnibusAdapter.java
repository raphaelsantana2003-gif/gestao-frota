package br.com.frota.infrastructure;

import br.com.frota.domain.DispositivoTelemetria;
import br.com.frota.domain.Onibus;
import br.com.frota.domain.Placa;
import br.com.frota.domain.port.OnibusRepositorio;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MongoOnibusAdapter implements OnibusRepositorio {
    private final MongoTemplate mongoTemplate;

    public MongoOnibusAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Document(collection = "onibus")
    record OnibusDocumento(
            @Id String id, String prefixo, String placa, String chassi, String marca, String modelo,
            int anoFabricacao, int capacidadePassageiros, BigDecimal quilometragem,
            String tipoCombustivel, boolean acessibilidadeCompleta, boolean arCondicionado,
            Onibus.Status statusOperacional, LocalDate dataUltimaRevisao,
            String linhaId, DispositivoTelemetria telemetria
    ) {
        static OnibusDocumento deDominio(Onibus o) {
            return new OnibusDocumento(o.id(), o.prefixo(), o.placa().valor(), o.chassi(), o.marca(),
                    o.modelo(), o.anoFabricacao(), o.capacidadePassageiros(), o.quilometragem(),
                    o.tipoCombustivel(), o.acessibilidadeCompleta(), o.arCondicionado(),
                    o.statusOperacional(), o.dataUltimaRevisao(), o.linhaId(), o.telemetria());
        }

        Onibus paraDominio() {
            return new Onibus(id, prefixo, new Placa(placa), chassi, marca, modelo, anoFabricacao,
                    capacidadePassageiros, quilometragem, tipoCombustivel, acessibilidadeCompleta,
                    arCondicionado, statusOperacional, dataUltimaRevisao, linhaId, telemetria);
        }
    }

    @Override
    public Onibus salvar(Onibus onibus) {
        return mongoTemplate.save(OnibusDocumento.deDominio(onibus)).paraDominio();
    }

    @Override
    public Optional<Onibus> buscarPorId(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, OnibusDocumento.class))
                .map(OnibusDocumento::paraDominio);
    }

    @Override
    public List<Onibus> listarTodos() {
        return mongoTemplate.findAll(OnibusDocumento.class).stream()
                .map(OnibusDocumento::paraDominio).toList();
    }

    @Override
    public List<Onibus> listarPorLinhaId(String linhaId) {
        Query busca = Query.query(Criteria.where("linhaId").is(linhaId));
        return mongoTemplate.find(busca, OnibusDocumento.class).stream()
                .map(OnibusDocumento::paraDominio).toList();
    }

    @Override
    public long contar() {
        return mongoTemplate.count(new Query(), OnibusDocumento.class);
    }
}