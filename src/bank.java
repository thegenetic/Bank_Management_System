import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class bank extends JFrame{
    private JPanel b;
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JTextField textField5;
    private JTextField textField6;
    private JTextField textField7;
    private JTextField textField8;
    private JTable table1;
    private JButton SUBMITButton;
    private JButton DEPOSITButton;
    private JButton WITHDRAWButton;
    private JButton CHECKBALANCEButton;


    Integer dep_cash = 0;
    Integer wd_cash = 0;
    Integer acc = 0;
    Integer cash = 0;


    private Connection getConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bank management", "root", "");
            return con;
        }
        catch (SQLException |ClassNotFoundException e){
            return null;
        }
    }

    public bank(){
        setContentPane(b);
        setTitle("Bank Management System");
        setSize(700, 750);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        show_details();

        Connection con = getConnection();

        SUBMITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String f_name = textField1.getText();
                String l_name = textField2.getText();
                String address = textField3.getText();
                String pin = textField4.getText();
                String acc = textField5.getText();
                String mobile = textField6.getText();
                String email = textField7.getText();


                if(pin.length()<=5 && acc.length()<=11 && mobile.length()<=10) {

                    input_details(f_name, l_name, address, pin, acc, mobile, email);
                    textField1.setText("");
                    textField2.setText("");
                    textField3.setText("");
                    textField4.setText("");
                    textField5.setText("");
                    textField6.setText("");
                    textField7.setText("");
                }
                else {
                try {
                    if(pin.length()>5){
                        textField4.setText("");
                        JOptionPane.showMessageDialog(null,"Length of the pin field is extended");

                    }
                    else if(acc.length()>11){
                        textField5.setText("");
                        JOptionPane.showMessageDialog(null,"Length of the account number field is extended");

                    }
                    else if(mobile.length()>10){
                        textField6.setText("");
                        JOptionPane.showMessageDialog(null,"Length of the mobile number field is extended");

                    }
                }
                catch (Exception e1){
                    JOptionPane.showMessageDialog(null,"Please re-enter the values");
                }
                }


            }
        });

        DEPOSITButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

