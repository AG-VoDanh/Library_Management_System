package service;

import dao.*;
import dto.PageResult;
import model.entity.*;
import model.search.BookSearchCriteria;
import model.search.BorrowSearchCriteria;
import model.search.MemberSearchCriteria;
import util.AppUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public class BorrowService {
    private final BookBorrowDao bookBorrowDao = new BookBorrowDao();
    private final BookBorrowExtendedDao bookBorrowExtendedDao = new BookBorrowExtendedDao();
    private final BorrowDetailDao borrowDetailDao = new BorrowDetailDao();
    private final MemberDao memberDao = new MemberDao();
    private final BookDao bookDao = new BookDao();
    private final UserDao userDao = new UserDao();

    public String getBookBorrowStaus(BookBorrow bookBorrow){
        LocalDate returnDate = bookBorrow.getReturnDate();
        LocalDate dueDate = bookBorrow.getDueDate();
        if(returnDate == null){
            return "Đang mượn" ;
        }else if(returnDate.isAfter(dueDate)){
            return "Trả trễ hạn";
        }else {
            return "Trả đúng hạn";
        }
    }

    public PageResult<BookBorrow> getBookBorrowsPageByMemberId(int memberId,int pageIndex, int pageSize){
        return DbHelper.runInTransaction(connection -> {
            List<BookBorrow> data;
            int total;

            data = bookBorrowDao.getBookBorrowsByMemberId(connection,memberId,pageIndex, pageSize);
            total = bookBorrowDao.countBookBorrowsByMemberId(connection,memberId);
            return new PageResult<>(data, total);
        });
    }

    public PageResult<BookBorrow> getBookBorrowsPage(
            BorrowSearchCriteria criteria, int pageIndex, int pageSize, Object... keyword) {
        return DbHelper.runInTransaction(connection -> {
            List<BookBorrow> data;
            int total;

            boolean invalidKeyword = keyword == null || keyword.length == 0;

            if (criteria == null || invalidKeyword) {
                data = bookBorrowDao.getAllBookBorrows(connection,pageIndex, pageSize);
                total = bookBorrowDao.countAllBookBorrows(connection);
            } else if (criteria == BorrowSearchCriteria.STATUS) {
                String status = keyword[0].toString();

                switch (status) {
                    case "Đang mượn" -> {
                        data = bookBorrowDao.findActiveBookBorrow(connection,pageIndex, pageSize);
                        total = bookBorrowDao.countActiveBookBorrow(connection);
                    }
                    case "Trả trễ hạn" -> {
                        data = bookBorrowDao.findOverdueBookBorrows(connection,pageIndex, pageSize);
                        total = bookBorrowDao.countOverdueBookBorrows(connection);
                    }
                    case "Trả đúng hạn" -> {
                        data = bookBorrowDao.findOnTimeBookBorrows(connection,pageIndex, pageSize);
                        total = bookBorrowDao.countOnTimeBookBorrows(connection);
                    }
                    default -> {
                        data = List.of();
                        total = 0;
                    }
                }
            } else if (criteria == BorrowSearchCriteria.BOOK_CODE) {
                data = bookBorrowExtendedDao.getBookBorrowsByBookCode(connection,keyword[0].toString(),pageIndex, pageSize);
                total = bookBorrowExtendedDao.countBookBorrowsByBookCode(connection,keyword[0].toString());
            } else if (criteria == BorrowSearchCriteria.BOOK_NAME) {
                data = bookBorrowExtendedDao.getBookBorrowsByBookName(connection,keyword[0].toString(),pageIndex, pageSize);
                total = bookBorrowExtendedDao.countBookBorrowsByBookName(connection,keyword[0].toString());
            } else {
                data = bookBorrowDao.getBookBorrowsByCriteria(connection,criteria, pageIndex, pageSize, keyword);
                total = bookBorrowDao.countBookBorrowsByCriteria(connection,criteria, keyword);
            }

            return new PageResult<>(data, total);
        });
    }

    public PageResult<BorrowDetail> getBorrowedBookPage(int borrowId,int pageIndex, int pageSize) {
        return DbHelper.runInTransaction(connection -> {
            List<BorrowDetail> data;
            int total;

            data = borrowDetailDao.getBorrowDetailsByBorrowId(connection,borrowId,pageIndex, pageSize);
            total = borrowDetailDao.countBorrowDetailsByBorrowId(connection,borrowId);

            return new PageResult<>(data, total);
        });
    }

    public int countAllBookBorrows(){
        return DbHelper.runInTransaction(bookBorrowDao::countAllBookBorrows);
    }

    public PageResult<Member> getMembersPage(boolean isSearching, String keyword, int pageIndex, int pageSize) {
        return DbHelper.runInTransaction(connection -> {
            List<Member> data;
            int total;

            if (isSearching && !keyword.isBlank()) {
                data = memberDao.getMembersByCriteria(connection,MemberSearchCriteria.MEMBER_NAME, keyword, pageIndex, pageSize);
                total = memberDao.countMembersByCriteria(connection,MemberSearchCriteria.MEMBER_NAME, keyword);
            } else {
                data = memberDao.getAllMembers(connection,pageIndex, pageSize);
                total = memberDao.countAllMembers(connection);
            }
            return new PageResult<>(data, total);
        });
    }

    public boolean checkMember(String keyword){
        return DbHelper.runInTransaction(connection ->
                memberDao.countMembersByCriteria(connection,MemberSearchCriteria.MEMBER_NAME, keyword) > 0);
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

    public boolean checkBook(String keyword){
        return DbHelper.runInTransaction(connection ->
                bookDao.countBooksByCriteria(connection,BookSearchCriteria.BOOK_NAME, keyword) > 0);
    }

    public void addBookBorrow(User user,Member member, List<Book> books, Map<Integer, Integer> quantityMap,
                              LocalDate dueDate) {
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if(memberDao.checkMemberActive(connection,member.getId()) == 0){
                throw new Exception("Hội viên '" + member.getMemberName() + "' đã bị xóa hoặc ngưng hoạt động bởi một người " +
                        "khác.Hãy khôi phục hôi viên hoặc chọn học viên khác để tiếp tục hoạt động");
            }
            int borrowId = bookBorrowDao.addBookBorrow(connection,
                    new BookBorrow(
                    null,
                    member.getId(),
                    LocalDate.now(),
                    dueDate,
                    null,
                    null,
                    member.getMemberCode(),
                    member.getMemberName(),
                    member.getAge(),
                    member.getGender(),
                    member.getAddress(),
                    member.getPhoneNumber()
            ));

            for (Book book : books) {
                int requestQuantity = quantityMap.get(book.getId());
                if(bookDao.deductAvailableQuantity(connection, book.getId(), requestQuantity) == 0){
                    throw new Exception("Cuốn sách '" + book.getBookName() + "' đã hết sách khả dụng trong kho!");
                }
                String imageName = book.getImage();
                if (imageName != null && !imageName.isBlank()) {
                    try {
                        Path bookPath = AppUtil.getAppImageDirectory("books").resolve(imageName);
                        Path borrowPath = AppUtil.getAppImageDirectory("borrows").resolve(imageName);
                        if (Files.exists(bookPath) && !Files.exists(borrowPath)) {
                            Files.copy(bookPath, borrowPath);
                        }
                    } catch (Exception _) {

                    }
                }
                borrowDetailDao.addBorrowDetail(connection,
                        new BorrowDetail(
                        borrowId,
                        book.getId(),
                        requestQuantity,
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

    public void returnBook(User user,BookBorrow bookBorrow){
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            bookBorrow.setReturnDate(LocalDate.now());
            bookBorrow.setLateFee(Math.max(0, Math.toIntExact(ChronoUnit.DAYS.between(bookBorrow.getDueDate(), LocalDate.now()))) * 5000);
            List<BorrowDetail> listBorrowDetails = borrowDetailDao.getBorrowDetailsByBorrowId(
                    connection,bookBorrow.getId(),
                    1,borrowDetailDao.countBorrowDetailsByBorrowId(connection,bookBorrow.getId()));
            for(BorrowDetail borrowDetail : listBorrowDetails){
                bookDao.addAvailableQuantity(connection,borrowDetail.getBookId(),borrowDetail.getQuantity());
            }
            bookBorrowDao.updateBooKBorrow(connection,bookBorrow);
            return null;
        });
    }
}