import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        DataSource dataSource = context.getBean(DataSource.class);
        JdbcTemplate template = new JdbcTemplate(dataSource);


        template.execute("CREATE TABLE Customer(id INTEGER, name VARCHAR(100) )");
        template.execute((ConnectionCallback<Object>)con -> {
            System.out.println(con.getMetaData().getURL());
            System.out.println(con.getMetaData().getUserName());
            return null;
        });


        template.update("INSERT INTO Customer VALUES (0,'Name1')");
        Boolean done = template.execute((StatementCallback<Boolean>) sc -> {
            sc.execute("INSERT INTO Customer VALUES(2,'Name2')");
            return Boolean.TRUE;
        });


        template.execute((PreparedStatementCreator)psc -> psc.prepareStatement("INSERT INTO Customer VALUES (?,?)"), (PreparedStatementCallback<Object>)ps -> {
            ps.setInt(1,3);
            ps.setString(2,"Name3");
            ps.execute();
            return null;
        });


        template.query("SELECT * FROM Customer", (RowCallbackHandler) rs -> {
            System.out.println(rs.getString("name"));
        });
        System.out.println(done);


        NamedParameterJdbcTemplate parameterJdbcTemplate = new NamedParameterJdbcTemplate(template);
        Map<String, Object> params = new HashMap<>();
        params.put("id",4);
        params.put("name","Name4");


        parameterJdbcTemplate.update("INSERT INTO Customer VALUES (:id,:name)",params);


        template.query("SELECT name,id FROM Customer",(RowCallbackHandler)rs-> System.out.println(rs.getString("name") + rs.getString("id")));



    }
}
