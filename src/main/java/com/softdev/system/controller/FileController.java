package com.softdev.system.controller;

import com.softdev.system.entity.FileInfo;
import com.softdev.system.entity.FileRequest;
import com.softdev.system.service.FileSystemService;
import com.softdev.system.service.PowerShellService;
import com.softdev.system.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class FileController {

    @Autowired
    FileSystemService fileSystemService;
    @Autowired
    PowerShellService powerShellService;

    @GetMapping("/file")
    public ModelAndView listFile() {
        return new ModelAndView("file");
    }
    @GetMapping("/vscode")
    public ModelAndView vscode(String filePath) {
        // 确保路径合法化
        Path normalizedPath = Paths.get(filePath).normalize();
        log.info("VSCode view requested for path: {}", normalizedPath);
        return new ModelAndView("vscode").addObject("filePath", normalizedPath.toString());
    }

    @GetMapping("/logViewer")
    public ModelAndView logViewer(String filePath, String fileNamePattern, String keyWord) {
        // 确保路径合法化
        Path normalizedPath = Paths.get(filePath).normalize();
        log.info("LogViewer requested for path: {}, fileNamePattern: {}, keyWord: {}", normalizedPath, fileNamePattern, keyWord);
        return new ModelAndView("logViewer")
                .addObject("filePath", normalizedPath.toString())
                .addObject("fileNamePattern", fileNamePattern)
                .addObject("keyWord", keyWord);
    }

    @PostMapping("/list")
    public Object listFile(@RequestBody FileRequest fileRequest) throws IOException {
        fileRequest.setExecutionType("list");
        fileRequest.setExecutionTime(new Date());

        // 确保路径合法化
        Path normalizedPath = Paths.get(fileRequest.getFilePath()).normalize();
        log.info("Listing files for path: {}", normalizedPath);

        return ResponseUtil.success(fileSystemService.listFiles(normalizedPath.toString()));
    }

    @PostMapping("/listPlus")
    public Object listFilePlus(@RequestBody FileRequest fileRequest) throws IOException {
        fileRequest.setExecutionType("list");
        fileRequest.setExecutionTime(new Date());

        // 确保路径合法化
        Path normalizedPath = Paths.get(fileRequest.getFilePath()).normalize();
        log.info("Listing files for path: {} ", fileRequest);

        // 获取所有文件
        List<FileInfo> allFiles = fileSystemService.listFiles(normalizedPath.toString());

        // 将通配符模式转换为正则表达式
        String fileNamePattern = fileRequest.getFileNamePattern().toLowerCase(Locale.ROOT).trim();

        // 过滤文件名称匹配fileNamePattern的文件
        List<FileInfo> filteredFiles = allFiles.stream()
                .filter(file -> file.getName().toLowerCase(Locale.ROOT).contains(fileNamePattern))
                .toList();

        // 进一步过滤包含keyWord关键字的文件
        List<FileInfo> resultFiles = new ArrayList<>();
        if(StringUtils.isNotBlank(fileRequest.getKeyWord())){
            List<FileInfo> finalResultFiles = new ArrayList<>();
            filteredFiles = filteredFiles.stream()
                    .filter(file -> {
                        Path filePath = Paths.get(fileRequest.getFilePath(), file.getName());
                        try {
                            String fileContent= FileUtils.readFileToString(new File(filePath.toString()), StandardCharsets.UTF_8).replaceAll("\\u0000","").replaceAll("\u0000","").replaceAll("�","");;
                            log.info("fileContent {}", fileContent);
                            if (fileContent.contains(fileRequest.getKeyWord().trim())) {
                                log.info("File {} contains keyWord {}", filePath, fileRequest.getKeyWord());
                                finalResultFiles.add(file);
                            }
//                            List<String> allLines=FileUtils.readLines(new File(filePath.toString()), "UTF-8");
//                            allLines=allLines.stream().filter(line->line.toLowerCase().contains(fileRequest.getKeyWord().trim().toLowerCase())).toList();
//                            if(!allLines.isEmpty()){
//                                finalResultFiles.add(file);
//                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return false;
                    })
                    .toList();
            resultFiles = finalResultFiles;
        }else {
            resultFiles = filteredFiles;
        }

        return ResponseUtil.success(resultFiles);
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestBody FileRequest fileRequest) {
        fileRequest.setExecutionType("download");
        fileRequest.setExecutionTime(new Date());

        String filePath = fileRequest.getFilePath().replaceAll("//","");
        log.info("FileDownloadRequest:{} {}",filePath, fileRequest);
        Path path = Paths.get(filePath).normalize();

        // 安全校验
//        if (!path.startsWith("C:\\allowed_directory")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }

        Resource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\""+ System.currentTimeMillis() +"_"+ resource.getFilename() + "\"")
                .body(resource);
    }

    @PostMapping("/read")
    public Object read(@RequestBody FileRequest fileRequest) throws IOException {
        fileRequest.setExecutionType("read");
        fileRequest.setExecutionTime(new Date());

        Path path = Paths.get(fileRequest.getFilePath()).normalize();
        log.info("FileReadRequest:{}", fileRequest);
        // 安全校验
//        if (!path.startsWith("C:\\allowed_directory")) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
        // 文件大小校验
        if (Files.size(path) > 10 * 1024 * 1024) {
            log.error("File exceeds 10MB limit :{} ", path );
            return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"File exceeds 10MB limit");
        }
        try {
            String contentType = Files.probeContentType(path);
            if(contentType == null && (
                fileRequest.getFilePath().toLowerCase().contains(".txt") ||
                fileRequest.getFilePath().toLowerCase().contains(".log") ||
                fileRequest.getFilePath().toLowerCase().contains(".json") ||
                fileRequest.getFilePath().toLowerCase().contains(".xml")
            )){
                //special file type
                log.info("File is a near text file: {} {}", path , null);
            }
            else if (contentType == null){
                log.error("File is a near text file: {} {}", path , null);
                return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"File is not a text file:"+null);
            }
            else if (!contentType.startsWith("text")) {
                log.error("File is not a text file: {} {}", path , contentType);
                return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"File is not a text file:"+contentType);
            }
            log.info("File type: {} {}", path , contentType);
        } catch (Exception e) {
            log.error("Error checking file type for path: {}", path, e);
            return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"Error checking file type for path");
        }
        String fileContent = null;
        try {
            fileContent = FileUtils.readFileToString(new File(path.toString()), "UTF-8");
            // 新增特殊字符转换:修复各种乱码问题
            fileContent = fileContent.replaceAll("\\u0000","").replaceAll("\u0000","").replaceAll("�","");
        } catch (IOException e) {
            log.error("Error", e.fillInStackTrace());
            return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"Error reading file :"+e.getMessage());
        }
        return ResponseUtil.success(fileContent);
    }
}
