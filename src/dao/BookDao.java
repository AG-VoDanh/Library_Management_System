package dao;

import model.entity.Book;
import model.enums.Category;
import model.search.BookSearchCriteria;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BookDao {
    public static Book mapToBook(ResultSet resultSet) throws SQLException {
        return new Book(
                resultSet.getInt("id"),
                resultSet.getString("book_code"),
                resultSet.getString("book_name"),
                resultSet.getString("author"),
                resultSet.getInt("publication_year"),
                MapperUtil.stringToEnum(resultSet.getString("category"), Category.class),
                resultSet.getInt("quantity"),
                resultSet.getString("image"),
                resultSet.getInt("available_quantity"));
    }

    public int countBooksUpdateByBookName(Connection connection, String keyword) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM books WHERE book_name = ? ",keyword);
    }

    public Book getBookByBookId(Connection connection,int id) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM books WHERE id = ?",BookDao::mapToBook,id).getFirst();
    }

    public int countAllBooks(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM books");
    }
    public List<Book> getAllBooks(Connection connection,int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM books LIMIT ?, ?",
                BookDao::mapToBook, (pageIndex -1) * pageSize, pageSize);
    }

    public int countBooksByCriteria(Connection connection,BookSearchCriteria bookSearchCriteria,
                                    String keyword) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM books WHERE "+
                        bookSearchCriteria.getColumnName()+" "+ bookSearchCriteria.getSearchType().getOperator()+" ? ",
                bookSearchCriteria.getSearchType().format(keyword));
    }
    public List<Book> getBooksByCriteria(Connection connection,BookSearchCriteria bookSearchCriteria,
                                         String keyword, int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM books WHERE "+bookSearchCriteria.getColumnName()+" "+
                        bookSearchCriteria.getSearchType().getOperator()+" ? LIMIT ?, ?",
                BookDao::mapToBook,bookSearchCriteria.getSearchType().format(keyword),
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int addBook(Connection connection,Book book) throws SQLException {
        int bookId = QueryDao.add(connection,"INSERT INTO books (book_name , author , publication_year , category , quantity , " +
                        "image,available_quantity) VALUES(?, ?, ?, ?, ?, ?,?)",
                        book.getBookName(),book.getAuthor(),book.getPublicationYear(),
                        MapperUtil.enumToString(book.getCategory()),
                        book.getQuantity(),book.getImage(),book.getAvailableQuantity());
        QueryDao.update(connection, "UPDATE books SET book_code = ? WHERE id = ?",
                String.format("BK_%05d", bookId), bookId);
        return bookId;
    }

    public void updateBook(Connection connection,Book book) throws SQLException {
        QueryDao.update(connection,"UPDATE books SET book_name = ?, author = ?, publication_year = ?, category = ?, " +
                        "quantity = ?, image = ?, available_quantity = ? WHERE id = ?",
                book.getBookName(),book.getAuthor(),book.getPublicationYear(), MapperUtil.enumToString(book.getCategory()),
                book.getQuantity(),book.getImage(),book.getAvailableQuantity(),book.getId());
    }

    public int deductAvailableQuantity(Connection connection, int bookId, int borrowQuantity) throws Exception {
        return  QueryDao.update(connection,
                "UPDATE books SET available_quantity = available_quantity - ? WHERE id = ? AND available_quantity >= ?",
                borrowQuantity, bookId, borrowQuantity);
    }

    public void addAvailableQuantity(Connection connection, int bookId, int returnQuantity) throws SQLException {
        QueryDao.update(connection, "UPDATE books SET available_quantity = available_quantity + ? WHERE id = ?",
                returnQuantity, bookId);
    }

    public void importBookQuantity(Connection connection, int bookId, int importQuantity) throws SQLException {
        QueryDao.update(connection,
                "UPDATE books SET quantity = quantity + ?, available_quantity = available_quantity + ? WHERE id = ?",
                importQuantity, importQuantity, bookId);
    }
}
