package com.hfjy.framework.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import org.slf4j.Logger;

import com.hfjy.framework.common.util.FileUtils;
import com.hfjy.framework.common.util.LocalResourcesUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.database.entity.DBType;
import com.hfjy.framework.init.ConfigDbUtil;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.view.entity.ExecuteRunnable;
import com.hfjy.framework.view.factory.SwingActivatorFactory;
import com.hfjy.framework.view.factory.SwingContainerFactory;

public class DBConnectionInfoPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel jlConfigPath, jlStatus;
	private JTable jtDbinfo;
	private Table tmDbInfo;
	private JScrollPane jspDbInfo;
	private JButton jbAddDbInfo, jbDelDbInfo, jbSaveDbInfo, jbClearDbInfo, jbBrowse, jbLoadDbInfo;
	private JPanel jpOperatingDbInfo, jpStatusBar, jpDisplay;
	private JFileChooser jfcBrowse;
	private JTextField jtfConfigPath;
	private JProgressBar jpbLoading;

	public DBConnectionInfoPanel() {
		this.setLayout(new BorderLayout());
		buildWin();
		loadEvent();
		this.setVisible(true);
	}

	private void buildWin() {
		jpbLoading = new JProgressBar(0, 100);
		jfcBrowse = new JFileChooser(Initial.DB_CONFIG_PATH);
		jlConfigPath = new JLabel("配置文件路径:");
		jlStatus = new JLabel("未操作");
		jlConfigPath.setFont(SwingContainerFactory.getFont());
		jlStatus.setFont(SwingContainerFactory.getFont());
		jlStatus.setForeground(Color.RED);
		jpOperatingDbInfo = new JPanel();
		jpStatusBar = new JPanel(new BorderLayout());
		jpDisplay = new JPanel(new BorderLayout());
		jbAddDbInfo = new JButton("添加");
		jbDelDbInfo = new JButton("删除");
		jbSaveDbInfo = new JButton("保存");
		jbBrowse = new JButton("浏览...");
		jbLoadDbInfo = new JButton("加载");
		jbClearDbInfo = new JButton("清空");
		jtfConfigPath = new JTextField(20);
		jbAddDbInfo.setFont(SwingContainerFactory.getFont());
		jbDelDbInfo.setFont(SwingContainerFactory.getFont());
		jbSaveDbInfo.setFont(SwingContainerFactory.getFont());
		jbBrowse.setFont(SwingContainerFactory.getFont());
		jbLoadDbInfo.setFont(SwingContainerFactory.getFont());
		jbClearDbInfo.setFont(SwingContainerFactory.getFont());
		jpOperatingDbInfo.add(jbAddDbInfo);
		jpOperatingDbInfo.add(jbDelDbInfo);
		jpOperatingDbInfo.add(jbClearDbInfo);
		jpOperatingDbInfo.add(jbSaveDbInfo);
		jpOperatingDbInfo.add(jlConfigPath);
		jpOperatingDbInfo.add(jtfConfigPath);
		jpOperatingDbInfo.add(jbBrowse);
		jpOperatingDbInfo.add(jbLoadDbInfo);
		jpStatusBar.add(jpbLoading, BorderLayout.CENTER);
		jpStatusBar.add(jlStatus, BorderLayout.WEST);
		tmDbInfo = new Table();
		jtDbinfo = new JTable(tmDbInfo);
		jtDbinfo.setRowHeight(20);
		jtDbinfo.setBackground(Color.white);
		jtDbinfo.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumnModel tcm = jtDbinfo.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			tcm.getColumn(i).setPreferredWidth(80);
		}
		tcm.getColumn(10).setPreferredWidth(150);
		tcm.getColumn(11).setPreferredWidth(300);
		tcm.getColumn(3).setCellEditor(new DefaultCellEditor(new JComboBox<DBType>(DBType.values())));
		jspDbInfo = new JScrollPane(jtDbinfo);
		jpDisplay.add(jspDbInfo, BorderLayout.CENTER);
		jpDisplay.add(jpOperatingDbInfo, BorderLayout.NORTH);
		this.add(jpDisplay, BorderLayout.CENTER);
		this.add(jpStatusBar, BorderLayout.SOUTH);
	}

	private void loadEvent() {
		jbAddDbInfo.addActionListener(this);
		jbDelDbInfo.addActionListener(this);
		jbSaveDbInfo.addActionListener(this);
		jbBrowse.addActionListener(this);
		jbLoadDbInfo.addActionListener(this);
		jbClearDbInfo.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		SwingActivatorFactory.run(new ExecuteRunnable(e) {
			@Override
			public void process() {
				Object tmp = eventObject.getSource();
				if (tmp == jbAddDbInfo) {
					addData();
				} else if (tmp == jbDelDbInfo) {
					removeData();
				} else if (tmp == jbSaveDbInfo) {
					saveData();
				} else if (tmp == jbBrowse) {
					browseFile();
				} else if (tmp == jbLoadDbInfo) {
					loadFile();
				} else if (tmp == jbClearDbInfo) {
					removeDatas();
				}
			}
		});
	}

	private void addData() {
		DBType dbType = (DBType) JOptionPane.showInputDialog(null, "请选择模版:", "数据库类型", JOptionPane.PLAIN_MESSAGE, null, DBType.values(), DBType.valueOf("MYSQL"));
		if (dbType == null) {
			return;
		}
		DBConnectionInfo dbInfo = ConfigDbUtil.init().getInitDBConnectionInfo(dbType);
		tmDbInfo.addRow(dbInfo);
		jtDbinfo.updateUI();
	}

	private void removeData() {
		tmDbInfo.removeRow(jtDbinfo.getSelectedRows());
		jtDbinfo.updateUI();
	}

	private void removeDatas() {
		tmDbInfo.removeRows();
		jtDbinfo.updateUI();
	}

	private void saveData() {
		String path = jtfConfigPath.getText();
		if (path == null || path.length() < 1) {
			JOptionPane.showMessageDialog(this, "文件目录不能为空！");
			return;
		}
		List<String> addList = new ArrayList<>();
		List<String> upList = new ArrayList<>();
		Properties config = LocalResourcesUtil.getProperties(path);
		for (int i = 0; i < tmDbInfo.getRowCount(); i++) {
			String code = tmDbInfo.getValueAt(i, 0).toString();
			if (config.containsKey(code)) {
				upList.add(config.getProperty(code));
				config.remove(code);
			} else {
				addList.add(StringUtils.unite(code, "DBConfig.properties"));
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtils.unite("本次\r\n新增:", addList, "\r\n"));
		sb.append(StringUtils.unite("修改:", upList, "\r\n"));
		sb.append(StringUtils.unite("删除:", config.values(), "\r\n"));
		int isOk = JOptionPane.showConfirmDialog(this, sb.toString(), "确认要保存吗？", JOptionPane.OK_CANCEL_OPTION);
		if (isOk == 0) {
			if (tmDbInfo.saveList(path)) {
				JOptionPane.showMessageDialog(this, "保存成功！");
			} else {
				JOptionPane.showMessageDialog(this, "保存失败。");
			}
		}
	}

	public void browseFile() {
		jfcBrowse.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int state = jfcBrowse.showOpenDialog(null);
		if (state == 1) {
			return;
		} else {
			jtfConfigPath.setText(jfcBrowse.getSelectedFile().getAbsolutePath());
		}
	}

	public void loadFile() {
		if (jtfConfigPath.getText().trim().length() < 1) {
			JOptionPane.showMessageDialog(this, "文件目录不能为空！");
			return;
		}
		new Thread() {
			public void run() {
				load();
			}
		}.start();
		Properties properties = LocalResourcesUtil.getProperties(jtfConfigPath.getText());
		Iterator<Object> dbsKeys = properties.keySet().iterator();
		String path = jtfConfigPath.getText();
		path = path.substring(0, path.lastIndexOf(File.separator) + 1);
		while (dbsKeys.hasNext()) {
			String key = dbsKeys.next().toString().trim();
			String value = properties.getProperty(key).trim();
			Properties dbConfig = LocalResourcesUtil.getProperties(StringUtils.unite(path, value));
			DBConnectionInfo dbinfo = new DBConnectionInfo(key, dbConfig);
			tmDbInfo.addRow(dbinfo);
		}
		jtDbinfo.updateUI();
		jpbLoading.setValue(100);
	}

	public void load() {
		jlStatus.setText("正在加载...\t");
		jpbLoading.setValue(1);
		while (true) {
			if (jpbLoading.getValue() == 99) {
				jpbLoading.setValue(1);
			}
			if (jpbLoading.getValue() == 100) {
				jlStatus.setText("加载完毕\t");
				break;
			} else if (jpbLoading.getValue() == 0) {
				jlStatus.setText("加载失败\t");
				break;
			}
			jpbLoading.setValue(jpbLoading.getValue() + 1);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}
}

class Table extends AbstractTableModel {
	private static final Logger logger = LoggerFactory.getLogger(DBConnectionInfoPanel.class);
	private static final long serialVersionUID = 1L;

	private List<DBConnectionInfo> dbConnectionInfoList = new ArrayList<>();;

	private String[] title = { "标识", "地址", "端口", "数据库类型", "数据库名称", "用户名", "密码", "连接测试用SQL", "最大连接数", "最小连接数", "驱动类", "连接字符串" };

	public void addRow(DBConnectionInfo dbConnectionInfo) {
		dbConnectionInfoList.add(dbConnectionInfo);
	}

	public void removeRow(int[] ids) {
		if (dbConnectionInfoList == null || dbConnectionInfoList.size() < 1) {
			return;
		}
		List<DBConnectionInfo> tmpList = new ArrayList<DBConnectionInfo>();
		for (int i = 0; i < ids.length; i++) {
			tmpList.add(dbConnectionInfoList.get(ids[i]));
		}
		dbConnectionInfoList.removeAll(tmpList);
	}

	public void removeRows() {
		dbConnectionInfoList.clear();
	}

	public boolean saveList(String path) {
		Properties properties = LocalResourcesUtil.getProperties(path);
		Iterator<Object> dbsKeys = properties.keySet().iterator();
		String dbsPath = path.substring(0, path.lastIndexOf(File.separator) + 1);
		while (dbsKeys.hasNext()) {
			String key = dbsKeys.next().toString().trim();
			String value = properties.getProperty(key).trim();
			FileUtils.deleteFile(StringUtils.unite(dbsPath, value));
		}
		for (int i = 0; i < dbConnectionInfoList.size(); i++) {
			DBConnectionInfo dbConnectionInfo = dbConnectionInfoList.get(i);
			String key = dbConnectionInfo.getDbCode();
			try {
				String name = StringUtils.unite(key, "DBConfig.properties");
				if (properties.get(key) != null) {
					name = properties.getProperty(key);
				}
				FileOutputStream fos = new FileOutputStream(StringUtils.unite(dbsPath, name));
				fos.write(dbConnectionInfo.toString().getBytes());
				fos.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return true;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 10 || columnIndex == 11) {
			return false;
		}
		return true;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		DBConnectionInfo tmp = dbConnectionInfoList.get(row);
		if (tmp == null || value == null) {
			return;
		}
		switch (col) {
		case 0:
			tmp.setDbCode(value.toString());
			break;
		case 1:
			tmp.setDbAddress(value.toString());
			break;
		case 2:
			tmp.setDbPort(value.toString());
			break;
		case 3:
			tmp.setDbType(DBType.valueOf(value.toString()));
			break;
		case 4:
			tmp.setDbName(value.toString());
			break;
		case 5:
			tmp.setDbUsername(value.toString());
			break;
		case 6:
			tmp.setDbPassword(value.toString());
			break;
		case 7:
			tmp.setDbTestSQL(value.toString());
			break;
		case 8:
			tmp.setMaxConnectionNum(Integer.valueOf(value.toString()));
			break;
		case 9:
			tmp.setMinConnectionNum(Integer.valueOf(value.toString()));
			break;
		}
		DBType dbtype = tmp.getDbType() == null ? DBType.MYSQL : tmp.getDbType();
		tmp.initConnectionString(ConfigDbUtil.init().getConnectionStringFormat(dbtype));
		for (int i = 0; i < title.length; i++) {
			this.fireTableCellUpdated(row, i);
		}
	}

	@Override
	public String getColumnName(int col) {
		return title[col];
	}

	@Override
	public int getColumnCount() {
		return title.length;
	}

	@Override
	public int getRowCount() {
		return dbConnectionInfoList.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		Object tmp = new Object();
		switch (col) {
		case 0:
			tmp = dbConnectionInfoList.get(row).getDbCode();
			break;
		case 1:
			tmp = dbConnectionInfoList.get(row).getDbAddress();
			break;
		case 2:
			tmp = dbConnectionInfoList.get(row).getDbPort();
			break;
		case 3:
			tmp = dbConnectionInfoList.get(row).getDbType();
			break;
		case 4:
			tmp = dbConnectionInfoList.get(row).getDbName();
			break;
		case 5:
			tmp = dbConnectionInfoList.get(row).getDbUsername();
			break;
		case 6:
			tmp = dbConnectionInfoList.get(row).getDbPassword();
			break;
		case 7:
			tmp = dbConnectionInfoList.get(row).getDbTestSQL();
			break;
		case 8:
			tmp = dbConnectionInfoList.get(row).getMaxConnectionNum();
			break;
		case 9:
			tmp = dbConnectionInfoList.get(row).getMinConnectionNum();
			break;
		case 10:
			tmp = dbConnectionInfoList.get(row).getDriveClass();
			break;
		case 11:
			tmp = dbConnectionInfoList.get(row).getDbConnectionString();
			break;
		}
		return tmp;
	}

	@Override
	public Class<?> getColumnClass(int col) {
		Class<?> clazz = Object.class;
		switch (col) {
		case 3:
			clazz = DBType.class;
			break;
		case 8:
		case 9:
			clazz = Integer.class;
			break;
		default:
			clazz = String.class;
		}
		return clazz;
	}
}