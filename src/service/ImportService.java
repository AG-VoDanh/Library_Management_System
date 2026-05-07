package service;

import dao.*;
import dto.PageResult;
import model.entity.Book;
import model.entity.BookImport;
import model.entity.ImportDetail;
import model.entity.User;
import model.search.BookSearchCriteria;
import model.search.ImportSearchCriteria;
import util.AppUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImportService {
    private final BookImportDao bookImportDao = new BookImportDao();
    private final BookImportExtendedDao bookImportExtendedDao = new BookImportExtendedDao();
    private final ImportDetailDao importDetailDao = new ImportDetailDao();
    private final BookDao bookDao = new BookDao();
    private final UserDao userDao = new UserDao();

    public PageResult<BookImport> getBookImportsPage(
            ImportSearchCriteria criteria, int pageIndex, int pageSize, Object... keyword) {
        return DbHelper.runInTransaction(connection -> {
            List<BookImport> data;
            int total;

            boolean invalidKeyword = keyword == null || keyword.length == 0;

            if (criteria == null || invalidKeyword) {
                data = bookImportDao.getAllBookImports(connection,pageIndex, pageSize);
                total = bookImportDao.countAllBookImports(connection);
            }  else if (criteria == ImportSearchCriteria.BOOK_CODE) {
                data = bookImportExtendedDao.getBookImportsByBookCode(connection,keyword[0].toString(),pageIndex, pageSize);
                total = bookImportExtendedDao.countBookImportsByBookCode(connection,keyword[0].toString());
            } else if (criteria == ImportSearchCriteria.BOOK_NAME) {
                data = bookImportExtendedDao.getBookImportsByBookName(connection,keyword[0].toString(),pageIndex, pageSize);
                total = bookImportExtendedDao.countBookImportsByBookName(connection,keyword[0].toString());
            } else {
                data = bookImportDao.getBookImportsByCriteria(connection,criteria, pageIndex, pageSize, keyword);
                total = bookImportDao.countBookImportsByCriteria(connection,criteria, keyword);
            }

            return new PageResult<>(data, total);
        });
    }

    public PageResult<ImportDetail> getImportedBookPage(int importId, int pageIndex, int pageSize) {
        return DbHelper.runInTransaction(connection -> {
            List<ImportDetail> data;
            int total;

            data = importDetailDao.getImportDetailsByImportId(connection,importId,pageIndex, pageSize);
            total = importDetailDao.countImportDetailsByImportId(connection,importId);

            return new PageResult<>(data, total);
        });
    }

    public PageResult<Book> getBooksPage(boolean isSearching, String keyword, int pageIndex, int pageSize) {
        return DbHelper.runInTransaction(connection -> {
            List<Book> data;
            int total;

            if (isSearching && !keyword.isBlank()) {
                data = bookDao.getBooksByCriteria(connection,BookSearchCriteria.BOOK_NAME, keyword, pageIndex, pageSize);
                total = bookDao.countBooksByCriteria(connection,BookSearchCriteria.BOOK_NAME, keyword);
            } else {
                data = bookDao.getAllBooks(connection,pageIndex, pageSize);
                total = bookDao.countAllBooks(connection);
            }
            return new PageResult<>(data, total);
        });
    }

    public int countAllBookImports(){
        return DbHelper.runInTransaction(bookImportDao::countAllBookImports);
    }

    public boolean checkBook(String keyword){
        return DbHelper.runInTransaction(connection ->
                bookDao.countBooksByCriteria(connection,BookSearchCriteria.BOOK_NAME,keyword) > 0);
    }

    public Book addBook(Book book,File selectedImageFile) {
        if(selectedImageFile != null){
            String originalName = book.getImage();
            String extension = originalName.substring(originalName.lastIndexOf("."));
            String baseName = originalName.substring(0, originalName.lastIndexOf("."));
            String uniqueName = baseName + "_" + System.currentTimeMillis() + extension;

            book.setImage(uniqueName);

            try {
                Path destFolder = AppUtil.getAppImageDirectory("books");
                Path destFile = destFolder.resolve(book.getImage());
                Files.copy(selectedImageFile.toPath(), destFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return book;
    }
    public String validateBookImport(List<Book> books, Map<Integer, Integer> quantityMap){
        StringBuilder error = new StringBuilder();

        for (Book book : books) {
            List<String> fieldBlankList = new ArrayList<>();

            Integer quantity = quantityMap.get(book.getId());

            if (quantity == null) {
                fieldBlankList.add("Bạn phải nhập số lượng");
            } else if (quantity == 0) {
                fieldBlankList.add("Bạn phải nhập số lượng > 0");
            } else if (quantity > Integer.MAX_VALUE - book.getQuantity()) {
                fieldBlankList.add("Số lượng vượt giới hạn tối đa là " + (Integer.MAX_VALUE - book.getQuantity()));
            }

            if (!fieldBlankList.isEmpty()) {
                error.append(book.getBookName())
                        .append(": ")
                        .append(String.join(", ", fieldBlankList))
                        .append("\n");
            }
        }

        return error.isEmpty() ? null : error.toString();
    }
    public void addBookImport(User user, List<Book> books, Map<Integer, Integer> quantityMap) {
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            int importId = bookImportDao.addBookImport(connection,
                    new BookImport(
                    null,
                    user.getId(),
                    LocalDate.now(),

                    user.getUserCode(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getAge(),
                    user.getGender(),
                    user.getAddress(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getRole()
            ));
            Integer quantity;
            for (Book book : books) {
                quantity = quantityMap.get(book.getId());
                if(book.getId() < 0){
                    book = bookDao.getBookByBookId(connection,bookDao.addBook(connection,book));
                }else {
                    bookDao.importBookQuantity(connection,book.getId(),quantity);
                }
                String imageName = book.getImage();
                if (imageName != null && !imageName.isBlank()) {
                    try {
                        Path bookPath = AppUtil.getAppImageDirectory("books").resolve(imageName);
                        Path importPath = AppUtil.getAppImageDirectory("imports").resolve(imageName);

                        if (Files.exists(bookPath) && !Files.exists(importPath)) {
                            Files.copy(bookPath, importPath);
                        }
                    } catch (Exception _) {

                    }
                }
                importDetailDao.addImportDetail(connection,
                        new ImportDetail(
                        importId,
                        book.getId(),
                        quantity,

                        book.getBookCode(),
                        book.getBookName(),
                        book.getAuthor(),
                        book.getPublicationYear(),
                        book.getCategory(),
                        book.getImage()
                ));
            }
            return null;
        });
    }
}
