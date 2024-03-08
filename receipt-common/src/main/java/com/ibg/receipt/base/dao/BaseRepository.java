package com.ibg.receipt.base.dao;

import com.ibg.receipt.base.model.BaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends BaseModel> extends JpaRepository<T, Long> {
            default  int updateBankCardNo(Long id, String encryptBankCardNo){
                return 0;
            }
}
