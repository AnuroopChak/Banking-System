import java.sql.*;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner scanner;

    public User(Connection connection, Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    public void register(){
        scanner.nextLine();
        System.out.print("Full name:");
        String full_name = scanner.nextLine();
        System.out.print("Email Id:");
        String email = scanner.nextLine();
        while(true){
            if(!email.contains("@") && !email.contains(".")){
                System.out.println("Enter a valid email. Email Id should contain '@' and '.'.");
                email=scanner.nextLine();
            }
            else{
                break;
            }
        }
        System.out.println("Password must meet the following criteria:\n" +
                "1. It must be between 8 to 12 characters long.\n" +
                "2. It must contain at least one uppercase letter.\n" +
                "3. It must contain at least one lowercase letter.\n" +
                "4. It must contain at least one special character (non-alphanumeric).");
        System.out.print("Password:  ");
        String password = scanner.nextLine();
        while(true){
            if(!isValidPassword(password)){
                System.out.println("Password does not meet the following criteria:\n" +
                        "1. It must be between 8 to 12 characters long.\n" +
                        "2. It must contain at least one uppercase letter.\n" +
                        "3. It must contain at least one lowercase letter.\n" +
                        "4. It must contain at least one special character (non-alphanumeric).");
                System.out.print("Password:  ");
                password = scanner.nextLine();
            }
            else{
                break;
            }
        }
        if(userExists(email)){
            System.out.println("User already exists for this email !!");
        }

        String registerQuery = "INSERT INTO user(full_name, email, password) VALUES(?,?,?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(registerQuery);
            preparedStatement.setString(1,full_name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows>0){
                System.out.println("Registration Successfull!!");
            }
            else{
                System.out.println("Registration Failed!!");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public String login(){
        scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        while(true){
            if(!email.contains("@") && !email.contains(".")){
                System.out.println("Enter a valid email. Email Id should contain '@' and '.'.");
                email=scanner.nextLine();
            }
            else{
                break;
            }
        }
        System.out.print("Password:  ");
        String password = scanner.nextLine();
//        while(true){
//            if(!isValidPassword(password)){
//                System.out.println("Password does not meet the following criteria:\n" +
//                        "1. It must be between 8 to 12 characters long.\n" +
//                        "2. It must contain at least one uppercase letter.\n" +
//                        "3. It must contain at least one lowercase letter.\n" +
//                        "4. It must contain at least one special character (non-alphanumeric).");
//                System.out.print("Password:  ");
//                password = scanner.nextLine();
//            }
//            else{
//                break;
//            }
//        }

        String loginQuery = "SELECT * FROM user WHERE email = ? AND password = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return  email;
            }
            else{
                return  null;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        return null;

    }

    public boolean userExists(String email){
        String userExistQuery = "SELECT * FROM user WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(userExistQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else {
                return false;
            }
        }
        catch (SQLException e){
            e.printStackTrace();;
        }
        return false;
    }

    public boolean isValidPassword(String password) {
        if (password.length() < 8 || password.length() > 12) {
            return false;
        }

        boolean hasUpper = false, hasLower = false, hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            } else if (Character.isLowerCase(ch)) {
                hasLower = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasSpecial;
    }

}
