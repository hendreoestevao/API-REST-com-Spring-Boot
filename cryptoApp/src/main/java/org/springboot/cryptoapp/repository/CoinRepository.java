package org.springboot.cryptoapp.repository;

import org.springboot.cryptoapp.dto.CoinTransationDTO;
import org.springboot.cryptoapp.entity.Coin;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class CoinRepository {

    private static final String INSERT = "insert into coin (name, price, quantity, datetime) values (?, ?, ?, ?)";

    private static final String SELECT_ALL = "select name, sum(quantity) as quantity from coin group by name";

    private static final String SELECT_BY_NAME = "select * from coin where name = ?";

    private static final String UPDATE = "update coin set name = ? , price = ? , quantity = ?  where id = ?";

    private static final String DELETE = "delete from coin where id = ?";

    private final JdbcTemplate jdbcTemplate;

    public CoinRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Coin insert(Coin coin) {

        Object[] params = new Object[]{
                coin.getName(),
                coin.getPrice(),
                coin.getQuantity(),
                coin.getDateTime()
        };

        jdbcTemplate.update(INSERT, params);
        return coin;
    }

    public List<CoinTransationDTO> selectAll() {
        return jdbcTemplate.query(SELECT_ALL, new RowMapper<CoinTransationDTO>() {
            @Override
            public CoinTransationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                CoinTransationDTO coin = new CoinTransationDTO();
                coin.setName(rs.getString("name"));
                coin.setQuantity(rs.getBigDecimal("quantity"));

                return coin;
            }
        });
    }

    public List<Coin> getByName(String name) {
        Object[] params = new Object[]{name};
        return jdbcTemplate.query(SELECT_BY_NAME, new RowMapper<Coin>() {
            @Override
            public Coin mapRow(ResultSet rs, int rowNum) throws SQLException {
                Coin coin = new Coin();
                coin.setId(rs.getInt("id"));
                coin.setName(rs.getString("name"));
                coin.setPrice(rs.getBigDecimal("price"));
                coin.setQuantity(rs.getBigDecimal("quantity"));
                coin.setDateTime(rs.getTimestamp("datetime"));

                return coin;
            }
        }, params);
    }

    public Coin update(Coin coin) {
        Object[] params = new Object[]{ coin.getName(), coin.getPrice(), coin.getQuantity(), coin.getId() };
        jdbcTemplate.update(UPDATE, params);
        return coin;
    }


    public int deleteById(int id) {
        return jdbcTemplate.update(DELETE, id);
    }
}
