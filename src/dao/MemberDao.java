package dao;

import model.entity.Member;
import model.enums.Address;
import model.enums.Gender;
import model.search.MemberSearchCriteria;
import util.MapperUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MemberDao {
    public static Member mapToMember(ResultSet resultSet) throws SQLException {
        return new Member(
                resultSet.getInt("id"),
                resultSet.getString("member_code"),
                resultSet.getString("member_name"),
                resultSet.getInt("age"),
                MapperUtil.stringToEnum(resultSet.getString("gender"), Gender.class),
                MapperUtil.stringToEnum(resultSet.getString("address"), Address.class),
                resultSet.getString("phone_number"),
                resultSet.getInt("status"));
    }

    public Member getMemberByMemberId(Connection connection, int id) throws SQLException {
        return QueryDao.queryForList(connection,"SELECT * FROM members WHERE id = ?",MemberDao::mapToMember,id).getFirst();
    }

    public int countAllMembers(Connection connection) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM members");
    }
    public List<Member> getAllMembers(Connection connection,int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM members LIMIT ?, ?",
                MemberDao::mapToMember, (pageIndex -1) * pageSize, pageSize);
    }

    public int countMembersByStatus(Connection connection,int status) throws SQLException {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM members WHERE status = ?",status);
    }
    public List<Member> getMembersByStatus(Connection connection,int status, int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM members WHERE status = ? LIMIT ?, ?",
                MemberDao::mapToMember,status, (pageIndex -1) * pageSize, pageSize);
    }

    public int countMembersByCriteria(Connection connection,MemberSearchCriteria memberSearchCriteria,
                                      String keyword) throws SQLException {
        return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM members WHERE "+memberSearchCriteria.getColumnName()+" "+
                memberSearchCriteria.getSearchType().getOperator()+" ? ", memberSearchCriteria.getSearchType().format(keyword));
    }
    public List<Member> getMembersByCriteria(Connection connection,MemberSearchCriteria memberSearchCriteria,
                                             String keyword, int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM members WHERE "+memberSearchCriteria.getColumnName()+" "+
                        memberSearchCriteria.getSearchType().getOperator()+" ? LIMIT ?, ?",
                MemberDao::mapToMember,memberSearchCriteria.getSearchType().format(keyword),
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int countMembersByCriteriaAndStatus(Connection connection,MemberSearchCriteria memberSearchCriteria,
                                               String keyword,int status) throws SQLException {
        return QueryDao.queryForCount(connection, "SELECT COUNT(*) FROM members WHERE "+memberSearchCriteria.getColumnName()+" "+
                        memberSearchCriteria.getSearchType().getOperator()+" ? AND status = ? ",
                memberSearchCriteria.getSearchType().format(keyword),status);
    }
    public List<Member> getMembersByCriteriaAndStatus(Connection connection,MemberSearchCriteria memberSearchCriteria, String keyword, int status,
                                                       int pageIndex, int pageSize) throws SQLException {
        return QueryDao.queryForList(connection, "SELECT * FROM members WHERE "+memberSearchCriteria.getColumnName()+" "+
                        memberSearchCriteria.getSearchType().getOperator()+" ? AND status = ? LIMIT ?, ?",
                MemberDao::mapToMember,memberSearchCriteria.getSearchType().format(keyword), status,
                (pageIndex - 1)* pageSize,pageSize);
    }

    public int addMember(Connection connection,Member member) throws SQLException {
        int memberId = QueryDao.add(connection, "INSERT INTO members (member_name , age , gender , address , phone_number ) " +
                "VALUES (?, ?, ?, ?, ?)",
                member.getMemberName(),member.getAge(), MapperUtil.enumToString(member.getGender()),
                MapperUtil.enumToString(member.getAddress()), member.getPhoneNumber());
        QueryDao.update(connection, "UPDATE members SET member_code = ? WHERE id = ?",
                String.format("MEM_%05d", memberId), memberId);
        return memberId;
    }

    public void updateMember(Connection connection,Member member) throws SQLException {
        QueryDao.update(connection,"UPDATE members SET member_name = ?, age = ?, gender = ?, address = ?, " +
                        "phone_number = ? , status = ? WHERE id = ?",
                member.getMemberName(),member.getAge(), MapperUtil.enumToString(member.getGender()),
                MapperUtil.enumToString(member.getAddress()), member.getPhoneNumber(),member.getStatus(),member.getId());
    }

    public void updateMemberStatus(Connection connection,int id, int status) throws SQLException {
        QueryDao.update(connection,"UPDATE members SET status = ? WHERE id = ?", status, id);
    }

    public int checkMemberActive(Connection connection, int memberId) throws Exception {
        return QueryDao.queryForCount(connection,"SELECT COUNT(*) FROM members WHERE id = ? AND status = 1",memberId);
    }
}
