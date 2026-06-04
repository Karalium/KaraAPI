package org.kerix.karaapi.api.recipe;

public final class RecipeException extends RuntimeException {

  public RecipeException(String message) {
    super(message);
  }

  public RecipeException(String message, Throwable cause) {
    super(message, cause);
  }
}
