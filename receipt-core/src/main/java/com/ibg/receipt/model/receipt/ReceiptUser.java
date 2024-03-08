package com.ibg.receipt.model.receipt;

import com.ibg.receipt.base.model.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/23 14:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "receipt_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptUser extends BaseModel {

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "is_export")
    private Byte isExport;

    @Column(name = "is_white")
    private Byte isWhite;

}
