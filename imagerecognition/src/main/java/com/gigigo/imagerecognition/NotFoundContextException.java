package com.gigigo.imagerecognition;

public class NotFoundContextException extends RuntimeException {
  public NotFoundContextException(String message) {
    super(message);
  }

  public NotFoundContextException() {
    super("Context not provided, please call setContextProvider() method providing a "
        + "ContextProvider implementation");
  }
}