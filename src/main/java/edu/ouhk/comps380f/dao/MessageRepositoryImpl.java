package edu.ouhk.comps380f.dao;

import edu.ouhk.comps380f.model.Message;
import edu.ouhk.comps380f.model.MessageReply;
import edu.ouhk.comps380f.model.MessageUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MessageRepositoryImpl implements MessageRepository {

    @Autowired
    DataSource dataSource;

    private static final String SQL_INSERT_ENTRY
            = "insert into messageTopic (username, title, message, date, category) values (?, ?, ?, ?, ?)";

    @Override
    public void create(Message entry, MessageUser user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_ENTRY);
            stmt.setString(1, entry.getCustomerName());
            stmt.setString(2, entry.getSubject());
            stmt.setString(3, entry.getBody());
            stmt.setTimestamp(4, new Timestamp(entry.getDate().getTime()));
            stmt.setString(5, entry.getCategory());
            stmt.execute();
        } catch (SQLException e) {
            // do something ... not sure what, though
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Even less sure about what to do here
            }
        }
    }

    private static final String SQL_SELECT_ALL_ENTRY
        = "select t.id, t.username, t.title, t.message, t.date from messageTopic t, user_roles u where t.category = ? and u.ban = ? and t.username = u.username group by t.id, t.username, t.title, t.message, t.date";

@Override
public List<Message> findAllTopic(String category) {
    
    /*String SQL_SELECT_ALL_ENTRY
        = "select id, username, title, message, date from messageTopic where category = ?";*/
    
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = dataSource.getConnection();
        stmt = conn.prepareStatement(SQL_SELECT_ALL_ENTRY);
        stmt.setString(1, category);
        stmt.setString(2, "false");
        //stmt.setString(3, "ROLE_ADMIN");
        rs = stmt.executeQuery();
        List<Message> entries = new ArrayList<>();
        while (rs.next()) {
            Message entry = new Message();
            MessageUser entry2 = new MessageUser();
            entry.setId(rs.getInt("id"));
            entry.setCustomerName(rs.getString("username"));
            entry.setSubject(rs.getString("title"));
            entry.setBody(rs.getString("message"));
            entry.setDate(toDate(rs.getTimestamp("date")));
            entries.add(entry);
        }
        return entries;
    } catch (SQLException e) {
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
    }
    return null;
}
    
MessageReply messageReply = new MessageReply();    
int num =1;
private String SQL_SELECT_ALL_ENTRY2
        = "select r.id, r.username, r.message, r.date from messageReply r , user_roles u where r.id_topic = ? and u.ban = ? and r.username = u.username group by r.id, r.username, r.message, r.date" ;

@Override
public List<MessageReply> findAllReply(int id) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = dataSource.getConnection();
        stmt = conn.prepareStatement(SQL_SELECT_ALL_ENTRY2);
        stmt.setInt(1, id);
        stmt.setString(2, "false");
        rs = stmt.executeQuery();
        
        List<MessageReply> entries = new ArrayList<>();
        while (rs.next()) {
            MessageReply entry = new MessageReply();
            
            entry.setId(rs.getInt("id"));
            entry.setCustomerName(rs.getString("username"));
            entry.setBody(rs.getString("message"));
            //entry.setDate(toDate(rs.getTimestamp("date")));
            entries.add(entry);
        }
        return entries;
    } catch (SQLException e) {
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
    }
    return null;
}

private static final String SQL_SELECT_ENTRY
        = "select id, username, title, message, date from message where id = ?";

@Override
public Message findById(int id) {
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = dataSource.getConnection();
        stmt = conn.prepareStatement(SQL_SELECT_ENTRY);
        stmt.setInt(1, id);
        rs = stmt.executeQuery();
        Message entry = null;
        if (rs.next()) {
            entry = new Message();
            entry.setId(rs.getInt("id"));
            entry.setSubject(rs.getString("title"));
            entry.setBody(rs.getString("message"));
            entry.setDate(toDate(rs.getTimestamp("date")));
        }
        return entry;
    } catch (SQLException e) {
    } finally {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
    }
    return null;
}

private static final String SQL_UPDATE_ENTRY
        = "update message set title = ?, message = ?, date = ? where id = ?";

@Override
public void update(Message entry) {
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
        conn = dataSource.getConnection();
        stmt = conn.prepareStatement(SQL_UPDATE_ENTRY);
        stmt.setString(1, entry.getSubject());
        stmt.setString(2, entry.getBody());
        stmt.setTimestamp(3, new Timestamp(entry.getDate().getTime()));
        //stmt.setInt(4, entry.getId());
        stmt.execute();
    } catch (SQLException e) {
    } finally {
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
        }
    }
}

    public static Date toDate(Timestamp timestamp) {
        long milliseconds = timestamp.getTime() + (timestamp.getNanos() / 1000000);
        return new java.util.Date(milliseconds);
    }

    private static final String SQL_INSERT_ENTRY_REPLY
            = "insert into messageReply (username, message, id_topic, date) values (?,?, ?, ?)";
    
    @Override
    public void reply(int id, MessageReply entry, MessageUser user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(SQL_INSERT_ENTRY_REPLY);
            stmt.setString(1, entry.getCustomerName());
            stmt.setString(2, entry.getBody());
            stmt.setLong(3, id);
            stmt.setTimestamp(4, new Timestamp(entry.getDate().getTime()));
            stmt.execute();
        } catch (SQLException e) {
            // do something ... not sure what, though
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Even less sure about what to do here
            }
        }
    }

}
