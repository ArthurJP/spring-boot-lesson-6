package com.zjp.springbootlesson6;

import com.zjp.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class JdbcController {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/user/get")
    public Map<String, Object> getUser(@RequestParam(value = "id", defaultValue = "1") int id) {
        Map<String, Object> data = new HashMap<>();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement( "select * from user where id=?" );
            statement.setInt( 1, id );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int id_ = resultSet.getInt( "id" );
                String name = resultSet.getString( "name" );
                String pass = resultSet.getString( "pass" );
                data.put( "id", id_ );
                data.put( "name", name );
                data.put( "pass", pass );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    @PostMapping("/user/add")
    @ResponseBody
    public Map<String, Object> getUser(@RequestBody User user) {
        Map<String, Object> data = new HashMap<>();
        Boolean result = jdbcTemplate.execute( "INSERT into user (`name`,pass) values (?,?)", new PreparedStatementCallback<Boolean>() {
            @Override
            public Boolean doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
                preparedStatement.setString( 1, user.getName() );
                preparedStatement.setString( 2, user.getPass() );
                return preparedStatement.executeUpdate() > 0;
            }
        } );
        data.put( "result",result );
        return data;
    }
}
