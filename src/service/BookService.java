package service;

import dao.BookDao;
import dao.DbHelper;
import dao.UserDao;
import dto.PageResult;
import model.entity.Book;
import model.entity.User;
import model.search.BookSearchCriteria;
import util.AppUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class BookService {
    private final BookDao bookDao = new BookDao();
    private final UserDao userDao = new UserDao();

    public PageResult<Book> getBooksPage(BookSearchCriteria criteria, String keyword, int pageIndex, int pageSize) {
        return DbHelper.runInTransaction(connection -> {
            List<Book> data;
            int total;

            if (criteria == null || keyword == null || keyword.isBlank()) {
                data = bookDao.getAllBooks(connection,pageIndex, pageSize);
                total = bookDao.countAllBooks(connection);
            } else {
                data = bookDao.getBooksByCriteria(connection,criteria, keyword, pageIndex, pageSize);
                total = bookDao.countBooksByCriteria(connection,criteria, keyword);
            }

            return new PageResult<>(data, total);
        });
    }

    public String updateBook(User user, Book book, Book existingBook, File selectedImageFile, boolean isRemoveImage) {
        return DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if (bookDao.countBooksUpdateByBookName(connection,book.getBookName()) > 0
                    && !book.getBookName().equals(existingBook.getBookName())) {
                return "tên sách đã tồn tại";
            }
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
                    return "Lỗi khi lưu ảnh";
                }
                if(existingBook.getImage() != null){
                    try {
                        Path destFolder = AppUtil.getAppImageDirectory("books");
                        Path destFile = destFolder.resolve(existingBook.getImage());
                        Files.deleteIfExists(destFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }else if(isRemoveImage && existingBook.getImage() != null){
                try {
                    Path destFolder = AppUtil.getAppImageDirectory("books");
                    Path destFile = destFolder.resolve(existingBook.getImage());
                    Files.deleteIfExists(destFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                book.setImage(existingBook.getImage());
            }
            bookDao.updateBook(connection,book);
            return null;
        });

    }

}
