package org.learning.auction.service;

public class NotEnoughBalanceException extends RuntimeException{
    public NotEnoughBalanceException(String message){
        super(message);
    }
}
