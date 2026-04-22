package ui;

import dao.PhimDAO;
import entity.Phim;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UI_DatVePhim extends JFrame implements ActionListener, MouseListener {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtMaPhim, txtTenPhim, txtSoLuong;
    private JButton btnDatVe, btnXoaRong;
    private PhimDAO phimDAO;

    public UI_DatVePhim() {
        phimDAO = new PhimDAO();
        
        setTitle("Quản lý đặt vé xem phim");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel pnlMain = new JPanel(new BorderLayout());
        add(pnlMain);

        JLabel lblTitle = new JLabel("HỆ THỐNG ĐẶT VÉ XEM PHIM", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(Color.RED);
        pnlMain.add(lblTitle, BorderLayout.NORTH);

        // Form nhập liệu dùng BoxLayout
        JPanel pnlForm = new JPanel(new BorderLayout());
        Box b = Box.createVerticalBox();
        Box b1 = Box.createHorizontalBox();
        Box b2 = Box.createHorizontalBox();
        Box b3 = Box.createHorizontalBox();

        txtMaPhim = new JTextField();
        txtMaPhim.setEditable(false); // Chỉ chọn từ bảng
        txtTenPhim = new JTextField();
        txtTenPhim.setEditable(false);
        txtSoLuong = new JTextField();

        b1.add(new JLabel("Mã Phim: ")); b1.add(Box.createHorizontalStrut(10)); b1.add(txtMaPhim);
        b1.add(Box.createHorizontalStrut(20));
        b1.add(new JLabel("Tên Phim: ")); b1.add(Box.createHorizontalStrut(10)); b1.add(txtTenPhim);
        
        b2.add(new JLabel("Số lượng vé: ")); b2.add(Box.createHorizontalStrut(10)); b2.add(txtSoLuong);
        
        b.add(b1); b.add(Box.createVerticalStrut(10));
        b.add(b2); b.add(Box.createVerticalStrut(10));
        
        pnlForm.add(b, BorderLayout.CENTER);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnDatVe = new JButton("Đặt Vé");
        btnXoaRong = new JButton("Xóa Rỗng");
        pnlButtons.add(btnDatVe);
        pnlButtons.add(btnXoaRong);
        pnlForm.add(pnlButtons, BorderLayout.SOUTH);

        pnlMain.add(pnlForm, BorderLayout.CENTER);

        // Bảng dữ liệu
        String[] cols = {"Mã Phim", "Tên Phim", "Thể Loại", "Giá Vé"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(800, 300));
        pnlMain.add(scroll, BorderLayout.SOUTH);

        // Events
        table.addMouseListener(this);
        btnDatVe.addActionListener(this);
        btnXoaRong.addActionListener(this);

        loadDataToTable();
    }

    private void loadDataToTable() {
        ArrayList<Phim> list = phimDAO.docTuBang();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        for (Phim p : list) {
            model.addRow(new Object[]{
                p.getMaPhim(), p.getTenPhim(), p.getTheLoai(), nf.format(p.getGiaVe())
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnXoaRong) {
            txtMaPhim.setText("");
            txtTenPhim.setText("");
            txtSoLuong.setText("");
            table.clearSelection();
        } else if (o == btnDatVe) {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phim muốn đặt!");
                return;
            }
            try {
                int soLuong = Integer.parseInt(txtSoLuong.getText());
                if (soLuong <= 0) throw new Exception();
                
                String giaStr = model.getValueAt(row, 3).toString().replaceAll("[^0-9]", "");
                double giaVe = Double.parseDouble(giaStr);
                double tongTien = giaVe * soLuong;
                
                NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                JOptionPane.showMessageDialog(this, "Đặt thành công " + soLuong + " vé phim " 
                        + txtTenPhim.getText() + ".\nTổng tiền: " + nf.format(tongTien));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Số lượng vé phải là số nguyên dương!");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaPhim.setText(model.getValueAt(row, 0).toString());
            txtTenPhim.setText(model.getValueAt(row, 1).toString());
        }
    }
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}