//                cash = Integer.valueOf(textField8.getText());

                acc = Integer.valueOf(textField5.getText());
                dep_cash = Integer.valueOf(textField8.getText());
                Integer total_cash = dep_cash+cash;

                deposit_money(acc, total_cash);
                dep_cash = 0;
            }
        });

        WITHDRAWButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                acc = Integer.valueOf(textField5.getText());
                wd_cash = Integer.valueOf(textField8.getText());
                Integer tot_cash = cash-wd_cash;

                if (tot_cash>=0) {
                    withdraw_money(acc, tot_cash);
                    wd_cash = 0;
                }

                else
                    JOptionPane.showMessageDialog(null, "You don't have enough balance.");
                    textField8.setText("");
            }
        });

        CHECKBALANCEButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                check_balance( cash, acc);
            }
        });
        
    }

    private void show_details(){
        Connection con = getConnection();

        if(con != null){
            String[] column = {"First Name", "Last name", "address", "Pin", "AccountNo.", "MobileNo.", "EmailID", "balance"};
//            String[] column = {"First Name", "Last name", "address", "Pin", "AccountNo.", "MobileNo.", "EmailID","balance"};

            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(column);


        try {
            String sql = "SELECT * FROM tbl_bank";

            PreparedStatement st = null;
            st = con.prepareStatement(sql);

            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {

                Object[] ob = new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7),rs.getString(8)};
//                Object[] ob = new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)};
                model.addRow(ob);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        table1.setModel(model);

            table1.getColumnModel().getColumn(4).setMinWidth(0);
            table1.getColumnModel().getColumn(4).setMaxWidth(0);
            table1.getColumnModel().getColumn(7).setMinWidth(0);
            table1.getColumnModel().getColumn(7).setMaxWidth(0);

            table1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int i = table1.getSelectedRow();
                    TableModel model = table1.getModel();

                    acc = Integer.parseInt(model.getValueAt(i, 4).toString());


                    textField1.setText(model.getValueAt(i, 0).toString());
                    textField2.setText(model.getValueAt(i, 1).toString());
                    textField3.setText(model.getValueAt(i, 2).toString());
                    textField4.setText(model.getValueAt(i, 3).toString());
                    textField5.setText(model.getValueAt(i,4).toString());
                    textField6.setText(model.getValueAt(i, 5).toString());
                    textField7.setText(model.getValueAt(i, 6).toString());
                    textField8.setText(model.getValueAt(i, 7).toString());

                    cash = Integer.parseInt(model.getValueAt(i,7).toString());

                    textField8.setText("");

                }
            });

        }

        else {
            JOptionPane.showMessageDialog(null, "Connection Error");
        }

    }

    private void input_details(String f_name,String l_name,String address,String pin,String acc,String mobile,String email){
        Connection con = getConnection();

        if (con != null){
            try{
                String sql = "INSERT INTO `tbl_bank`(`First Name`, `Last name`, `address`, `Pin`, `AccountNo.`, `MobileNo.`, `EmailID`) VALUES (?, ?, ?, ?, ?, ?, ?)";

                PreparedStatement st = null;
                st = con.prepareStatement(sql);
                st.setString(1,f_name);
                st.setString(2,l_name);
                st.setString(3, address);
                st.setString(4, pin);
                st.setString(5, acc);
                st.setString(6, mobile);
                st.setString(7, email);



                int rowCount = st.executeUpdate();

                if (rowCount!=0 ){
                    show_details();
                    JOptionPane.showMessageDialog(null, "data inserted");
                }else {
                    JOptionPane.showMessageDialog(null, "data not inserted");
                }
            }
            catch (SQLException ex){
                ex.printStackTrace();
            }
        }else {
            JOptionPane.showMessageDialog(null, "Connection error");
        }
    }

    private void deposit_money(Integer account, Integer c){

        Connection con = getConnection();
        if (con != null){
            try{
                String sql ="update tbl_bank set `balance`=? where `AccountNo.`=?";
                PreparedStatement st = null;
                st= con.prepareStatement(sql);
                st.setInt(1, c);
                st.setInt(2, account);

                int rowCount = st.executeUpdate();
                if(rowCount != 0){
                    show_details();
                    JOptionPane.showMessageDialog(null,"Hello "+textField1.getText()+" your account has been credited by Rs."+dep_cash);
                    textField8.setText("");
                }else {
                    JOptionPane.showMessageDialog(null, "Amount couldn't be deposited");
                }
            }catch (SQLException|ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"Connection error");
        }
    }

    private void withdraw_money(Integer account, Integer c){
        Connection con = getConnection();
        if (con != null){
            try{
                String sql ="update tbl_bank set `balance`=? where `AccountNo.`=?";
                PreparedStatement st = null;
                st= con.prepareStatement(sql);
                st.setInt(1, c);
                st.setInt(2, account);

                int rowCount = st.executeUpdate();
                if(rowCount != 0){
                    show_details();
                    JOptionPane.showMessageDialog(null,"Hello "+textField1.getText()+" your account has been debited by Rs."+wd_cash);
                    textField8.setText("");
                }else {
                    JOptionPane.showMessageDialog(null, "Amount couldn't be witdrawn");
                }
            }catch (SQLException|ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
        else{
            JOptionPane.showMessageDialog(null,"Connection error");
        }
    }

    private void check_balance(Integer cas, Integer ac){

        Connection con = getConnection();
        if(con != null){
            try{
                String sql = "SELECT balance=? FROM tbl_bank WHERE AccountNo.=?";
                PreparedStatement st = null;
                st= con.prepareStatement(sql);
                st.setInt(1,cas);
                st.setInt(2,ac);
                JOptionPane.showMessageDialog(null, "Hello " + textField1.getText()+ " your account has a balance of Rs." + cas);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Connection error");
        }
    }

    public static void main(String[] args) {
        new bank();
    }
}
