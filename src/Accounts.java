import java.sql.*;
import java.util.Scanner;

class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    public long openAcccount(String email){
        if(!accountExist(email)){
            scanner.nextLine();
            System.out.print("Full Name: ");
            String fullName = scanner.nextLine();
            System.out.println("Initial Balance: ");
            double balance = scanner.nextDouble();
            while(true){
                if(balance < 0){
                    System.out.println("Enter a valid amount!!");
                    balance = scanner.nextDouble();
                }
                else{
                    break;
                }
            }
            scanner.nextLine();
            System.out.println("4 digit Security Pin: ");
            String pin = scanner.nextLine();
            while (true){
                if(!isFourDigitNumber(pin)){
                    System.out.print("Enter valid 4 digit Security Pin: ");
                    pin = scanner.nextLine();
                }
                else{
                    break;
                }
            }
            String accountQuery = "INSERT INTO accounts(account_number, full_name, email, balance, security_pin)" +
                    " VALUES (?,?,?,?,?)";
            try{
                long accountNumber = generateAcccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(accountQuery);
                preparedStatement.setLong(1,accountNumber);
                preparedStatement.setString(2, fullName);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, balance);
                preparedStatement.setString(5, pin);
                int rowsAffected = preparedStatement.executeUpdate();
                if(rowsAffected>0){
                    return accountNumber;
                }
                else{
                    throw new RuntimeException("Account creation failed!!");
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }


        }
        throw new RuntimeException("Account already exists!!");
    }
    public long generateAcccountNumber() {
        String query = "SELECT account_number FROM accounts ORDER BY account_number DESC";
        try{
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                long previousAccountNumber = resultSet.getLong("account_number");
                return previousAccountNumber+1;
            }
            else{
                return 10000100;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 10000100;
    }

    public long getAccountNumber(String email){
        String accountNumberQuery = "SELECT account_number FROM accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(accountNumberQuery);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return resultSet.getLong("account_number");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        throw new RuntimeException("Account number does not exists!!");
    }

    public boolean accountExist(String email) {
        String query = "SELECT * FROM accounts WHERE email = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }
            else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean isFourDigitNumber(String str) {
        if (str.length() != 4) return false;
        try {
            int num = Integer.parseInt(str);
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
