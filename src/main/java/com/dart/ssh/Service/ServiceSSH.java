package com.dart.ssh.Service;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.repository.SshRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceSSH {

    @Autowired
    SshRepository sshRepository;

    // CREATE
    public SSH save(SSH ssh){
        return sshRepository.save(ssh);
    }

    // READ
    public List<SSH> findAll() {
        return sshRepository.findAll();
    }

    // DELETE
    public void deleteById(Long empId) {
        sshRepository.deleteById(empId);
    }
}
