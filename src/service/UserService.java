package service;

import dao.DbHelper;
import dao.UserDao;
import dto.PageResult;
import model.enums.Role;
import model.entity.User;
import model.search.UserSearchCriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDao();

    public int countAllUsers(){
        return DbHelper.runInTransaction(userDao::countAllUsers);
    }

    public User login(String username, String password) {
        return DbHelper.runInTransaction(connection -> {
            List<User> listUser = userDao.getUsersByCriteriaAndStatus(
                    connection, UserSearchCriteria.USERNAME,username,1,1, userDao.countAllUsers(connection));

        if(listUser.isEmpty()){
            return null;
        }

        User user = listUser.getFirst();

        if (!user.getPassword().equals(password)) {
            return null;
        }

            String newToken = UUID.randomUUID().toString();

            userDao.updateSessionToken(connection, user.getId(), newToken);

            user.setSessionToken(newToken);

        return user;
        });
    }

    public PageResult<User> getUsersPage(UserSearchCriteria criteria, String keyword, int pageIndex, int pageSize) {

        return DbHelper.runInTransaction(connection -> {
            List<User> data;
            int total;
            if (criteria == null || keyword == null || keyword.isBlank()) {
                data = userDao.getAllUsers(connection,pageIndex, pageSize);
                total = userDao.countAllUsers(connection);
            } else {
                data = userDao.getUsersByCriteria(connection,criteria, keyword, pageIndex, pageSize);
                total = userDao.countUsersByCriteria(connection,criteria, keyword);
            }
            return new PageResult<>(data,total);
        });
    }

    public String validateAdd(User user){
        return DbHelper.runInTransaction(connection -> {
            List<String> fieldBlankList = new ArrayList<>();

            if (userDao.countUsersByCriteria(connection, UserSearchCriteria.USERNAME, user.getUsername()) > 0) {
                fieldBlankList.add("tên đăng nhập");
            }
            if (userDao.countUsersByCriteria(connection, UserSearchCriteria.EMAIL, user.getEmail()) > 0) {
                fieldBlankList.add("email");
            }
            if (userDao.countUsersByCriteria(connection, UserSearchCriteria.PHONE_NUMBER, user.getPhoneNumber()) > 0) {
                fieldBlankList.add("số điện thoại");
            }

            if (!fieldBlankList.isEmpty()) {
                return String.join(", ", fieldBlankList) + " đã tồn tại";
            }
            return null;
        });
    }

    public void addUser(User currentUser,User user){
            DbHelper.runInTransaction(connection -> {
                if(userDao.checkUserActive(connection,currentUser.getId()) == 0){
                    throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
                }
                if(userDao.checkUserSessionToken(connection,currentUser.getId(),currentUser.getSessionToken()) == 0){
                    throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
                }
                if(userDao.checkUserRole(connection,currentUser.getId()) == 0){
                    throw new SecurityException("Tài khoản của bạn đã bị hạ quyền bởi một người khác. Bạn bị đăng xuất!");
                }
                userDao.addUser(connection,user);
                return null;
            });
    }

    public boolean hasRoleDowngraded(User updateUser, User existingUser, User loginUser){
        return updateUser.getRole() == Role.NHAN_VIEN && existingUser.getRole() == Role.QUAN_TRI &&
                updateUser.getId() == loginUser.getId();
    }

    public String validateUpdate(User updateUser,User existingUser){
        return DbHelper.runInTransaction(connection -> {
            List<String> fieldBlankList = new ArrayList<>();
            if(userDao.countUsersByCriteria(connection,UserSearchCriteria.USERNAME, updateUser.getUsername()) > 0 &&
                    !updateUser.getUsername().equals(existingUser.getUsername())){
                fieldBlankList.add("tên đăng nhập");
            }
            if(userDao.countUsersByCriteria(connection,UserSearchCriteria.EMAIL,updateUser.getEmail()) > 0 &&
                    !updateUser.getEmail().equals(existingUser.getEmail())){
                fieldBlankList.add("email");
            }
            if(userDao.countUsersByCriteria(connection,UserSearchCriteria.PHONE_NUMBER, updateUser.getPhoneNumber()) > 0 &&
                    !updateUser.getPhoneNumber().equals(existingUser.getPhoneNumber())){
                fieldBlankList.add("số điện thoại");
            }
            if (!fieldBlankList.isEmpty()) {
                return String.join(", ", fieldBlankList) + " đã tồn tại";
            }
            return null;
        });
    }

    public void updateUser(User currentUser,User user){
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,currentUser.getId(),currentUser.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if(userDao.checkUserRole(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị hạ quyền bởi một người khác. Bạn bị đăng xuất!");
            }
            userDao.updateUser(connection,user);
            return null;
        });
    }

    public void deleteUser(User currentUser,int userId){
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,currentUser.getId(),currentUser.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if(userDao.checkUserRole(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị hạ quyền bởi một người khác. Bạn bị đăng xuất!");
            }
            userDao.updateUserStatus(connection,userId, 0);
            return null;
        });
    }

    public void restoreUser(User currentUser,int userId){
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,currentUser.getId(),currentUser.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            if(userDao.checkUserRole(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị hạ quyền bởi một người khác. Bạn bị đăng xuất!");
            }
            userDao.updateUserStatus(connection,userId, 1);
            return null;
        });
    }

    public void checkUserLogin(User currentUser){
        DbHelper.runInTransaction(connection -> {
            if(userDao.checkUserActive(connection,currentUser.getId()) == 0){
                throw new SecurityException("Tài khoản của bạn đã bị xóa hoặc ngưng hoạt động bởi một người khác. Bạn bị đăng xuất!");
            }
            if(userDao.checkUserSessionToken(connection,currentUser.getId(),currentUser.getSessionToken()) == 0){
                throw new SecurityException("Tài khoản của bạn đã được đăng nhập ở một thiết bị khác. Bạn bị buộc đăng xuất!");
            }
            return null;
        });
    }
}
