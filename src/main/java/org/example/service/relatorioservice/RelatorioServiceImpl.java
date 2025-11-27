package org.example.service.relatorioservice;

import org.example.dto.EquipamentoContagemFalhasDTO;
import org.example.dto.FalhaDetalhadaDTO;
import org.example.dto.RelatorioParadaDTO;
import org.example.model.AcaoCorretiva;
import org.example.model.Equipamento;
import org.example.model.Falha;
import org.example.repositorio.AcaoCorretivaRepositorio;
import org.example.repositorio.EquipamentoRepositorio;
import org.example.repositorio.FalhaRepositorio;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RelatorioServiceImpl implements RelatorioService{

    AcaoCorretivaRepositorio acaoCorretivaRepositorio = new AcaoCorretivaRepositorio();
    EquipamentoRepositorio equipamentoRepositorio = new EquipamentoRepositorio();
    FalhaRepositorio falhaRepositorio = new FalhaRepositorio();

    @Override
    public List<RelatorioParadaDTO> gerarRelatorioTempoParada() throws SQLException {
        return falhaRepositorio.gerarRelatorioTempooParada();
    }

    @Override
    public List<Equipamento> buscarEquipamentosSemFalhasPorPeriodo(LocalDate dataInicio, LocalDate datafim) throws SQLException {
        return equipamentoRepositorio.buscarEquipamentoSemFalhasPorPeriodo(dataInicio,datafim);
    }

    @Override
    public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId) throws SQLException {
        Falha falha = falhaRepositorio.buscarFalhaPorId(falhaId);

        if (falha == null) {
            throw new RuntimeException();
        }
        return falhaRepositorio.buscarDetalhesCompletosFalha(falhaId);
    }

    @Override
    public List<EquipamentoContagemFalhasDTO> gerarRelatorioManutencaoPreventiva(int contagemMinimaFalhas) throws SQLException {

        if (contagemMinimaFalhas < 1) {
            throw new RuntimeException();
        }
        return equipamentoRepositorio.gerarRelatorioManutencaoPreventiva(contagemMinimaFalhas);
    }

}
