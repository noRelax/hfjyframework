package com.hfjy.framework.net.file;

import java.util.Map;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.exception.FileAccessException;

public interface FileAccess {

	public AccessResult<String> add(byte[] file, String name) throws FileAccessException;

	public AccessResult<String> add(byte[] file, String path, String name) throws FileAccessException;

	public AccessResult<String> add(byte[] file, String path, String name, FileType type) throws FileAccessException;

	public AccessResult<String> addTest(byte[] file, String name) throws FileAccessException;

	public AccessResult<Map<String, AccessResult<String>>> adds(Map<String, byte[]> files, String path, FileType type) throws FileAccessException;

	public AccessResult<byte[]> delete(String name) throws FileAccessException;

	public AccessResult<byte[]> delete(String path, String name) throws FileAccessException;

	public AccessResult<byte[]> delete(String path, String name, FileType type) throws FileAccessException;

	public AccessResult<Map<String, AccessResult<byte[]>>> deletes(String path, FileType type) throws FileAccessException;

	public AccessResult<byte[]> get(String name) throws FileAccessException;

	public AccessResult<byte[]> get(String path, String name) throws FileAccessException;

	public AccessResult<byte[]> get(String path, String name, FileType type) throws FileAccessException;

	public AccessResult<Map<String, AccessResult<byte[]>>> gets(String path, FileType type) throws FileAccessException;

	public AccessResult<String> update(byte[] file, String name) throws FileAccessException;

	public AccessResult<String> update(byte[] file, String path, String name) throws FileAccessException;

	public AccessResult<String> update(byte[] file, String path, String name, FileType type) throws FileAccessException;

	public AccessResult<Map<String, AccessResult<String>>> updates(Map<String, byte[]> file, String path, FileType type) throws FileAccessException;
}