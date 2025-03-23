import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    public void debitMoney(long accountNumber)throws  SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        while(true) {
            if (amount < 0) {
                System.out.println("Enter a valid amount!!");
                amount = scanner.nextDouble();
            } else {
                break;
            }
        }
        scanner.nextLine();
        System.out.print("Enter 4 digit Pin: ");
        String pin = scanner.nextLine();
        while (true){
            if(!isFourDigitNumber(pin)){
                System.out.print("Security pin should be of 4 digits: ");
                pin = scanner.nextLine();
            }
            else{
                break;
            }
        }
        try{
            connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND" +
                        "security_pint = ?");
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    double actualBalance = resultSet.getDouble("balance");
                    if(amount<=actualBalance){
                        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(debitQuery);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, accountNumber);
                        int rowsAffected = preparedStatement1.executeUpdate();
                        if(rowsAffected>0){
                            System.out.println("Rs "+amount+" debited successfully!!");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }
                        else{
                            System.out.println("Transaction Failed!!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }
                    else {
                        System.out.println("Insufficient Balance");
                    }
                }
                else{
                    System.out.println("Wrong Security Pin!!");
                }
        }catch (SQLException e){
            e.printStackTrace();
        }
    connection.setAutoCommit(true);
    }

    public void creditMoney(long accountNumber)throws  SQLException{
        scanner.nextLine();
        System.out.print("Enter Amount: ");
        double amount = scanner.nextDouble();
        while(true) {
            if (amount < 0) {
                System.out.println("Enter a valid amount!!");
                amount = scanner.nextDouble();
            } else {
                break;
            }
        }
        scanner.nextLine();
        System.out.print("Enter 4 digit Pin: ");
        String pin = scanner.nextLine();
        while (true){
            if(!isFourDigitNumber(pin)){
                System.out.print("Security pin should be of 4 digits: ");
                pin = scanner.nextLine();
            }
            else{
                break;
            }
        }
        try{
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE account_number = ? AND " +
                    "security_pin = ?");
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement preparedStatement1 = connection.prepareStatement(creditQuery);
                preparedStatement1.setDouble(1, amount);
                preparedStatement1.setLong(2, accountNumber);
                int rowsAffected = preparedStatement1.executeUpdate();
                if(rowsAffected>0){
                    System.out.println("Rs "+amount+" credited successfully!!");
                    connection.commit();
                    connection.setAutoCommit(true);
                }
                else{
                    System.out.println("Transaction Failed!!");
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
            }

            else{
                System.out.println("Wrong Security Pin!!");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);

    }

    public void transferMoney(long senderAccountNumber)throws SQLException{
        scanner.nextLine();
        System.out.print("Enter Receiver's account number: ");
        long receiverAccountNumber = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        while(true) {
            if (amount < 0) {
                System.out.println("Enter a valid amount!!");
                amount = scanner.nextDouble();
            } else {
                break;
            }
        }
        scanner.nextLine();
        System.out.print("Enter Security pin: ");
        String pin = scanner.nextLine();
        while (true){
            if(!isFourDigitNumber(pin)){
                System.out.print("Security pin should be of 4 digits: ");
                pin = scanner.nextLine();
            }
            else{
                break;
            }
        }
        try{
            connection.setAutoCommit(false);
            if(receiverAccountNumber>0 && receiverAccountNumber!=senderAccountNumber){
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts WHERE " +
                        "account_number = ? AND security_pin = ?");
                preparedStatement.setLong(1,senderAccountNumber);
                preparedStatement.setString(2,pin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    double currentbalance = resultSet.getDouble("balance");
                    if(amount<=currentbalance){
                        String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        String debitQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);
                        debitPreparedStatement.setDouble(1,amount);
                        debitPreparedStatement.setLong(2,senderAccountNumber);
                        creditPreparedStatement.setDouble(1,amount);
                        creditPreparedStatement.setLong(2, receiverAccountNumber);
                        int rowsAffected1 = debitPreparedStatement.executeUpdate();
                        int rowsAffected2 = creditPreparedStatement.executeUpdate();
                        if(rowsAffected1>0 && rowsAffected2>0){
                            System.out.println("Transaction Successful!!");
                            System.out.println("Rs"+amount+" transferred successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                        }
                        else{
                            System.out.println("Transaction failed!!");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    }
                    else {
                        System.out.println("Insufficient Balance");
                    }

                }
                else {
                    System.out.println("Invalid Pin");
                }
            }
            else {
                System.out.println("Invalid Account Number!!");
            }



        }
        catch (SQLException e){
            e.printStackTrace();
        }
        connection.setAutoCommit(true);

    }

    public void getBalance(long accountNumber){
        scanner.nextLine();
        System.out.print("Enter Security Pin: ");
        String pin = scanner.nextLine();
        while (true){
            if(!isFourDigitNumber(pin)){
                System.out.print("Security pin should be of 4 digits: ");
                pin = scanner.nextLine();
            }
            else{
                break;
            }
        }
        String balanceQuery = "SELECT balance FROM accounts WHERE account_number = ? AND security_pin = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(balanceQuery);
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, pin);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance for Account Number: "+accountNumber+" is Rs"+balance);
            }
            else {
                System.out.println("Wrong Security Pin!!");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
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
