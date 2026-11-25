package br.com.frota.domain;

public record DispositivoTelemetria(
        String codigoSerial,
        String identificadorImei,
        double latitude,
        double longitude,
        double temperaturaMotor,
        int percentualCombustivel
) {
    private static final double LIMITE_SUPERAQUECIMENTO = 100.0;

    public boolean temperaturaAcimaDoLimite() {
        return temperaturaMotor >= LIMITE_SUPERAQUECIMENTO;
    }
}