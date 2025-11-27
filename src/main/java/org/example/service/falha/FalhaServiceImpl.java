package org.example.service.falha;

import org.example.model.Falha;
import org.example.repositorio.EquipamentoRepositorio;
import org.example.repositorio.FalhaRepositorio;

import java.sql.SQLException;
import java.util.List;

public class FalhaServiceImpl implements FalhaService{

    FalhaRepositorio falhaRespositorio = new FalhaRepositorio();
    EquipamentoRepositorio equipamentoRepositorio = new EquipamentoRepositorio();

    @Override
    public Falha registrarNovaFalha(Falha falha) throws SQLException {

        if (!equipamentoRepositorio.EquipamentoExiste(falha.getEquipamentoId())) {
            throw new IllegalArgumentException("Equipamento não encontrado!");
        }

        falha.setStatus("ABERTA");
        falha = falhaRespositorio.registrarNovaFalha(falha);

        if (falha.getCriticidade() == "CRITICA") {
            equipamentoRepositorio.atualizarStatus(falha.getEquipamentoId(), "EM_MANUTENCAO");
        }

        return falha;
    }

    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        return falhaRespositorio.buscarFalhasCriticasAbertas();
    }
}
