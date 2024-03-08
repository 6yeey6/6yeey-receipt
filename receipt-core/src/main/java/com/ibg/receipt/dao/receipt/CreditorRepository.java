package com.ibg.receipt.dao.receipt;

import com.ibg.receipt.base.dao.BaseRepository;
import com.ibg.receipt.base.vo.CustomPageRequest;
import com.ibg.receipt.enums.business.CreditorEnum;
import com.ibg.receipt.model.receipt.Creditor;
import com.ibg.receipt.model.receipt.CreditorBaseConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;


public interface CreditorRepository extends BaseRepository<Creditor> {

    List<Creditor> findByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted);

    List<Creditor> findByDeleted(Boolean deleted);
    
    List<Creditor> findByCreditorAndCreditorConfigVersionAndDeleted(CreditorEnum creditor, String creditorConfigVersion, Boolean deleted);

    Page<Creditor> findPageByDeleted(Boolean deleted, Pageable pageable);

    Page<Creditor> findPageByCreditorAndDeleted(CreditorEnum creditor, Boolean deleted, Pageable pageable);

    List<Creditor> findByUserNameAndDeleted(String userName,Boolean deleted);

    List<Creditor> findByCreditorInAndDeleted(List<CreditorEnum>Creditors,Boolean deleted);
}
