// reviewed on 2024.07.14

package com.luv2code.spring_boot_library.dao;

import com.luv2code.spring_boot_library.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.RequestParam;

public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> findBookByUserEmail(@RequestParam("email") String userEmail, Pageable pageable);
}
