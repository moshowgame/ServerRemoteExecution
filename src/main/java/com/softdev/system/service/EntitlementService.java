package com.softdev.system.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
@Slf4j
@Service
public class EntitlementService {
    private final Map<String, String> userMap = new HashMap<>();

    public EntitlementService() {
        try {
            // 从资源文件夹中读取 users.csv
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream("/data/entitlement.csv")), StandardCharsets.UTF_8)
            );

            String line;
            reader.readLine(); // 跳过表头
            while ((line = reader.readLine()) != null) {
                // 按照逗号分割每一行
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    String username = tokens[0].trim();
                    String password = tokens[1].trim();
                    userMap.put(username, password);
                }
            }
            reader.close();

            log.info("EntitlementService initialized successfully. total {}",userMap.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String username, String password) {
        // 验证用户名和密码是否匹配
        return password.equals(userMap.get(username));
    }
}
