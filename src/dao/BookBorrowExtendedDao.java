package dao;

import model.entity.BookBorrow;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookBorrowExtendedDao {
    public int countBookBorrowsByBookCode(Connection connection, String bookCode) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_borrows JOIN borrow_details ON " +
                "book_borrows.id = borrow_details.borrow_id WHERE borrowed_book_code LIKE ? ", "%"+bookCode+"%");
    }
    public List<BookBorrow> getBookBorrowsByBookCode(Connection connection,String bookCode,int pageIndex,
                                                     int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT book_borrows.* FROM book_borrows JOIN borrow_details ON book_borrows.id = " +
                "borrow_details.borrow_id WHERE borrowed_book_code LIKE ? LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow,"%"+bookCode+"%", (pageIndex - 1) * pageSize, pageSize);
    }
    public int countBookBorrowsByBookName(Connection connection,String bookName) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_borrows JOIN borrow_details ON " +
                "book_borrows.id = borrow_details.borrow_id WHERE borrowed_book_name LIKE ? ", "%"+bookName+"%");
    }
    public List<BookBorrow> getBookBorrowsByBookName(Connection connection,String bookName,int pageIndex,
                                                     int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT book_borrows.* FROM book_borrows JOIN borrow_details ON " +
                        "book_borrows.id = borrow_details.borrow_id WHERE borrowed_book_name LIKE ? LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow,"%"+bookName+"%", (pageIndex - 1) * pageSize, pageSize);
    }

    public int countMonthlyBorrowedBook(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT SUM(quantity) FROM borrow_details JOIN book_borrows ON " +
                "borrow_details.borrow_id = book_borrows.id WHERE MONTH(book_borrows.borrow_date) " +
                "= MONTH(now()) AND YEAR(book_borrows.borrow_date) = YEAR(NOW());");
    }

    public int countMonthlyBorrow(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(id) FROM book_borrows WHERE MONTH(" +
                "book_borrows.borrow_date) = MONTH(now()) AND YEAR(book_borrows.borrow_date) = YEAR(NOW()); ");
    }

    public int countOverdueBorrow(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(id) FROM book_borrows WHERE due_date < " +
                "CURDATE() AND return_date IS NULL");
    }

    public List<String> getTopBorrowedBooksThisMonth(Connection connection) throws SQLException {
        return QueryDao.queryForList(connection,"WITH ranked_books AS ( SELECT bd.book_id, bd.borrowed_book_code, " +
                "bd.borrowed_book_name, SUM(bd.quantity) AS total_borrowed, RANK() OVER (ORDER BY SUM(bd.quantity) DESC) AS " +
                "rnk FROM borrow_details bd JOIN book_borrows bb ON bd.borrow_id = bb.id WHERE MONTH(bb.borrow_date) = " +
                "MONTH(CURRENT_DATE()) AND YEAR(bb.borrow_date) = YEAR(CURRENT_DATE()) GROUP BY bd.book_id, " +
                "bd.borrowed_book_code, bd.borrowed_book_name ) SELECT * FROM ranked_books WHERE rnk = 1;",
                rs -> rs.getString("borrowed_book_code") + " - " +
                        rs.getString("borrowed_book_name"));
    }

    public List<String> getTopBorrowingMembersThisMonth(Connection connection) throws SQLException {
        return QueryDao.queryForList(connection,"WITH ranked_members AS ( SELECT bb.member_id, bb.borrower_code, bb.borrower_name, " +
                "COUNT(*) AS total_borrows, RANK() OVER (ORDER BY COUNT(*) DESC) AS rank_position FROM book_borrows bb " +
                "WHERE MONTH(bb.borrow_date) = MONTH(CURRENT_DATE()) AND YEAR(bb.borrow_date) = YEAR(CURRENT_DATE()) " +
                "GROUP BY bb.member_id, bb.borrower_code, bb.borrower_name ) SELECT * FROM ranked_members WHERE " +
                "rank_position = 1;",rs -> rs.getString("borrower_code") + " - " +
                        rs.getString("borrower_name"));
    }
}
