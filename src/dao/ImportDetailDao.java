package dao;

import model.entity.ImportDetail;
import model.enums.Category;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ImportDetailDao {
    public static ImportDetail mapToImportDetail(ResultSet resultSet) throws SQLException {
        return new ImportDetail(
                resultSet.getInt("id"),
                resultSet.getInt("import_id"),
                resultSet.getInt("book_id"),
                resultSet.getInt("quantity"),
                resultSet.getString("imported_book_code"),
                resultSet.getString("imported_book_name"),
                resultSet.getString("imported_author"),
                resultSet.getInt("imported_publication_year"),
                MapperUtil.stringToEnum(resultSet.getString("imported_category"), Category.class),
                resultSet.getString("imported_image"));
    }

    public int countImportDetailsByImportId(Connection connection, int importId) throws SQLException {
        return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM import_details WHERE import_id = ? ",importId);
    }
    public List<ImportDetail> getImportDetailsByImportId(Connection connection,int importId, int pageIndex,
                                                         int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM import_details WHERE import_id = ? LIMIT ?, ?",
                ImportDetailDao::mapToImportDetail, importId, (pageIndex - 1)* pageSize,pageSize);
    }

    public void addImportDetail(Connection connection,ImportDetail importDetail) throws SQLException {
        QueryDao.add(connection,"INSERT INTO import_details (import_id, book_id, quantity,imported_book_code, " +
                        "imported_book_name, imported_author, imported_publication_year, imported_category, imported_image) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                importDetail.getImportId(),importDetail.getBookId(),importDetail.getQuantity(),importDetail.getImportedBookCode(),
                importDetail.getImportedBookName(), importDetail.getImportedAuthor(),importDetail.getImportedPublicationYear(),
                MapperUtil.enumToString(importDetail.getImportedCategory()),
                importDetail.getImportedImage());
    }

}
