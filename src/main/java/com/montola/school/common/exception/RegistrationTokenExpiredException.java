package com.montola.school.common.exception;

/**
 * @author avidewan
 * @date 8/27/25
 */
public class RegistrationTokenExpiredException extends RuntimeException {

  public RegistrationTokenExpiredException() {
    super("registration.token.expired");
  }
}