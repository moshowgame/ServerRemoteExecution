package com.softdev.system.service;

import com.softdev.system.entity.FileInfo;
import com.softdev.system.util.DateUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileSystemService {

    public List<FileInfo> listFiles(String path) throws IOException {
        Path dirPath = Paths.get(path).normalize().toAbsolutePath();

        // 验证路径合法性（示例校验）
//        if (!dirPath.startsWith("C:\\allowed_directory")) {
//            throw new AccessDeniedException("Forbidden path");
//        }

        try (Stream<Path> paths = Files.list(dirPath)) {
            return paths.map(p -> {
                FileInfo info = new FileInfo();
                info.setName(p.getFileName().toString());
                info.setPath(p.toString());
                info.setDirectory(Files.isDirectory(p));
                info.setSize(String.valueOf(info.getDirectory() ? 0 : p.toFile().length()));
                info.setLastModified(DateUtil.formatTimestamp(p.toFile().lastModified()));
                return info;
            }).toList().stream().sorted(Comparator.comparing(FileInfo::getLastModified, Comparator.reverseOrder())).toList();
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("Access Denied: "+dirPath);
        }
    }

    // FileInfo DTO省略
}