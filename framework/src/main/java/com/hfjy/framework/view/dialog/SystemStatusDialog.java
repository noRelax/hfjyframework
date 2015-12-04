package com.hfjy.framework.view.dialog;

import java.awt.GridLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import com.hfjy.framework.init.Initial;
import com.hfjy.framework.view.factory.SwingContainerFactory;

public class SystemStatusDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private JPanel jpSystemInfo;
	private JScrollPane jspSystemInfo;

	public SystemStatusDialog(JFrame mainWin) {
		super(mainWin, true);
		buildWin();
		loadEvent();
		this.setSize((int) (mainWin.getWidth() * 0.8), (int) (mainWin.getHeight() * 0.8));
		this.setTitle("系统状态");
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
	}

	private void buildWin() {
		List<String[]> infoList = getSystemInfoList();
		jpSystemInfo = new JPanel(new GridLayout(infoList.size(), 2, 5, 5));
		jspSystemInfo = new JScrollPane(jpSystemInfo);
		for (int i = 0; i < infoList.size(); i++) {
			jpSystemInfo.add(SwingContainerFactory.getJLabel(infoList.get(i)[0], SwingConstants.LEFT));
			jpSystemInfo.add(SwingContainerFactory.getJLabel(infoList.get(i)[1], SwingConstants.LEFT));
		}
		this.add(jspSystemInfo);
	}

	private void loadEvent() {

	}

	private List<String[]> getSystemInfoList() {
		List<String[]> infoList = new ArrayList<>();
		Field[] fields = Initial.class.getFields();
		Initial init = new Initial();
		try {
			for (int i = 0; i < fields.length; i++) {
				infoList.add(new String[] { fields[i].getName() + "：", fields[i].get(init).toString() });
			}
		} catch (Exception e) {
		}
		return infoList;
	}
}