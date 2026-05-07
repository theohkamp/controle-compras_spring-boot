package br.com.controlecompras.dao;

import br.com.controlecompras.model.Compra;
import br.com.controlecompras.service.Database;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CompraDAO {

    public Compra inserir(Compra compra) {
        String sql = "INSERT INTO compras(item, valor, data, solicitado_por, setor) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, compra.getItem());
            ps.setBigDecimal(2, compra.getValor());
            ps.setString(3, compra.getData().toString());
            ps.setString(4, compra.getSolicitadoPor());
            ps.setString(5, compra.getSetor());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    compra.setId(rs.getInt(1));
                }
            }

            return compra;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir compra.", e);
        }
    }

    public Compra atualizar(Compra compra) {
        String sql = "UPDATE compras SET item=?, valor=?, data=?, solicitado_por=?, setor=? WHERE id=?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, compra.getItem());
            ps.setBigDecimal(2, compra.getValor());
            ps.setString(3, compra.getData().toString());
            ps.setString(4, compra.getSolicitadoPor());
            ps.setString(5, compra.getSetor());
            ps.setInt(6, compra.getId());

            ps.executeUpdate();

            return compra;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar compra.", e);
        }
    }

    public void excluir(int id) {
        String sql = "DELETE FROM compras WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir compra.", e);
        }
    }

    public Optional<Compra> buscarPorId(int id) {
        String sql = "SELECT id, item, valor, data, solicitado_por, setor FROM compras WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar compra.", e);
        }
    }

    public List<Compra> buscarPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            placeholders.append("?");
            if (i < ids.size() - 1) {
                placeholders.append(",");
            }
        }

        String sql = "SELECT id, item, valor, data, solicitado_por, setor " +
                "FROM compras WHERE id IN (" + placeholders + ") ORDER BY data DESC, id DESC";

        List<Compra> lista = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 0; i < ids.size(); i++) {
                ps.setInt(i + 1, ids.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar compras por IDs.", e);
        }
    }

    public List<Compra> listarTodas() {
        String sql = "SELECT id, item, valor, data, solicitado_por, setor FROM compras ORDER BY data DESC, id DESC";
        List<Compra> lista = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }

            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar compras.", e);
        }
    }

    private Compra mapear(ResultSet rs) throws SQLException {
        return new Compra(
                rs.getInt("id"),
                rs.getString("item"),
                rs.getBigDecimal("valor"),
                LocalDate.parse(rs.getString("data")),
                rs.getString("solicitado_por"),
                rs.getString("setor")
        );
    }
}