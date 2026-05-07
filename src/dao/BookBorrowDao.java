package dao;

import model.entity.BookBorrow;
import model.enums.Address;
import model.enums.Gender;
import model.search.BorrowSearchCriteria;
import model.search.SearchType;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookBorrowDao {
    public static BookBorrow mapToBookBorrow(ResultSet resultSet) throws SQLException {
        return new BookBorrow(
                resultSet.getInt("id"),
                resultSet.getString("borrow_code"),
                resultSet.getInt("member_id"),
                resultSet.getObject("borrow_date", LocalDate.class),
                resultSet.getObject("due_date", LocalDate.class),
                resultSet.getObject("return_date", LocalDate.class),
                resultSet.getInt("late_fee"),
                resultSet.getString("borrower_code"),
                resultSet.getString("borrower_name"),
                resultSet.getInt("borrower_age"),
                MapperUtil.stringToEnum(resultSet.getString("borrower_gender"), Gender.class),
                MapperUtil.stringToEnum(resultSet.getString("borrower_address"), Address.class),
                resultSet.getString("borrower_phone_number"));
    }

    public int countAllBookBorrows(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_borrows");
    }
    public List<BookBorrow> getAllBookBorrows(Connection connection,int pageIndex, int pageSize)
            throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM book_borrows LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow, (pageIndex -1) * pageSize, pageSize);
    }

    public int countBookBorrowsByCriteria(Connection connection,BorrowSearchCriteria borrowSearchCriteria,
                                          Object... keyword) throws SQLException {
        if(borrowSearchCriteria.getSearchType() == SearchType.BETWEEN){
            return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM book_borrows WHERE "+
                            borrowSearchCriteria.getColumnName()+" BETWEEN ? AND ? ", keyword[0],keyword[1]);
        }
        return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM book_borrows WHERE "+
                        borrowSearchCriteria.getColumnName()+" "+ borrowSearchCriteria.getSearchType().getOperator()+" ? ",
                borrowSearchCriteria.getSearchType().format(keyword[0].toString()));
    }
    public List<BookBorrow> getBookBorrowsByCriteria(Connection connection,
                                                     BorrowSearchCriteria borrowSearchCriteria, int pageIndex, int pageSize,
                                                     Object... keyword) throws SQLException {
        if(borrowSearchCriteria.getSearchType() == SearchType.BETWEEN){
            return QueryDao.queryForList(connection, "SELECT * FROM book_borrows WHERE "+
                            borrowSearchCriteria.getColumnName()+" BETWEEN ? AND ? LIMIT ?, ?",
                    BookBorrowDao::mapToBookBorrow,keyword[0],keyword[1],(pageIndex - 1)* pageSize,pageSize);
        }
        return QueryDao.queryForList(connection, "SELECT * FROM book_borrows WHERE "+
                        borrowSearchCriteria.getColumnName()+" "+ borrowSearchCriteria.getSearchType().getOperator()+
                        " ? LIMIT ?, ?", BookBorrowDao::mapToBookBorrow,
                borrowSearchCriteria.getSearchType().format(keyword[0].toString()),
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int countBookBorrowsByMemberId(Connection connection,int memberId) throws SQLException {
        return QueryDao.queryForCount(connection,
                "SELECT COUNT(*) FROM book_borrows WHERE member_id = ? AND return_date IS NULL", memberId);
    }
    public List<BookBorrow> getBookBorrowsByMemberId(Connection connection,int memberId,int pageIndex,
                                                     int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM book_borrows WHERE member_id =  ? LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow,memberId,(pageIndex -1) * pageSize, pageSize);
    }

    public int countActiveBookBorrow(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_borrows WHERE return_date IS NULL");
    }
    public List<BookBorrow> findActiveBookBorrow(Connection connection, int pageIndex, int pageSize)
            throws SQLException {
        return QueryDao.queryForList(connection,
                "SELECT * FROM book_borrows WHERE return_date IS NULL LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow, pageIndex, pageSize);
    }

    public int countOverdueBookBorrows(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,
                "SELECT COUNT(*) FROM book_borrows WHERE due_date < return_date");
    }
    public List<BookBorrow> findOverdueBookBorrows(Connection connection,int pageIndex, int pageSize)
            throws SQLException {
        return QueryDao.queryForList(connection,
                "SELECT * FROM book_borrows WHERE due_date < return_date LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow, pageIndex, pageSize);
    }

    public int countOnTimeBookBorrows(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_borrows WHERE due_date >= return_date");
    }
    public List<BookBorrow> findOnTimeBookBorrows(Connection connection, int pageIndex, int pageSize)
            throws SQLException {
        return QueryDao.queryForList(connection,
                "SELECT * FROM book_borrows WHERE due_date >= return_date LIMIT ?, ?",
                BookBorrowDao::mapToBookBorrow, pageIndex, pageSize);
    }

    public int addBookBorrow(Connection connection,BookBorrow bookBorrow) throws SQLException {
        int bookBorrowId = QueryDao.add(connection,"INSERT INTO book_borrows (member_id, borrow_date, due_date, return_date, " +
                        "late_fee, borrower_code, borrower_name, borrower_age, borrower_gender, borrower_address, " +
                        "borrower_phone_number) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                bookBorrow.getMemberId(),bookBorrow.getBorrowDate(),bookBorrow.getDueDate(),
                bookBorrow.getReturnDate(),bookBorrow.getLateFee(),bookBorrow.getBorrowerCode(),bookBorrow.getBorrowerName(),
                bookBorrow.getBorrowerAge(), MapperUtil.enumToString(bookBorrow.getBorrowerGender()),
                MapperUtil.enumToString(bookBorrow.getBorrowerAddress()),
                bookBorrow.getBorrowerPhoneNumber());
        QueryDao.update(connection, "UPDATE book_borrows SET borrow_code = ? WHERE id = ?",
                String.format("BR_%05d", bookBorrowId), bookBorrowId);
        return bookBorrowId;
    }

    public void updateBooKBorrow(Connection connection,BookBorrow bookBorrow) throws SQLException {
        QueryDao.update(connection,"UPDATE book_borrows SET member_id = ?, borrow_date = ?, due_date = ?, " +
                        "return_date = ?, late_fee = ?,borrower_code = ?, borrower_name = ?, borrower_age = ?, " +
                        "borrower_gender = ?, borrower_address = ? , borrower_phone_number = ? WHERE id = ?",
                bookBorrow.getMemberId(),bookBorrow.getBorrowDate(),bookBorrow.getDueDate(),
                bookBorrow.getReturnDate(),bookBorrow.getLateFee(),bookBorrow.getBorrowerCode(),bookBorrow.getBorrowerName(),
                bookBorrow.getBorrowerAge(), MapperUtil.enumToString(bookBorrow.getBorrowerGender()),
                MapperUtil.enumToString(bookBorrow.getBorrowerAddress()),
                bookBorrow.getBorrowerPhoneNumber(), bookBorrow.getId());
    }
}
