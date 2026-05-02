package ui;

import dao.PhimDAO;
import dao.SuatChieuDAO;
import entity.KhachHang;
import entity.Phim;
import entity.SuatChieu;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;

public class PNL_SuatChieu extends JPanel implements ActionListener, MouseListener {
	private JTextField txtMaSuat;
	private JDateChooser txtNgay;
	private JSpinner spnGio;
	private JComboBox<String> cboPhim, cboPhong; // Combo box chọn phim, phòng
	private JButton btnThem, btnSua, btnXoaRong, btnXoa;
	private DefaultTableModel model;
	private JTable table;

	private SuatChieuDAO suatChieuDAO;
	private PhimDAO phimDAO;

	public PNL_SuatChieu() {
		suatChieuDAO = new SuatChieuDAO();
		phimDAO = new PhimDAO();

		setLayout(new BorderLayout(10, 10));
		setBackground(new Color(30, 30, 30));
		setBorder(new EmptyBorder(10, 10, 10, 10));

		// --- FORM NHẬP LIỆU ---
		JPanel pnlTop = new JPanel(new BorderLayout(0, 10));
		pnlTop.setOpaque(false);

		JPanel pnlInput = new JPanel(new GridLayout(3, 4, 15, 15));
		pnlInput.setOpaque(false);
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
				"THÔNG TIN SUẤT CHIẾU");
		border.setTitleColor(Color.ORANGE);
		pnlInput.setBorder(border);

		pnlInput.add(createLabel("Mã Suất:"));
		txtMaSuat = new JTextField();
		pnlInput.add(txtMaSuat);

		pnlInput.add(createLabel("Chọn Phim:"));
		cboPhim = new JComboBox<>();
		loadPhimToComboBox(); // Nạp dữ liệu phim vào ComboBox
		pnlInput.add(cboPhim);

		pnlInput.add(createLabel("Phòng Chiếu:"));
		String[] danhSachPhong = { "Phòng 1 (2D)", "Phòng 2 (3D)", "Phòng 3 (2D)", "Phòng 4 (3D)", "Phòng 5 (VIP)" };
		cboPhong = new JComboBox<>(danhSachPhong);
		pnlInput.add(cboPhong);

		pnlInput.add(createLabel("Ngày (YYYY-MM-DD):"));
		txtNgay = new JDateChooser();
		txtNgay.setDateFormatString("yyyy-MM-dd");
		pnlInput.add(txtNgay);

