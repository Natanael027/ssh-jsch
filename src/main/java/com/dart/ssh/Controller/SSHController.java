package com.dart.ssh.Controller;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.Service.ServiceSSH;
import com.dart.ssh.Service.ServiceShell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SSHController {
    @Autowired
    ServiceShell serviceShell;

    @Autowired
    ServiceSSH serviceSSH;

    @GetMapping(value = {"/", "/home"})
    public String home(Model map, @Param("keyword") String keyword) {
        List<SSH> results = serviceSSH.findAll();

        map.addAttribute("keyword", keyword);
        map.addAttribute("result", results);

        return "index";
    }



}
