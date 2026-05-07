package dao;

import model.entity.BorrowDetail;
import model.enums.Category;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BorrowDetailDao {
    public static BorrowDetail mapToBorrowDetail(ResultSet resultSet) throws SQLException {
        return new BorrowDetail(
                resultSet.getInt("id"),
                resultSet.getInt("borrow_id"),
                resultSet.getInt("book_id"),
                resultSet.getInt("quantity"),
                resultSet.getString("borrowed_book_code"),
                resultSet.getString("borrowed_book_name"),
                resultSet.getString("borrowed_author"),
                resultSet.getInt("borrowed_publication_year"),
                MapperUtil.stringToEnum(resultSet.getString("borrowed_category"), Category.class),
                resultSet.getString("borrowed_image"));
    }

    public int countBorrowDetailsByBorrowId(Connection connection,int borrowId) throws SQLException {
        return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM borrow_details WHERE borrow_id = ? ",borrowId);
    }
    public List<BorrowDetail> getBorrowDetailsByBorrowId(Connection connection,int borrowId, int pageIndex,
                                                         int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM borrow_details WHERE borrow_id = ? LIMIT ?, ?",
                BorrowDetailDao::mapToBorrowDetail, borrowId, (pageIndex - 1)* pageSize,pageSize);
    }

    public void addBorrowDetail(Connection connection,BorrowDetail borrowDetail) throws SQLException {
        QueryDao.add(connection,"INSERT INTO borrow_details (borrow_id, book_id, quantity,borrowed_book_code, " +
                        "borrowed_book_name, borrowed_author, borrowed_publication_year, borrowed_category, borrowed_image) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                borrowDetail.getBorrowId(),borrowDetail.getBookId(),borrowDetail.getQuantity(),borrowDetail.getBorrowedBookCode(),
                borrowDetail.getBorrowedBookName(), borrowDetail.getBorrowedAuthor(),borrowDetail.getBorrowedPublicationYear(),
                MapperUtil.enumToString(borrowDetail.getBorrowedCategory()), borrowDetail.getBorrowedImage());
    }
}
