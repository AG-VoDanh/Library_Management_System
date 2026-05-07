package dao;

import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class DbHelper {

    @FunctionalInterface
    public interface TransactionAction<T> {
        T execute(Connection connection) throws Exception;
    }

    public static <T> T runInTransaction(TransactionAction<T> action) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            T result = action.execute(connection);

            connection.commit();
            return result;

        } catch (RuntimeException re) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException _) {

                }
            }
            throw re;
        }catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException _) {

                }
            }
            throw new RuntimeException("Lỗi Database Transaction: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException _) {

                }
            }
        }
    }
}
