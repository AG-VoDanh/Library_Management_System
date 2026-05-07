package dao;

import model.entity.User;
import model.enums.Address;
import model.enums.Gender;
import model.enums.Role;
import model.search.UserSearchCriteria;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    public static User mapToUser(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("user_code"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("full_name"),
                resultSet.getInt("age"),
                MapperUtil.stringToEnum(resultSet.getString("gender"), Gender.class),
                MapperUtil.stringToEnum(resultSet.getString("address"), Address.class),
                resultSet.getString("email"),
                resultSet.getString("phone_number"),
                Role.valueOf(resultSet.getString("role")),
                resultSet.getInt("status"),
                resultSet.getString("session_token"));
    }

    public User getUserByUserId(Connection connection, int id) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM users WHERE id = ?",UserDao::mapToUser,id).getFirst();
    }


    public int countAllUsers(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM users");
    }
    public List<User> getAllUsers(Connection connection,int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM users LIMIT ?, ?",
                UserDao::mapToUser, (pageIndex -1) * pageSize, pageSize);
    }

    public int countUsersByStatus(Connection connection,int status) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM users WHERE status = ?",status);
    }
    public List<User> getUsersByStatus(Connection connection,int status, int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM users WHERE status = ? LIMIT ?, ?",
                UserDao::mapToUser,status, (pageIndex -1) * pageSize, pageSize);
    }

    public int countUsersByCriteria(Connection connection,UserSearchCriteria userSearchCriteria, String keyword)
            throws SQLException {
        return QueryDao.queryForCount(connection,
                "SELECT COUNT(*) FROM users WHERE "+userSearchCriteria.getColumnName()+" "+
                userSearchCriteria.getSearchType().getOperator()+" ? ", userSearchCriteria.getSearchType().format(keyword));
    }
    public List<User> getUsersByCriteria(Connection connection,UserSearchCriteria userSearchCriteria,
                                         String keyword, int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM users WHERE "+userSearchCriteria.getColumnName()+" "+
                userSearchCriteria.getSearchType().getOperator()+" ? LIMIT ?, ?",
                UserDao::mapToUser,userSearchCriteria.getSearchType().format(keyword),
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int countUsersByCriteriaAndStatus(Connection connection,UserSearchCriteria userSearchCriteria,
                                             String keyword,int status) throws SQLException {
        return QueryDao.queryForCount(connection,
                "SELECT COUNT(*) FROM users WHERE "+userSearchCriteria.getColumnName()+" "+
                userSearchCriteria.getSearchType().getOperator()+" ? AND status = ? ",
                userSearchCriteria.getSearchType().format(keyword),status);
    }
    public List<User> getUsersByCriteriaAndStatus(Connection connection,UserSearchCriteria userSearchCriteria,
                                                  String keyword, int status, int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM users WHERE "+userSearchCriteria.getColumnName()+" "+
                userSearchCriteria.getSearchType().getOperator()+" ? AND status = ? LIMIT ?, ?",
                UserDao::mapToUser,userSearchCriteria.getSearchType().format(keyword), status,
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int addUser(Connection connection,User user) throws SQLException {
        int userId = QueryDao.add(connection, "INSERT INTO users (username , password , full_name , age , gender , address , " +
                "email , phone_number , role ) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                user.getUsername(),user.getPassword(),user.getFullName(),user.getAge(),
                MapperUtil.enumToString(user.getGender()), MapperUtil.enumToString(user.getAddress()),
                user.getEmail(),user.getPhoneNumber(),user.getRole().name());
        QueryDao.update(connection, "UPDATE users SET user_code = ? WHERE id = ?", String.format("USR_%05d", userId), userId);
        return userId;
    }

    public void updateUser(Connection connection,User user) throws SQLException {
        QueryDao.update(connection, "UPDATE users SET username = ?, password = ?, full_name = ?, age = ?, gender = ?, " +
                "address = ?, email = ?, phone_number = ?, role = ? , status = ? WHERE id = ?",
                user.getUsername(),user.getPassword(),user.getFullName(),user.getAge(),
                MapperUtil.enumToString(user.getGender()), MapperUtil.enumToString(user.getAddress()),
                user.getEmail(),user.getPhoneNumber(),user.getRole().name(),user.getStatus(),user.getId());
    }

    public void updateUserStatus(Connection connection,int id, int status) throws SQLException{
        QueryDao.update(connection,"UPDATE users SET status = ? WHERE id = ?", status, id);
    }

    public int checkUserActive(Connection connection, int userId) throws Exception {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM users WHERE id = ? AND status = 1",userId);
    }

    public int checkUserRole(Connection connection, int userId) throws Exception {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM users WHERE id = ? AND role = 'QUAN_TRI'",userId);
    }

    public void updateSessionToken(Connection connection,int userId,String newToKen) throws Exception {
        QueryDao.update(connection,"UPDATE users SET session_token = ? WHERE id = ?",newToKen,userId);
    }

    public int checkUserSessionToken(Connection connection, int userId,String currentRamToken) throws Exception {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM users WHERE id = ? AND session_token = ?",
                userId,currentRamToken);
    }
}
