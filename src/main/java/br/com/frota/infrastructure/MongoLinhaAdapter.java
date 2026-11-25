package br.com.frota.infrastructure;

import br.com.frota.domain.LinhaOnibus;
import br.com.frota.domain.port.LinhaRepositorio;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class MongoLinhaAdapter implements LinhaRepositorio {
    private final MongoTemplate mongoTemplate;

    public MongoLinhaAdapter(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Document(collection = "linhas_onibus")
    record LinhaDocumento(
            @Id String id, String codigo, String nome, String origem, String destino, BigDecimal tarifa
    ) {
        static LinhaDocumento deDominio(LinhaOnibus l) {
            return new LinhaDocumento(l.id(), l.codigo(), l.nome(), l.origem(), l.destino(), l.tarifa());
        }

        LinhaOnibus paraDominio() {
            return new LinhaOnibus(id, codigo, nome, origem, destino, tarifa);
        }
    }

    @Override
    public LinhaOnibus salvar(LinhaOnibus linha) {
        return mongoTemplate.save(LinhaDocumento.deDominio(linha)).paraDominio();
    }

    @Override
    public Optional<LinhaOnibus> buscarPorId(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, LinhaDocumento.class))
                .map(LinhaDocumento::paraDominio);
    }

    @Override
    public List<LinhaOnibus> listarTodas() {
        return mongoTemplate.findAll(LinhaDocumento.class).stream()
                .map(LinhaDocumento::paraDominio).toList();
    }

    @Override
    public long contar() {
        return mongoTemplate.count(new Query(), LinhaDocumento.class);
    }
}