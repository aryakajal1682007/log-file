import java.sql.*;

import static java.lang.Class.forName;

public class jdbc {
    private static final String url=" jdbc:mysql://localhost:3306/arya";
    private static final String username=" arya";
    private static final String password="";


    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Statement statement=connection.createStatement();
            String query ="select*from student";
            ResultSet resultset=statement.executeQuery(query);
            while(resultset.next()){
                int id=resultset.getInt("id");
                String name=resultset.getString("name");
                int age = resultset.getInt("age");
                int marks=resultset.getInt("marks");


                System.out.println("id: "+id);
                System.out.println("name: "+name);
                System.out.println("age: "+age);
                System.out.println("marks: "+marks);
            }

        }catch(SQLException e ){
            System.out.println(e.getMessage());
        }


    }


}