package org.example.repositorio;

import org.example.database.Conexao;
import org.example.dto.FalhaDetalhadaDTO;
import org.example.dto.RelatorioParadaDTO;
import org.example.model.Equipamento;
import org.example.model.Falha;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FalhaRepositorio {

    public Falha registrarNovaFalha(Falha falha) throws SQLException {

        String query = """
                INSERT INTO Falha
                (equipamentoId,dataHoraOcorrencia,descricao,criticidade,status,tempoParadaHoras)
                VALUES
                (?,?,?,?,?,?)
                """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, falha.getEquipamentoId());
            stmt.setTimestamp(2, Timestamp.valueOf(falha.getDataHoraOcorrencia()));
            stmt.setString(3, falha.getDescricao());
            stmt.setString(4, falha.getCriticidade());
            stmt.setString(5, falha.getStatus());
            stmt.setBigDecimal(6, falha.getTempoParadaHoras());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                falha.setId(rs.getLong(1));
                return falha;
            }
        }
        return falha;
    }

    public List<Falha> buscarFalhasCriticasAbertas() throws SQLException {

        List<Falha> falhas = new ArrayList<>();
        String query = """
                SELECT  id
                       ,equipamentoId
                       ,dataHoraOcorrencia
                       ,descricao
                       ,criticidade
                       ,status
                       ,tempoParadaHoras
                FROM Falha
                WHERE criticidade = 'CRITICA'
                AND status = 'ABERTA'
                """;

        try (Connection conn  = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)) {

        ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime();
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                falhas.add(new Falha(
                   id,
                   equipamentoId,
                   dataHoraOcorrencia,
                   descricao,
                   criticidade,
                   status,
                   tempoParadaHoras
                ));
            }
        }
        return falhas;
    }

    public Falha buscarFalhaPorId (Long id) throws SQLException {

        String query = """
                SELECT  id
                       ,equipamentoId
                       ,dataHoraOcorrencia
                       ,descricao
                       ,criticidade
                       ,status
                       ,tempoParadaHoras
                FROM Falha
                WHERE id = ?
                """;

        try (Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long equipamentoId = rs.getLong("equipamentoId");
                LocalDateTime dataHoraOcorrencia = rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime();
                String descricao = rs.getString("descricao");
                String criticidade = rs.getString("criticidade");
                String status = rs.getString("status");
                BigDecimal tempoParadaHoras = rs.getBigDecimal("tempoParadaHoras");

                return new Falha(
                        id,
                        equipamentoId,
                        dataHoraOcorrencia,
                        descricao,
                        criticidade,
                        status,
                        tempoParadaHoras
                );
            }
        }
        return null;
    }

    public void atualizarStatus(Long id, String status) throws SQLException {

        String query = """
                UPDATE Falha
                SET Status = ?
                WHERE id = ?
                """;

        try (Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }
        public List<RelatorioParadaDTO> gerarRelatorioTempooParada() throws SQLException {

        List<RelatorioParadaDTO> relatorioParada = new ArrayList<>();
        String query = """
                SELECT F.equipamentoId, E.nome, F.tempoParadaHoras
                FROM Falha F
                JOIN Equipamento E ON F.equipamentoId = E.id;
                """;

            try (Connection conn = Conexao.conectar();
            PreparedStatement stmt = conn.prepareStatement(query)) {

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Long equipamentoId = rs.getLong("equipamentoId");
                    String nome = rs.getString("nome");
                    double tempoParadaHoras = rs.getDouble("tempoParadaHoras");

                    relatorioParada.add( new RelatorioParadaDTO(
                            equipamentoId,
                            nome,
                            tempoParadaHoras
                    ));
                }
            }
            return relatorioParada;
        }

        public Optional<FalhaDetalhadaDTO> buscarDetalhesCompletosFalha(long falhaId) throws SQLException {

        String query = """
                SELECT  F.id AS falha_id,
                        F.equipamentoId AS falha_equipamentoId,
                        F.dataHoraOcorrencia,
                        F.descricao,
                        F.criticidade,
                        F.status AS falha_status,
                        F.tempoParadaHoras,
                    
                        E.id AS equipamento_id,
                        E.nome AS equipamento_nome,
                        E.numeroDeSerie AS equipamento_numeroDeSerie,
                        E.areaSetor AS equipamento_areaSetor,
                        E.statusOperacional AS equipamento_statusOperacional
                FROM Falha F
                JOIN Equipamento E ON F.equipamentoId = E.id
                WHERE F.id = ?;
                """;

        try (Connection conn = Conexao.conectar();
        PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, falhaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Falha falha = new Falha(
                        rs.getLong("falha_id"),
                        rs.getLong("falha_equipamentoId"),
                        rs.getTimestamp("dataHoraOcorrencia").toLocalDateTime(),
                        rs.getString("descricao"),
                        rs.getString("criticidade"),
                        rs.getString("falha_status"),
                        rs.getBigDecimal("tempoParadaHoras")
                );

                Equipamento equipamento = new Equipamento(
                        rs.getLong("equipamento_id"),
                        rs.getString("equipamento_nome"),
                        rs.getString("equipamento_numeroDeSerie"),
                        rs.getString("equipamento_areaSetor"),
                        rs.getString("equipamento_statusOperacional")
                );

                return Optional.of(new FalhaDetalhadaDTO(falha, equipamento));
            }

        }
            return Optional.empty();
    }

}
