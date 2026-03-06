package org.springboot.cryptoapp.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
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

    private final EntityManager entityManager;

    public CoinRepository(EntityManager entityManager, JdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Coin insertManual(Coin coin) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(coin);
        transaction.commit();
        return coin;
    }

    //OU

    @Transactional
    public Coin insert(Coin coin) {
        entityManager.persist(coin);
        return coin;
    }

    public Coin insertJDBC(Coin coin) {
        Object[] params = new Object[]{
                coin.getName(),
                coin.getPrice(),
                coin.getQuantity(),
                coin.getDateTime()
        };

        jdbcTemplate.update(INSERT, params);
        return coin;
    }

    public Coin updateJDBC(Coin coin) {
        Object[] params = new Object[]{coin.getName(), coin.getPrice(), coin.getQuantity(), coin.getId()};
        jdbcTemplate.update(UPDATE, params);
        return coin;
    }

    @Transactional
    public Coin update(Coin coin) {
        entityManager.merge(coin);
        return coin;
    }


    public List<CoinTransationDTO> selectAllJDBC() {
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

    public List<CoinTransationDTO> selectAll() {
        String jpql = "select new org.springboot.cryptoapp.dto.CoinTransationDTO(c.name, sum(c.quantity)) from Coin c group by c.name";
        TypedQuery<CoinTransationDTO> query = entityManager.createQuery(jpql, CoinTransationDTO.class);
        return query.getResultList();
    }


    public List<Coin> getByNameJDBC(String name) {
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

    public List<Coin> getByName(String name) {
        String jpql = "select c from Coin c where c.name like :name";
        TypedQuery<Coin> query = entityManager.createQuery(jpql, Coin.class);
        query.setParameter("name", "%" + name + "%");

        return query.getResultList();
    }


    public int deleteByIdJDBC(int id) {
        return jdbcTemplate.update(DELETE, id);
    }

    @Transactional
    public boolean deleteById(int id) {
        Coin coin = entityManager.find(Coin.class, id);

        if (coin == null) {
            throw new RuntimeException();
        }
        entityManager.remove(coin);
        return true;
    }
}
