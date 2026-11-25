package br.com.frota;

import br.com.frota.application.AbrirManutencaoUseCase;
import br.com.frota.domain.DispositivoTelemetria;
import br.com.frota.domain.Onibus;
import br.com.frota.domain.Placa;
import br.com.frota.domain.notification.MontadorNotificador;
import br.com.frota.domain.notification.NotificadorManutencao;
import br.com.frota.domain.port.EnviadorEmail;
import br.com.frota.domain.port.OnibusRepositorio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FrotaTest {

    @Mock
    private OnibusRepositorio onibusRepositorio;

    @Mock
    private EnviadorEmail enviadorEmail;

    private Onibus criarOnibus(Onibus.Status status, double temperaturaMotor) {
        DispositivoTelemetria gps = new DispositivoTelemetria("GPS-2403", "356938002403", -23.52, -46.19, temperaturaMotor, 61);
        return new Onibus("bus-2403", "2403", new Placa("MOG4D03"), "9BM384067NB002403", "Scania",
                "K280IB", 2024, 88, new BigDecimal("19800"), "DIESEL", true, true,
                status, LocalDate.now(), "L02", gps);
    }

    @Test
    void deveRejeitarPlacaInvalidaPorCalistenia() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Placa("ABC12"));
    }

    @Test
    void deveBloquearAlocacaoDeOnibusEmManutencao() {
        // Arrange
        Onibus onibus = criarOnibus(Onibus.Status.EM_MANUTENCAO, 80.0);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> onibus.alocarNaLinha("L01"));
    }

    @Test
    void deveDetectarSuperaquecimentoPeloMetodoTellDontAsk() {
        // Arrange
        Onibus onibus = criarOnibus(Onibus.Status.EM_OPERACAO, 108.5);

        // Act
        boolean superaquecendo = onibus.estaSuperaquecendo();

        // Assert
        assertTrue(superaquecendo);
    }

    @Test
    void deveEmpilharDecoratorManualComPainelEmailESmsRespeitandoLiskov() {
        // Arrange
        MontadorNotificador montador = new MontadorNotificador(enviadorEmail);
        NotificadorManutencao notificador = montador.montar(Set.of("EMAIL", "SMS"), "garagem@empresa.com", "+5511999992403");

        // Act
        List<String> avisos = notificador.notificar("2403", "MOG4D03", "Superaquecimento no motor");

        // Assert
        assertEquals(3, avisos.size());
        verify(enviadorEmail, times(1)).enviar(eq("garagem@empresa.com"), contains("2403"), anyString());
    }

    @Test
    void deveAbrirManutencaoAtualizarStatusEDispararNotificacoesComMocksDip() {
        // Arrange
        Onibus onibus = criarOnibus(Onibus.Status.EM_OPERACAO, 108.5);
        when(onibusRepositorio.buscarPorId("bus-2403")).thenReturn(Optional.of(onibus));
        AbrirManutencaoUseCase useCase = new AbrirManutencaoUseCase(onibusRepositorio, new MontadorNotificador(enviadorEmail));

        // Act
        List<String> avisos = useCase.executar("bus-2403", "Motor 108.5C", Set.of("EMAIL", "SMS"), "garagem@empresa.com", "+5511999992403");

        // Assert
        assertEquals(Onibus.Status.EM_MANUTENCAO, onibus.statusOperacional());
        assertNull(onibus.linhaId());
        assertEquals(3, avisos.size());
        verify(onibusRepositorio, times(1)).salvar(onibus);
    }
}