package br.com.frota.infrastructure;

import br.com.frota.application.AbrirManutencaoUseCase;
import br.com.frota.domain.*;
import br.com.frota.domain.notification.MontadorNotificador;
import br.com.frota.domain.port.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class ConfiguracaoApp {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfiguracaoApp.class);

    @Bean
    public EnviadorEmail enviadorEmail() {
        return (destinatario, assunto, corpo) ->
                LOGGER.info("[EMAIL DISPARADO] Para: {} | Assunto: {} | {}", destinatario, assunto, corpo);
    }

    @Bean
    public MontadorNotificador montadorNotificador(EnviadorEmail enviadorEmail) {
        return new MontadorNotificador(enviadorEmail);
    }

    @Bean
    public AbrirManutencaoUseCase abrirManutencaoUseCase(OnibusRepositorio onibusRepositorio,
                                                         MontadorNotificador montadorNotificador) {
        return new AbrirManutencaoUseCase(onibusRepositorio, montadorNotificador);
    }

    @Bean
    public CommandLineRunner dataSeeder(LinhaRepositorio linhaRepo, OnibusRepositorio onibusRepo) {
        return args -> {
            if (linhaRepo.contar() == 0) {
                linhaRepo.salvar(new LinhaOnibus("L01", "E103", "Terminal Central - Estudantes", "Terminal Central", "Terminal Estudantes", new BigDecimal("5.00")));
                linhaRepo.salvar(new LinhaOnibus("L02", "C207", "Jundiapeba - Centro", "Jundiapeba", "Terminal Central", new BigDecimal("5.00")));
            }
            if (onibusRepo.contar() > 0) {
                return;
            }

            onibusRepo.salvar(new Onibus(
                    "bus-2400", "2400", new Placa("MOG2A00"), "9BM384067NB002400", "Mercedes-Benz", "OF-1721 Torino",
                    2022, 82, new BigDecimal("42000"), "DIESEL", true, true, Onibus.Status.EM_OPERACAO,
                    LocalDate.of(2026, 10, 10), "L01",
                    new DispositivoTelemetria("GPS-2400", "356938002400", -23.5220, -46.1880, 83.0, 88)
            ));
            onibusRepo.salvar(new Onibus(
                    "bus-2401", "2401", new Placa("BRA2B01"), "9BM384067NB002401", "Volvo", "B270F Apache Vip",
                    2023, 86, new BigDecimal("28500"), "HIBRIDO", true, true, Onibus.Status.EM_OPERACAO,
                    LocalDate.of(2026, 10, 12), "L01",
                    new DispositivoTelemetria("GPS-2401", "356938002401", -23.5190, -46.1840, 85.5, 76)
            ));
            onibusRepo.salvar(new Onibus(
                    "bus-2402", "2402", new Placa("SPX3C02"), "9BM384067NB002402", "BYD", "D9W Elétrico",
                    2024, 80, new BigDecimal("14300"), "ELETRICO", true, true, Onibus.Status.EM_MANUTENCAO,
                    LocalDate.of(2026, 10, 15), null,
                    new DispositivoTelemetria("GPS-2402", "356938002402", -23.5150, -46.1810, 78.0, 92)
            ));
            onibusRepo.salvar(new Onibus(
                    "bus-2403", "2403", new Placa("MOG4D03"), "9BM384067NB002403", "Scania", "K280IB GranVia",
                    2024, 88, new BigDecimal("19800"), "DIESEL", true, true, Onibus.Status.EM_OPERACAO,
                    LocalDate.of(2026, 10, 18), "L02",
                    new DispositivoTelemetria("GPS-2403", "356938002403", -23.5245, -46.1910, 108.5, 61)
            ));

            LOGGER.info(">>> DataSeeder carregou 2 linhas e 4 ônibus (2400 a 2403) no MongoDB!");
        };
    }
}