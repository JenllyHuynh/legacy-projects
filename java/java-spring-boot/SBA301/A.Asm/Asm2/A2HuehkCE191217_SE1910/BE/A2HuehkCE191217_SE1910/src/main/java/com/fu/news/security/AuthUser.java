package com.fu.news.security;

/** Principal được đặt vào SecurityContext sau khi xác thực JWT. */
public record AuthUser(Integer accountId, String email, Integer role) {}
