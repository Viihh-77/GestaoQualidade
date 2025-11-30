package org.example.service.falha;

import org.example.model.Equipamento;
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

        boolean equipamento = equipamentoRepositorio.EquipamentoExiste(falha.getEquipamentoId());

        if (equipamento == false) {
            throw new IllegalArgumentException("Equipamento não encontrado!");
        }

        falha.setStatus("ABERTA");
        falhaRespositorio.registrarNovaFalha(falha);

        if (falha.getCriticidade().equals("CRITICA")) {
            equipamentoRepositorio.atualizarStatus(falha.getEquipamentoId(), "EM_MANUTENCAO");
        }


    }

    @Override
    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {
        return falhaRespositorio.buscarFalhasCriticasAbertas();
    }
}
