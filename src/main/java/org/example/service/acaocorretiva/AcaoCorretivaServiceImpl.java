package org.example.service.acaocorretiva;

import org.example.model.AcaoCorretiva;
import org.example.model.Falha;
import org.example.repositorio.AcaoCorretivaRepositorio;
import org.example.repositorio.EquipamentoRepositorio;
import org.example.repositorio.FalhaRepositorio;

import java.sql.SQLException;

public class AcaoCorretivaServiceImpl implements AcaoCorretivaService{

    FalhaRepositorio falhaRepositorio = new FalhaRepositorio();
    EquipamentoRepositorio equipamentoRepositorio = new EquipamentoRepositorio();
    AcaoCorretivaRepositorio acaoCorretivaRepositorio = new AcaoCorretivaRepositorio();

    @Override
    public AcaoCorretiva registrarConclusaoDeAcao(AcaoCorretiva acao) throws SQLException {

        Falha falha = falhaRepositorio.buscarFalhaPorId(acao.getFalhaId());

        if (falha == null) {
            throw new RuntimeException("Falha não encontrada!");
        }

        acaoCorretivaRepositorio.registrarConclusaoDeAcao(acao);

        // Regras de Negocio
        falhaRepositorio.atualizarStatus(acao.getFalhaId(), "RESOLVIDA");

        if (falha.getCriticidade().equals("CRITICA")) {
            equipamentoRepositorio.atualizarStatus(falha.getEquipamentoId(), "OPERACIONAL");
        }

        return acao;
    }
}
