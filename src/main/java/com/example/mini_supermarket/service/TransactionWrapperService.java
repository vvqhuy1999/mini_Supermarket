package com.example.mini_supermarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.dao.DataAccessException;

import java.util.function.Supplier;

/**
 * Service wrapper để xử lý transaction errors và rollback tự động
 */
@Service
public class TransactionWrapperService {
    
    /**
     * Thực hiện operation trong transaction với rollback tự động khi có lỗi
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public <T> T executeInNewTransaction(Supplier<T> operation) throws Exception {
        try {
            return operation.get();
        } catch (JpaSystemException e) {
            // Log transaction error
            System.err.println("🚨 Transaction Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("🚨 Root Cause: " + e.getCause().getMessage());
            }
            throw e;
        } catch (DataAccessException e) {
            System.err.println("🚨 Data Access Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("🚨 General Error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Thực hiện operation trong transaction với rollback khi có lỗi
     */
    @Transactional(rollbackFor = Exception.class)
    public <T> T executeInTransaction(Supplier<T> operation) throws Exception {
        try {
            return operation.get();
        } catch (Exception e) {
            System.err.println("🚨 Transaction Error: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Thực hiện operation không có transaction (để debug)
     */
    public <T> T executeWithoutTransaction(Supplier<T> operation) throws Exception {
        try {
            return operation.get();
        } catch (Exception e) {
            System.err.println("🚨 Non-Transaction Error: " + e.getMessage());
            throw e;
        }
    }
}
