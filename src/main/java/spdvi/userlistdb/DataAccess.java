/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spdvi.userlistdb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author angel
 */
public class DataAccess {
    
    private Connection getConnection(){
        Connection connection = null;
        Properties properties = new Properties();
        
        try{
            properties.load(DataAccess.class.getClassLoader().getResourceAsStream("application.properties"));
            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
        }catch(IOException ioe){
            ioe.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    } 
    public ArrayList<User> getUsers() {
        //obtener conection
        //crear un prepared statement
        //execute / query
        ArrayList<User> users = new ArrayList<User>();
         try(Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(
                    "Select * FROM dbo.[User]"
            );
            ResultSet resultSet = selectStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                    //resultSet.getInt ("id"),
                    resultSet.getString ("firstName"),
                    resultSet.getString ("lastName"),
                    LocalDate.parse(resultSet.getString("birthDate")),
                    resultSet.getString("gender"),
                    resultSet.getBoolean("alive"),
                    null
                );
                user.setId(resultSet.getInt("id"));
                users.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return users;
    }
    
    public int insertUser(User u){
        try(Connection connection = getConnection()){
            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO dbo.[User] (firstName, lastName, birthDate, gender, alive, profilePicture)"
                        + "VALUES (?,?,?,?,?,?)"
            );
            insertStatement.setString(1, u.getFirstName());
            insertStatement.setString(2, u.getLastName());
            insertStatement.setString(3, u.getBirthDate().toString());
            insertStatement.setString(4, u.getGender());
            insertStatement.setBoolean(5, u.isIsAlive());
            insertStatement.setString(6, null);
            
            int result = insertStatement.executeUpdate();
            if (result > 0){
                PreparedStatement selectStatement = connection.prepareStatement(
                        "Select MAx(id) as newId FROM dbo.[User]");
                ResultSet resultSet = selectStatement.executeQuery();
                if(!resultSet.next()){
                    return 0;
                }
                return resultSet.getInt("newId");
            }
            return result;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
