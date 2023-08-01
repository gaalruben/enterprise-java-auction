package org.learning.auction.persistence;

public class InvalidReferenceException extends RuntimeException{
    public InvalidReferenceException(String message){
        super(message);
    }
}
