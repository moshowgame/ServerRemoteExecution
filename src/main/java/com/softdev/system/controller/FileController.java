package com.softdev.system.controller;

import com.softdev.system.entity.FileInfo;
import com.softdev.system.entity.FileRequest;
import com.softdev.system.service.FileSystemService;
import com.softdev.system.service.PowerShellService;
import com.softdev.system.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

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
        return new ModelAndView("vscode").addObject("filePath",filePath);
    }

    @PostMapping("/list")
    public ResponseEntity<List<FileInfo>> listFile(@RequestBody FileRequest fileRequest) throws IOException {
        fileRequest.setExecutionType("list");
        fileRequest.setExecutionTime(new Date());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,"application/json")
                .body(fileSystemService.listFiles(fileRequest.getFilePath()));
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
            if(contentType == null && (fileRequest.getFilePath().toLowerCase().contains(".txt")||fileRequest.getFilePath().toLowerCase().contains(".log"))){
                //special file type
                log.info("File is a near text file: {} {}", path , null);
            }
            else if (contentType == null){
                log.error("File is a near text file: {} {}", path , null);
                return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"File is not a text file:"+null);
            }
            else if (contentType != null && !contentType.startsWith("text")) {
                log.error("File is not a text file: {} {}", path , contentType);
                return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"File is not a text file:"+contentType);
            }
            log.info("File type: {} {}", path , contentType);
        } catch (IOException e) {
            log.error("Error checking file type for path: {}", path, e);
            return ResponseUtil.fail(ResponseUtil.StatusCode.INTERNAL_ERROR,"Error checking file type for path");
        }
        return ResponseUtil.success(Files.readString(path));
    }
}
