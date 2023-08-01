package org.learning.auction.service;


public class AuthenticationException extends RuntimeException{
    public AuthenticationException(String message){
        super(message);
    }
}
