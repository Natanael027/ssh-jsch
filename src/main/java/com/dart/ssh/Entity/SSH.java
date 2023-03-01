package com.dart.ssh.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ssh")
@Data
@NoArgsConstructor
public class SSH {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private Long status;
    private String ip;
    private String port;
    @Column(columnDefinition = "LONGTEXT")
    private String command;
    private String result;

    public SSH(Long status, String ip, String port, String command, String result) {
        this.status = status;
        this.ip = ip;
        this.port = port;
        this.command = command;
        this.result = result;
    }
}