		pnlInput.add(createLabel("Giờ (HH:MM):"));
		SpinnerDateModel timeModel = new SpinnerDateModel();
		spnGio = new JSpinner(timeModel);
		JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGio, "HH:mm");
		spnGio.setEditor(timeEditor);
		spnGio.setValue(java.sql.Time.valueOf("00:00:00"));
		pnlInput.add(spnGio);

		pnlTop.add(pnlInput, BorderLayout.CENTER);

		// --- NÚT BẤM ---
		JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		pnlBtns.setOpaque(false);
		btnThem = new JButton("Thêm Suất");
		btnSua = new JButton("Sửa");
		btnXoaRong = new JButton("Xóa Rỗng");
		btnXoa = new JButton("Xóa");
		pnlBtns.add(btnThem);
		pnlBtns.add(btnSua);
		pnlBtns.add(btnXoaRong);
		pnlBtns.add(btnXoa);
		pnlTop.add(pnlBtns, BorderLayout.SOUTH);
		add(pnlTop, BorderLayout.NORTH);

		// --- BẢNG DỮ LIỆU ---
		String[] cols = { "Mã Suất", "Mã Phim", "Phòng", "Ngày", "Giờ" };
		model = new DefaultTableModel(cols, 0);
		table = new JTable(model);
		add(new JScrollPane(table), BorderLayout.CENTER);

		btnThem.addActionListener(this);
		btnSua.addActionListener(this);
		btnXoaRong.addActionListener(this);
		btnXoa.addActionListener(this);
		table.addMouseListener(this);

		loadDataToTable();
	}

	private JLabel createLabel(String text) {
		JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
		lbl.setForeground(Color.WHITE);
		return lbl;
	}

	// Hàm load tên phim từ Database ném vào JComboBox
	private void loadPhimToComboBox() {
		ArrayList<Phim> dsPhim = phimDAO.docTuBang();
		for (Phim p : dsPhim) {
			// Định dạng: "MãPhim - Tên Phim" để dễ nhìn
			cboPhim.addItem(p.getMaPhim() + " - " + p.getTenPhim());
		}
	}

	private void loadDataToTable() {
		model.setRowCount(0);
		ArrayList<SuatChieu> ds = suatChieuDAO.docTuBang();

		DateTimeFormatter dtfNgay = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter dtfGio = DateTimeFormatter.ofPattern("HH:mm");

		for (SuatChieu s : ds) {
			String hienThiNgay = (s.getNgayChieu() != null) ? s.getNgayChieu().format(dtfNgay) : "";
			String hienThiGio = (s.getGioChieu() != null) ? s.getGioChieu().format(dtfGio) : "";

			model.addRow(new Object[] { s.getMaSuat(), s.getMaPhim(), s.getPhongChieu(), hienThiNgay, hienThiGio });
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnThem) {
			if (validData()) {
				SuatChieu s = revertSCFromTextField();
				if (suatChieuDAO.themSuatChieu(s)) {
					loadDataToTable();
					JOptionPane.showMessageDialog(this, "Thêm thành công!");
				} else {
					JOptionPane.showMessageDialog(this, "Trùng mã hoặc lỗi dữ liệu!");
				}
			}
		} else if (e.getSource() == btnXoaRong) {
			txtMaSuat.setText("");
			txtMaSuat.setEditable(true);
			cboPhong.setSelectedIndex(0);
			txtNgay.setDate(null);
			spnGio.setValue(java.sql.Time.valueOf("00:00:00"));

		} else if (e.getSource() == btnXoa) {
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu cần xóa từ bảng!", "Nhắc nhở",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa suất chiếu này?", "Xác nhận",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				String ma = model.getValueAt(row, 0).toString();
				if (suatChieuDAO.xoaSuatChieu(ma)) {
					loadDataToTable();
					JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
				} else {
					JOptionPane.showMessageDialog(this,
							"Xóa thất bại! Suất chiếu này có thể đang tồn tại trong hóa đơn.", "Lỗi CSDL",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == btnSua) {
			int row = table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(this, "Vui lòng chọn suất chiếu cần sửa thông tin từ bảng!", "Nhắc nhở",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (!validData())
				return;

			SuatChieu s = revertSCFromTextField();
			if (suatChieuDAO.suaSuatChieu(s)) {
				loadDataToTable();
				JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thông báo",
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private SuatChieu revertSCFromTextField() {
		String maS = txtMaSuat.getText().trim();
		String maP = cboPhim.getSelectedItem().toString().split(" - ")[0];
		String phong = cboPhong.getSelectedItem().toString();

		java.util.Date dateNgay = txtNgay.getDate();
		java.util.Date dateGio = (java.util.Date) spnGio.getValue();
		LocalDate ngay = dateNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalTime gio = dateGio.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
		return new SuatChieu(maS, maP, phong, ngay, gio);
	}

	private boolean validData() {
		String maS = txtMaSuat.getText().trim();
		String maP = cboPhim.getSelectedItem().toString().split(" - ")[0];
		String phong = cboPhong.getSelectedItem().toString();

		java.util.Date ngay = txtNgay.getDate();
		java.util.Date gio = (java.util.Date) spnGio.getValue();

		if (!(maS.length() > 0 && maS.matches("^S[0-9]{3}$"))) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập mã suất chiếu đúng định dạng. VD: S001");
			txtMaSuat.requestFocus();
			return false;
		}

		if (ngay == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày chiếu");
			txtNgay.requestFocus();
			return false;
		}

		LocalDate ngayChon = ngay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalTime gioChon = gio.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();

		LocalDate ngayHT = LocalDate.now();
		LocalTime gioHT = LocalTime.now();

		if (ngayChon.isBefore(ngayHT)) {
			JOptionPane.showMessageDialog(this, "Ngày chiếu không được ở quá khứ");
			txtNgay.requestFocus();
			return false;
		} else if (ngayChon.equals(ngayHT)) {
			if (gioChon.isBefore(gioHT)) {
				JOptionPane.showMessageDialog(this, "Suất chiếu phải diễn ra sau giờ hiện tại");
				spnGio.requestFocus();
				return false;
			}
		}
		return true;
	}

	// (Bỏ trống các hàm MouseListener cho gọn code nhé)
	@Override
	public void mouseClicked(MouseEvent e) {
		int row = table.getSelectedRow();
		if (row != -1) {
			txtMaSuat.setText(model.getValueAt(row, 0).toString());
			txtMaSuat.setEditable(false);

			// Xử lý ComboBox Phim (Dò tìm Item bắt đầu bằng Mã Phim trong bảng)
			String maPhimTable = model.getValueAt(row, 1).toString();
			for (int i = 0; i < cboPhim.getItemCount(); i++) {
				String itemPhim = cboPhim.getItemAt(i);
				if (itemPhim.startsWith(maPhimTable)) {
					cboPhim.setSelectedIndex(i);
					break;
				}
			}

			cboPhong.setSelectedItem(model.getValueAt(row, 2).toString());

			// Xử lý Ngày (Dùng thẳng LocalDate cực gọn, bỏ luôn try-catch)
			String ngayStr = model.getValueAt(row, 3).toString();
			if (!ngayStr.isEmpty()) {
				txtNgay.setDate(java.sql.Date.valueOf(LocalDate.parse(ngayStr)));
			}

			// Xử lý Giờ (Dùng thẳng LocalTime, bỏ luôn try-catch)
			String gioStr = model.getValueAt(row, 4).toString();
			if (!gioStr.isEmpty()) {
				spnGio.setValue(java.sql.Time.valueOf(LocalTime.parse(gioStr)));
			}
		}
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Overridepublic void mouseEntered(MouseEvent e) {}
	@Overridepublic void mouseExited(MouseEvent e) {}
}
