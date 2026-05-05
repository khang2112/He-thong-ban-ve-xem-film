package ui;

import dao.PhimDAO;
import dao.SuatChieuDAO;
import entity.Phim;
import entity.SuatChieu;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {
    private JTextField txtMaSuat;
    private JDateChooser txtNgay;
    private JSpinner spnGio;
    private JComboBox<String> cboPhim, cboPhong;
    private JButton btnThem, btnSua, btnXoa, btnXoaRong;
    private DefaultTableModel model;
    private JTable table;
    
    private SuatChieuDAO suatChieuDAO;
    private PhimDAO phimDAO;

    public PNL_SuatChieu() {
        suatChieuDAO = new SuatChieuDAO();
        phimDAO = new PhimDAO();
        
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(30, 30, 30));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ==========================================
        // 1. FORM NHẬP LIỆU (Đồng bộ PNL_KhachHang)
        // ==========================================
        JPanel pnlTop = new JPanel(new BorderLayout(0, 15));
        pnlTop.setOpaque(false);

        JPanel pnlInput = new JPanel(new GridLayout(3, 4, 20, 20));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 20, 60)), "THÔNG TIN SUẤT CHIẾU",
                TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(220, 20, 60)
        );
        
        JPanel pnlInputWrapper = new JPanel(new BorderLayout());
        pnlInputWrapper.setOpaque(false);
        pnlInputWrapper.setBorder(border);
        pnlInputWrapper.add(pnlInput, BorderLayout.CENTER);

        // Các thành phần nhập liệu
        pnlInput.add(createLabel("Mã Suất Chiếu:"));
        txtMaSuat = createTextField();
        pnlInput.add(txtMaSuat);

        pnlInput.add(createLabel("Chọn Phim:"));
        cboPhim = createComboBox();
        loadPhimToComboBox();
        pnlInput.add(cboPhim);

        pnlInput.add(createLabel("Phòng Chiếu:"));
        cboPhong = createComboBox();
        String[] phongData = {"Phòng 1 (2D)", "Phòng 2 (3D)", "Phòng 3 (2D)", "Phòng 4 (3D)", "VIP"};
        for(String p : phongData) cboPhong.addItem(p);
        pnlInput.add(cboPhong);

        pnlInput.add(createLabel("Ngày Chiếu:"));
        txtNgay = new JDateChooser();
        txtNgay.setDateFormatString("yyyy-MM-dd");
        txtNgay.getCalendarButton().setBackground(new Color(100, 100, 100));
        pnlInput.add(txtNgay);

        pnlInput.add(createLabel("Giờ Chiếu:"));
        spnGio = new JSpinner(new SpinnerDateModel());
        spnGio.setEditor(new JSpinner.DateEditor(spnGio, "HH:mm"));
        pnlInput.add(spnGio);

        pnlTop.add(pnlInputWrapper, BorderLayout.CENTER);

        // ==========================================
        // 2. NÚT CHỨC NĂNG (Flat UI)
        // ==========================================
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBtns.setOpaque(false);
        
        btnThem = createButton("Thêm", new Color(46, 204, 113));
        btnSua = createButton("Sửa", new Color(52, 152, 219));
        btnXoa = createButton("Xóa", new Color(231, 76, 60));
        btnXoaRong = createButton("Xóa Rỗng", new Color(127, 140, 141));

        pnlBtns.add(btnThem);
        pnlBtns.add(btnSua);
        pnlBtns.add(btnXoa);
        pnlBtns.add(btnXoaRong);

        pnlTop.add(pnlBtns, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // ==========================================
        // 3. BẢNG DỮ LIỆU (Custom Design)
        // ==========================================
        String[] cols = {"Mã Suất", "Tên Phim", "Phòng Chiếu", "Ngày Chiếu", "Giờ Chiếu"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(40, 40, 40)); 
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setSelectionBackground(new Color(52, 152, 219)); 
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(30, 30, 30));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        add(scrollPane, BorderLayout.CENTER);

        // Đăng ký sự kiện
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        table.addMouseListener(this);
        
        loadDataToTable();
    }

    // --- CÁC HÀM HỖ TRỢ GIAO DIỆN (Giữ nguyên style từ PNL_KhachHang) ---
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setForeground(new Color(220, 220, 220));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return lbl;
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBackground(new Color(50, 50, 50)); 
        txt.setForeground(Color.WHITE); 
        txt.setCaretColor(Color.WHITE); 
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100)), 
            new EmptyBorder(5, 10, 5, 10) 
        ));
        return txt;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        cbo.setBackground(new Color(50, 50, 50));
        cbo.setForeground(Color.WHITE);
        return cbo;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(120, 38)); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        return btn;
    }

    // --- LOGIC XỬ LÝ DỮ LIỆU ---
    private void loadPhimToComboBox() {
        ArrayList<Phim> list = phimDAO.docTuBang();
        cboPhim.removeAllItems();
        for (Phim p : list) {
            cboPhim.addItem(p.getMaPhim() + " - " + p.getTenPhim());
        }
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<SuatChieu> ds = suatChieuDAO.docTuBang();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

        for (SuatChieu s : ds) {
            model.addRow(new Object[]{
                s.getMaSuat(), s.getMaPhim(), s.getPhongChieu(),
                s.getNgayChieu() != null ? s.getNgayChieu().format(dtf) : "",
                s.getGioChieu() != null ? s.getGioChieu().format(tf) : ""
            });
        }
    }

    private SuatChieu getEntityFromForm() {
        String ma = txtMaSuat.getText().trim();
        String phim = cboPhim.getSelectedItem().toString().split(" - ")[0];
        String phong = cboPhong.getSelectedItem().toString();
        
        LocalDate ngay = txtNgay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime gio = ((java.util.Date) spnGio.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

        return new SuatChieu(ma, phim, phong, ngay, gio);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnXoaRong) {
            txtMaSuat.setText("");
            txtMaSuat.setEditable(true);
            txtNgay.setDate(null);
            table.clearSelection();
        } else if (o == btnThem) {
            if(txtMaSuat.getText().isEmpty()) return;
            if (suatChieuDAO.themSuatChieu(getEntityFromForm())) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Thêm suất chiếu thành công!");
            }
        } else if (o == btnXoa) {
            int row = table.getSelectedRow();
            if (row != -1) {
                String ma = model.getValueAt(row, 0).toString();
                if (suatChieuDAO.xoaSuatChieu(ma)) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(this, "Đã xóa!");
                }
            }
        } else if (o == btnSua) {
            if (suatChieuDAO.suaSuatChieu(getEntityFromForm())) {
                loadDataToTable();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.getSelectedRow();
        if (row != -1) {
            txtMaSuat.setText(model.getValueAt(row, 0).toString());
            txtMaSuat.setEditable(false);
            // Các xử lý set giá trị cho ComboBox và Date/Spinner tùy thuộc vào dữ liệu từ DAO
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}