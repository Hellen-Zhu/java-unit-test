package com.example.service;

/**
 * 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送欢迎邮件
     */
    void sendWelcomeEmail(String email);

    /**
     * 发送更新通知
     */
    void sendUpdateNotification(String email);

    /**
     * 发送账户删除邮件
     */
    void sendAccountDeletionEmail(String email);
}