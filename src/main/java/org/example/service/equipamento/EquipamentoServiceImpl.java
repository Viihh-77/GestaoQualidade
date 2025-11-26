package org.example.service.equipamento;

import org.example.model.Equipamento;
import org.example.repositorio.EquipamentoRepositorio;

import java.sql.SQLException;

public class EquipamentoServiceImpl implements EquipamentoService{

    EquipamentoRepositorio repositorio = new EquipamentoRepositorio();

    @Override
    public Equipamento criarEquipamento(Equipamento equipamento) throws SQLException {

        equipamento.setStatusOperacional("OPERACIONAL");
        equipamento = repositorio.criarEquipamento(equipamento);

        if (equipamento.getId() == null) {
            System.out.println("Erro: ID não existe!");
        }

        return equipamento;
    }

    @Override
    public Equipamento buscarEquipamentoPorId(Long id) throws SQLException {

        Equipamento equipamento = repositorio.buscarEquipamentoPorId(id);

        if (equipamento == null) {
            throw new RuntimeException("Equipamento não encontrado!");
        }

        return equipamento;
    }
}
