package com.samuilolegovich.domain;

public enum Role {
    ROLE_GAMER,
    ROLE_ADMIN,
    ROLE_OWNER;

    // возможно где-то понадобится убрать префикс
    public String excludePrefix() {
        return this.toString().substring("ROLE_".length());
    }
}
