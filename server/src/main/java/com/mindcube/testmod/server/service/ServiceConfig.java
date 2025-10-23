package com.mindcube.testmod.server.service;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class ServiceConfig implements Serializable {
    private String host = "host";
    private int port = 5432;
    private String database = "database";
    private String user = "user";
    private String password = "pas";
}
