package service;

import dao.BookBorrowDao;
import dao.DbHelper;
import dao.MemberDao;
import dao.UserDao;
import dto.PageResult;
import model.entity.Member;
import model.entity.User;
import model.search.MemberSearchCriteria;

import java.util.List;

public class MemberService {
    private final MemberDao memberDao = new MemberDao();
    private final UserDao userDao = new UserDao();
    private final BookBorrowDao bookBorrowDao = new BookBorrowDao();

    public int countBookBorrowsByMemberId(int memberId){
        return DbHelper.runInTransaction(connection -> bookBorrowDao.countBookBorrowsByMemberId(connection,memberId));
    }

    public int countAllMembers(){
        return DbHelper.runInTransaction(memberDao::countAllMembers);
    }

    public PageResult<Member> getMembersPage(MemberSearchCriteria criteria, String keyword, int pageIndex, int pageSize) {
        return DbHelper.runInTransaction(connection -> {
            List<Member> data;
            int total;

            if (criteria == null || keyword == null || keyword.isBlank()) {
                data = memberDao.getAllMembers(connection,pageIndex, pageSize);
                total = memberDao.countAllMembers(connection);
            } else {
                data = memberDao.getMembersByCriteria(connection,criteria, keyword, pageIndex, pageSize);
                total = memberDao.countMembersByCriteria(connection,criteria, keyword);
            }

            return new PageResult<>(data, total);
        });

    }

    public String validateAddMember(Member member){
        return DbHelper.runInTransaction(connection -> {
            if (memberDao.countMembersByCriteria(connection,MemberSearchCriteria.PHONE_NUMBER, member.getPhoneNumber()) > 0) {
                return  "số điện thoại đã tồn tại";
            }
            return null;
        });

    }

    public Member addMember(User user,Member member){
        return DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            int memberId = memberDao.addMember(connection,member);
            return memberDao.getMemberByMemberId(connection,memberId);
        });

    }

    public String updateMember(User user,Member member,Member existingMember){
        return DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if(memberDao.countMembersByCriteria(connection,MemberSearchCriteria.PHONE_NUMBER, member.getPhoneNumber()) > 0 &&
                    !member.getPhoneNumber().equals(existingMember.getPhoneNumber())){
                return  "số điện thoại đã tồn tại";
            }
            memberDao.updateMember(connection,member);
            return null;
        });

    }

    public String deleteMember(User user,int memberId){
        return DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if (userDao.checkUserRole(connection, user.getId()) == 0) {
                throw new SecurityException("Tài khoản của bạn đã bị hạ quyền bởi một người khác. Bạn bị đăng xuất!");
            }
            if(bookBorrowDao.countBookBorrowsByMemberId(connection,memberId) > 0){
                return "hội viên nay vẫn đang mượn sách";
            }
            memberDao.updateMemberStatus(connection,memberId, 0);
            return null;
        });
    }

    public void restoreMember(User user,int memberId){
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,user.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,user.getId(),user.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            memberDao.updateMemberStatus(connection,memberId, 1);
            return null;
        });

    }

}
