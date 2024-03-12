package ru.avito.priceservice.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatrixDao {

    private static final String MATRIX_SQL = "SELECT price from %s where = microcategory_id = ? and location_id = ?";
    private static final String DISCOUNT_SQL = "SELECT price from %s where = microcategory_id = ? and location_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public Optional<Long> findPriceByMatrix(String matrixId, Long microCategory, Long location) {
        return jdbcTemplate.query(String.format(MATRIX_SQL, matrixId),
                (rs, numRows) -> rs.getLong("price"),
                microCategory, location).stream().findFirst();
    }

    public Optional<Long> findPriceByDiscount(String matrixId, Long microCategory, Long location) {
        return jdbcTemplate.query(String.format(DISCOUNT_SQL, matrixId),
                (rs, numRows) -> rs.getLong("price"),
                microCategory, location).stream().findFirst();
    }
}
