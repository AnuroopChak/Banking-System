import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {
    private static final String url = "jdbc:mysql://localhost:3306/banking_system";
    private static final String username = "root";
    private static final String password = "Admin";

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url,username,password);
            Scanner sc = new Scanner(System.in);

            User user = new User(connection,sc);
            Accounts accounts = new Accounts(connection, sc);
            AccountManager accountManager = new AccountManager(connection, sc);

            String email;
            long accountNumber;

            while(true){
                System.out.println("!!WELCOME TO BANKING SYSTEM!!");
                System.out.println("=============================");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter Your Choice");
                int choice = sc.nextInt();
                switch (choice){
                    case 1:
                        user.register();
                        break;
                    case 2:
                        email=user.login();
                        if(email!=null){
                            System.out.println("User Logged In !");
                            if(!accounts.accountExist(email)){
                                System.out.println("1. Open a new account");
                                System.out.println("2. Exit");
                                if(sc.nextInt() == 1){
                                    accountNumber = accounts.openAcccount(email);
                                    System.out.println("Account created Successfully !!");
                                    System.out.println("Account Number: "+accountNumber);
                                }
                                else {
                                    break;
                                }
                            }
                            accountNumber = accounts.getAccountNumber(email);
                            int choice1 = 0;
                            while (choice1 != 5){
                                System.out.println("1: Credit Money");
                                System.out.println("2: Debit Money");
                                System.out.println("3: Transfer Money");
                                System.out.println("4: Check Balance");
                                System.out.println("5: Logout");
                                System.out.println("Enter Your Choice");
                                choice1 = sc.nextInt();
                                switch (choice1){
                                    case 1:
                                        accountManager.creditMoney(accountNumber);
                                        break;
                                    case 2:
                                        accountManager.debitMoney(accountNumber);
                                        break;
                                    case 3:
                                        accountManager.transferMoney(accountNumber);
                                        break;
                                    case 4:
                                        accountManager.getBalance(accountNumber);
                                        break;
                                    case 5:
                                        break;
                                    default:
                                        System.out.println("Enter Valid Choice!");
                                }
                            }
                        }
                        else {
                            System.out.println("Incorrect Email Id or Password");
                        }

                    case 3:
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM");
                        System.out.println("Exiting System");
                        return;
                    default:
                        System.out.println("Enter valid choice");
                        break;
                }
            }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}
