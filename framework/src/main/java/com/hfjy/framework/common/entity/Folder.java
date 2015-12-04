package com.hfjy.framework.common.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Folder implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private String path;
	private String name;
	private List<Folder> folders = new ArrayList<>();
	private List<FileInfo> fileInfos = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public void setFolders(List<Folder> folders) {
		this.folders = folders;
	}

	public List<FileInfo> getFileInfos() {
		return fileInfos;
	}

	public void setFileInfos(List<FileInfo> fileInfos) {
		this.fileInfos = fileInfos;
	}

	public void addFile(FileInfo fileInfo) {
		fileInfos.add(fileInfo);
	}

	public void addFolder(Folder folder) {
		folders.add(folder);
	}

	@Override
	public Folder clone() throws CloneNotSupportedException {
		Folder folder = (Folder) super.clone();
		folder.fileInfos = new ArrayList<FileInfo>(fileInfos);
		folder.folders = new ArrayList<Folder>(folders);
		return folder;
	}
}
