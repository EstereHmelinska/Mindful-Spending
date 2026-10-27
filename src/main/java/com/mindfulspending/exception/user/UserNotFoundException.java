package com.mindfulspending.exception.user;

public class UserNotFoundException extends RuntimeException {
  public UserNotFoundException() {super("User not found");
  }
}
