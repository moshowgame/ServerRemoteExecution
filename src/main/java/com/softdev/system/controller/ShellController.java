package com.softdev.system.controller;

import com.softdev.system.entity.ShellRequest;
import com.softdev.system.service.PowerShellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Date;

@Slf4j
@RestController
public class ShellController {

    @Autowired
    PowerShellService powerShellService;

    @GetMapping("/shell")
    public ModelAndView shell() {
        return new ModelAndView("shell");
    }

    @PostMapping("execute")
    public String execute(@RequestBody ShellRequest shellRequest) throws IOException, InterruptedException {
        shellRequest.setExecutionTime(new Date());
        log.info("Shell Execution：{}", shellRequest);
        return powerShellService.executeCommand(shellRequest.getCommand());
    }
}
