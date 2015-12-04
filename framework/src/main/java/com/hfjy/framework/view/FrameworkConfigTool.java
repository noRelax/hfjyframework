package com.hfjy.framework.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import com.hfjy.framework.view.dialog.SystemConfigDialog;
import com.hfjy.framework.view.dialog.SystemStatusDialog;
import com.hfjy.framework.view.factory.SwingContainerFactory;
import com.hfjy.framework.view.panel.CacheViewPanel;
import com.hfjy.framework.view.panel.DBConnectionInfoPanel;
import com.hfjy.framework.view.panel.DatabBaseViewPanel;

public class FrameworkConfigTool extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private DBConnectionInfoPanel dbInfoPanel;
	private DatabBaseViewPanel dbViewPanel;
	private CacheViewPanel cacheViewPanel;
	private JTabbedPane jtpModule;
	private JMenuBar jmbMenu;
	private JMenu jmTool, jmInfo, jmHelp;
	private JMenuItem jmiAbout, jmiSysConfig, jmiSysState, jmiDBInfo, jmiDBView, jmiCacheView;

	public FrameworkConfigTool() {
		buildMenu();
		buildWin();
		loadEvent();
		this.setTitle("Framework 配置工具\t1.0");
		Dimension winSize = new Dimension(800, 500);
		this.setSize(winSize);
		this.setMinimumSize(winSize);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		// this.setResizable(false);
		this.setVisible(true);
	}

	private void buildMenu() {
		this.jmbMenu = new JMenuBar();
		this.jmTool = new JMenu("常用工具");
		this.jmTool.setFont(SwingContainerFactory.getFont());
		this.jmInfo = new JMenu("框架信息");
		this.jmInfo.setFont(SwingContainerFactory.getFont());
		this.jmHelp = new JMenu("帮助");
		this.jmHelp.setFont(SwingContainerFactory.getFont());
		this.jmiDBInfo = new JMenuItem("数据库信息");
		this.jmiDBInfo.setFont(SwingContainerFactory.getFont());
		this.jmiDBView = new JMenuItem("数据库查看");
		this.jmiDBView.setFont(SwingContainerFactory.getFont());
		this.jmiCacheView = new JMenuItem("Redis缓存查看");
		this.jmiCacheView.setFont(SwingContainerFactory.getFont());
		this.jmiSysConfig = new JMenuItem("系统配置");
		this.jmiSysConfig.setFont(SwingContainerFactory.getFont());
		this.jmiSysState = new JMenuItem("系统状态");
		this.jmiSysState.setFont(SwingContainerFactory.getFont());
		this.jmiAbout = new JMenuItem("关于");
		this.jmiAbout.setFont(SwingContainerFactory.getFont());
		this.jmbMenu.add(jmTool);
		this.jmbMenu.add(jmInfo);
		this.jmbMenu.add(jmHelp);
		this.jmTool.add(jmiDBInfo);
		this.jmTool.add(jmiDBView);
		this.jmTool.add(jmiCacheView);
		this.jmInfo.add(jmiSysConfig);
		this.jmInfo.add(jmiSysState);
		this.jmHelp.add(jmiAbout);
		this.jmbMenu.setVisible(true);
		this.add(jmbMenu, BorderLayout.NORTH);
	}

	private void buildWin() {
		jtpModule = new JTabbedPane();
		jtpModule.setFont(SwingContainerFactory.getFont());
		this.add(jtpModule, BorderLayout.CENTER);
	}

	private void loadEvent() {
		jmiAbout.addActionListener(this);
		jmiSysConfig.addActionListener(this);
		jmiSysState.addActionListener(this);
		jmiDBInfo.addActionListener(this);
		jmiDBView.addActionListener(this);
		jmiCacheView.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == jmiAbout) {
			JOptionPane.showMessageDialog(this, "Framework 配置工具\n版本 1.0\n(C) 刘 越\nCell：15000164380\n保留所有权利", "关于 Framework", JOptionPane.INFORMATION_MESSAGE);
		} else if (source == jmiDBInfo) {
			if (!jtpModule.isAncestorOf(dbInfoPanel)) {
				dbInfoPanel = new DBConnectionInfoPanel();
				jtpModule.add(dbInfoPanel, "数据库信息");
			}
		} else if (source == jmiDBView) {
			if (!jtpModule.isAncestorOf(dbViewPanel)) {
				dbViewPanel = new DatabBaseViewPanel();
				jtpModule.add(dbViewPanel, "数据库查看");
			}
		} else if (source == jmiCacheView) {
			if (!jtpModule.isAncestorOf(cacheViewPanel)) {
				cacheViewPanel = new CacheViewPanel();
				jtpModule.add(cacheViewPanel, "Redis缓存查看");
			}
		} else if (source == jmiSysConfig) {
			new SystemConfigDialog(this);
		} else if (source == jmiSysState) {
			new SystemStatusDialog(this);
		}
	}
}