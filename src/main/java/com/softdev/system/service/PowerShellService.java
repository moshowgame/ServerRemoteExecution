package com.softdev.system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class PowerShellService {

    public String executeCommand(String command, String encoding) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "powershell.exe", "-Command", command
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), encoding))) { // 使用用户选择的编码
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
//        log.info("output: " + output);
        return "Exit Code: " + exitCode + "\nOutput:\n" + output;
    }
}