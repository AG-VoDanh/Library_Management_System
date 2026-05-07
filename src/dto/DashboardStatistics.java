package dto;

import java.util.List;

public record DashboardStatistics(
        int monthlyBorrowedBooks,
        int monthlyBorrows,
        int overdueBorrows,
        List<String> topBooks,
        List<String> topMembers
) {}