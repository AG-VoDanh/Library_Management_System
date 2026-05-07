package service;

import dao.BookBorrowExtendedDao;
import dao.DbHelper;
import dto.DashboardStatistics;

import java.util.List;

public class StatisticsService {
    private final BookBorrowExtendedDao bookBorrowExtendedDao = new BookBorrowExtendedDao();

    public DashboardStatistics getDashboardStatistics() {
        return DbHelper.runInTransaction(connection -> {
            int borrowedBooks = bookBorrowExtendedDao.countMonthlyBorrowedBook(connection);
            int borrows = bookBorrowExtendedDao.countMonthlyBorrow(connection);
            int overdue = bookBorrowExtendedDao.countOverdueBorrow(connection);
            List<String> books = bookBorrowExtendedDao.getTopBorrowedBooksThisMonth(connection);
            List<String> members = bookBorrowExtendedDao.getTopBorrowingMembersThisMonth(connection);
            
            return new DashboardStatistics(borrowedBooks, borrows, overdue, books, members);
        });
    }
}
