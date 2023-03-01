package com.dart.ssh.repository;

import com.dart.ssh.Entity.SSH;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@EnableJpaRepositories
public interface SshRepository extends JpaRepository<SSH, Long> {
    @Query("SELECT u FROM SSH u where u.id= :id1 OR u.id= :id2 OR u.id= :id3")
    List<SSH> findStatus0(@Param("id") Long id, @Param("id2") Long id2, @Param("id3") Long id3);

    @Query("SELECT u FROM SSH u WHERE u.id=:id")
    SSH findByIds(@Param("id") Long id);
}
