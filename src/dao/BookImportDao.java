package dao;

import model.entity.BookImport;
import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;
import model.search.ImportSearchCriteria;
import model.search.SearchType;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class BookImportDao {
    public static BookImport mapToBookImport(ResultSet resultSet) throws SQLException {
        return new BookImport(
                resultSet.getInt("id"),
                resultSet.getString("import_code"),
                resultSet.getInt("user_id"),
                resultSet.getObject("import_date", LocalDate.class),
                resultSet.getString("importer_code"),
                resultSet.getString("importer_username"),
                resultSet.getString("importer_full_name"),
                resultSet.getInt("importer_age"),
                MapperUtil.stringToEnum(resultSet.getString("importer_gender"), Gender.class),
                MapperUtil.stringToEnum(resultSet.getString("importer_address"), Address.class),
                resultSet.getString("importer_email"),
                resultSet.getString("importer_phone_number"),
                MapperUtil.stringToEnum(resultSet.getString("importer_role"), Role.class));
    }

    public int countAllBookImports(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM book_imports");
    }
    public List<BookImport> getAllBookImports(Connection connection,int pageIndex, int pageSize)
            throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM book_imports LIMIT ?, ?",
                BookImportDao::mapToBookImport, (pageIndex -1) * pageSize, pageSize);
    }

    public int countBookImportsByCriteria(Connection connection,ImportSearchCriteria importSearchCriteria,
                                          Object... keyword) throws SQLException {
        if(importSearchCriteria.getSearchType() == SearchType.BETWEEN){
            return QueryDao.queryForCount(connection,
                    "SELECT COUNT(*) FROM book_imports WHERE "+importSearchCriteria.getColumnName()+" BETWEEN ? AND ? ",
                    keyword[0],keyword[1]);
        }
        return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM book_imports WHERE "+
                        importSearchCriteria.getColumnName()+" "+ importSearchCriteria.getSearchType().getOperator()+" ? ",
                importSearchCriteria.getSearchType().format(keyword[0].toString()));
    }
    public List<BookImport> getBookImportsByCriteria(
            Connection connection,ImportSearchCriteria importSearchCriteria, int pageIndex, int pageSize,
                                                     Object... keyword) throws SQLException {
        if(importSearchCriteria.getSearchType() == SearchType.BETWEEN){
            return QueryDao.queryForList(connection, "SELECT * FROM book_imports WHERE "+
                            importSearchCriteria.getColumnName()+" BETWEEN ? AND ? LIMIT ?, ?",
                    BookImportDao::mapToBookImport,keyword[0],keyword[1],(pageIndex - 1)* pageSize,pageSize);
        }
        return QueryDao.queryForList(connection, "SELECT * FROM book_imports WHERE "+
                        importSearchCriteria.getColumnName()+" "+ importSearchCriteria.getSearchType().getOperator()+
                        " ? LIMIT ?, ?",
                BookImportDao::mapToBookImport,importSearchCriteria.getSearchType().format(keyword[0].toString()),
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int addBookImport(Connection connection,BookImport bookImport) throws SQLException {
        int bookImportId = QueryDao.add(connection,"INSERT INTO book_imports (user_id, import_date,importer_code, importer_username, " +
                        "importer_full_name, importer_age, importer_gender, importer_address, importer_email, " +
                        "importer_phone_number , importer_role) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                bookImport.getUserId(),bookImport.getImportDate(),bookImport.getImporterCode(),
                bookImport.getImporterUsername(), bookImport.getImporterFullName(),bookImport.getImporterAge(),
                MapperUtil.enumToString(bookImport.getImporterGender()),
                MapperUtil.enumToString(bookImport.getImporterAddress()),bookImport.getImporterEmail(),
                bookImport.getImportPhoneNumber(),MapperUtil.enumToString(bookImport.getImporterRole()));
        QueryDao.update(connection, "UPDATE book_imports SET import_code = ? WHERE id = ?",
                String.format("IMP_%05d", bookImportId), bookImportId);
        return bookImportId;
    }
}
