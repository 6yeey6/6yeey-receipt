package com.ibg.receipt.shiro;

import org.apache.shiro.authc.credential.PasswordService;
import org.jasypt.util.password.rfc2307.RFC2307MD5PasswordEncryptor;

/**
 *
 * @author wanghongbo01
 * @date 2022/8/29 20:24
 */
public class ReceiptPasswordService implements PasswordService  {

    private RFC2307MD5PasswordEncryptor ENCRYPTOR = new RFC2307MD5PasswordEncryptor();

    @Override
    public String encryptPassword(Object plaintextPassword)
            throws IllegalArgumentException {
        return ENCRYPTOR.encryptPassword((String) plaintextPassword);
    }

    @Override
    public boolean passwordsMatch(Object submittedPlaintext, String encrypted) {
        if(submittedPlaintext != null) {
            return ENCRYPTOR.checkPassword(new String((char[]) submittedPlaintext),
                           encrypted);
        }
        return true;
    }

    public static void main(String[] args) {
        RFC2307MD5PasswordEncryptor ENCRYPTOR = new RFC2307MD5PasswordEncryptor();
        System.out.println( ENCRYPTOR.encryptPassword("QprJitIUtY"));
    }
}



