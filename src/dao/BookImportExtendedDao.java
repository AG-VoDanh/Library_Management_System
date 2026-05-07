package dao;

import model.entity.BookImport;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookImportExtendedDao {
    public int countBookImportsByBookCode(Connection connection, String bookCode) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_imports JOIN import_details ON " +
                "book_imports.id = import_details.import_id WHERE imported_book_code LIKE ? " , "%"+bookCode+"%");
    }
    public List<BookImport> getBookImportsByBookCode(Connection connection,String bookCode, int pageIndex,
                                                     int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT book_imports.* FROM book_imports JOIN import_details ON " +
                        "book_imports.id = import_details.import_id WHERE imported_book_code LIKE ? LIMIT ?, ?",
                BookImportDao::mapToBookImport,"%"+bookCode+"%", (pageIndex - 1) * pageSize, pageSize);
    }
    public int countBookImportsByBookName(Connection connection,String bookName) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_imports JOIN import_details ON " +
                "book_imports.id = import_details.import_id WHERE imported_book_name LIKE ? ", "%"+bookName+"%");
    }
    public List<BookImport> getBookImportsByBookName(Connection connection,String bookName,int pageIndex,
                                                     int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT book_imports.* FROM book_imports JOIN import_details ON " +
                        "book_imports.id = import_details.import_id WHERE imported_book_name LIKE ? LIMIT ?, ?",
                BookImportDao::mapToBookImport,"%"+bookName+"%", (pageIndex - 1) * pageSize, pageSize);
    }
}
