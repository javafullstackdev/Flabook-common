package com.msquare.flabook.common.configurations;


import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;

public class FlabookPasswordEncoder {

    private FlabookPasswordEncoder() throws IllegalAccessException {
        throw new IllegalAccessException("FlabookPasswordEncoder is static");
    }

    public static String hashedPassword(String plainTextPassword, String passwordSalt) {
        if (plainTextPassword == null  || passwordSalt == null) {
            throw new IllegalArgumentException("Bad password or passwordSalt!");
        }
        return new Sha256Hash(plainTextPassword, ByteSource.Util.bytes(passwordSalt), 1024).toBase64();
    }

}
