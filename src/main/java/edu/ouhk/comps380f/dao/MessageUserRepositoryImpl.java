/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.ouhk.comps380f.dao;

import edu.ouhk.comps380f.model.MessageUser;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 *
 * @author student
 */
@Repository
public class MessageUserRepositoryImpl implements MessageUserRepository {

    private DataSource dataSource;
    private JdbcOperations jdbcOp;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcOp = new JdbcTemplate(this.dataSource);
    }
    // data access operations

    private static final String SQL_INSERT_USER
            = "insert into users (username, password) values (?, ?)";
    private static final String SQL_INSERT_ROLE
            = "insert into user_roles (username, role) values (?, ?)";

    @Override
    public void create(MessageUser user) {
        jdbcOp.update(SQL_INSERT_USER,
                user.getUsername(),
                user.getPassword());
        for (String role : user.getRoles()) {
            jdbcOp.update(SQL_INSERT_ROLE,
                    user.getUsername(),
                    role);
        }
    }
///////////////////////////////////////////////////////////////
    private static final String SQL_SELECT_ALL_USER
            = "select username, password from users";
    private static final String SQL_SELECT_ROLES
            = "select username, role, ban from user_roles where username = ?";

    @Override
    public List<MessageUser> findAll() {
        List<MessageUser> users = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcOp.queryForList(SQL_SELECT_ALL_USER);
        for (Map<String, Object> row : rows) {
            MessageUser user = new MessageUser();
            String username = (String) row.get("username"); //obj cast to string
            user.setUsername(username);
            user.setPassword((String) row.get("password"));
            
            List<Map<String, Object>> roleRows
                    = jdbcOp.queryForList(SQL_SELECT_ROLES, username);
            for (Map<String, Object> roleRow : roleRows) {
                user.addRole((String) roleRow.get("role"));
                user.setBan((boolean) roleRow.get("ban"));
            }
            
            users.add(user);
        }
        return users;
    }
////////////////////////////////////////////////////////////////

    private static final class TicketUserRowMapper
            implements RowMapper<MessageUser> {

        @Override
        public MessageUser mapRow(ResultSet rs, int i) throws SQLException {
            MessageUser user = new MessageUser();
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            return user;
        }
    }

    private static final String SQL_SELECT_USER
            = "select username, password from users where username = ?";
    //query for obj (return 1 user only) (need a row mapper^^^)

    @Override
    public MessageUser findByUsername(String username) {
        MessageUser ticketUser = jdbcOp.queryForObject(SQL_SELECT_USER,
                new TicketUserRowMapper(), username);
        List<Map<String, Object>> rows = jdbcOp.queryForList(SQL_SELECT_ROLES,
                username);
        for (Map<String, Object> row : rows) {
            ticketUser.addRole((String) row.get("role"));
        }
        return ticketUser;
    }
////////////////////////////////////////////////////////////
    private static final String SQL_DELETE_USER
            = "delete from users where username = ?";
    private static final String SQL_DELETE_ROLES
            = "delete from user_roles where username = ?";
    private static final String SQL_DELETE_REPLY
            = "delete from messageReply where username = ?";
    private static final String SQL_DELETE_TOPIC
            = "delete from messageTopic where username = ?";

    @Override
    public void deleteByUsername(String username) {
        jdbcOp.update(SQL_DELETE_ROLES, username); //must delete first (foreign key)
        jdbcOp.update(SQL_DELETE_REPLY, username); 
        jdbcOp.update(SQL_DELETE_TOPIC, username); 
        jdbcOp.update(SQL_DELETE_USER, username);
    }

    private static final String SQL_BAND_ROLES
            = "update user_roles set ban = ? where username = ?";

    @Override
    public void bandByUsername(String username) {
        jdbcOp.update(SQL_BAND_ROLES, "true", username);
    }
}
