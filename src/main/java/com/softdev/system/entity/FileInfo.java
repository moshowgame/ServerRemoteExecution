package com.softdev.system.entity;

import lombok.Data;

@Data
public class FileInfo {
    String name;
    String path;
    Boolean directory;
    String size;
    String lastModified;
}
