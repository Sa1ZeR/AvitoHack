package ru.avito.priceservice.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatrixDao {

    private static final String MATRIX_SQL = "SELECT price from %s where microcategory_id = ? and location_id = ?";
    private static final String MICRO_CATEGORY_IDS_SQL = "SELECT DISTINCT(microcategory_id) from %s";
    private static final String LOCATION_IDS_SQL = "SELECT DISTINCT(location_id) from %s";
    private static final String EXISTS_ROOT_CATEGORY_SQL = "SELECT EXISTS (SELECT * FROM %s WHERE microcategory_id = 1);";
    private static final String EXISTS_ROOT_LOCATION_SQL = "SELECT EXISTS (SELECT * FROM %s WHERE location_id = 1);";
    private static final String EXISTS_ROOT_SQL = "SELECT EXISTS (SELECT * FROM %s WHERE microcategory_id = 1 AND location_id = 1);";

    private final JdbcTemplate jdbcTemplate;

    public Optional<Long> findPriceByMatrix(String matrixTableName, Long microCategory, Long location) {
        return jdbcTemplate.query(String.format(MATRIX_SQL, matrixTableName),
                (rs, numRows) -> rs.getLong("price"),
                microCategory, location).stream().findFirst();
    }

    public List<Long> findDistinctCategoryIds(String matrixTableName) {
        return jdbcTemplate.query(String.format(MICRO_CATEGORY_IDS_SQL, matrixTableName),
                (rs, numRows) -> rs.getLong("microcategory_id"));
    }

    public List<Long> findDistinctLocationIds(String matrixTableName) {
        return jdbcTemplate.query(String.format(LOCATION_IDS_SQL, matrixTableName),
                (rs, numRows) -> rs.getLong("location_id"));
    }

    public boolean existsMatrixMicroCategoryRoot(String matrixTableName) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                String.format(EXISTS_ROOT_CATEGORY_SQL, matrixTableName),
                Boolean.class));
    }

    public boolean existsMatrixLocationRoot(String matrixTableName) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                String.format(EXISTS_ROOT_LOCATION_SQL, matrixTableName),
                Boolean.class));
    }

    public boolean existsMatrixRoot(String matrixTableName) {
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                String.format(EXISTS_ROOT_SQL, matrixTableName),
                Boolean.class));
    }
}
