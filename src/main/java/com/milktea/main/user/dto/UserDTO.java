package com.milktea.main.user.dto;

public record UserDTO(String username,
                      String email,
                      String bio,
                      String image,
                      String password) {}
